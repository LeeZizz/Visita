"""
Chatbot service with Gemini AI integration.
Fully aligned with Java backend capabilities.
"""
import re
from google import genai
from config import Config
from chatbot.prompts import SYSTEM_PROMPT, build_context_prompt
from database.queries import (
    get_tours_summary, search_tours, get_tour_details, get_booking_by_id,
    get_bookings_by_email, get_bookings_by_phone
)


# Initialize Gemini client
client = genai.Client(api_key=Config.GEMINI_API_KEY)
MODEL = "gemini-2.5-flash"


def extract_price_from_message(message):
    """Extract price values from message."""
    message_lower = message.lower()
    
    # Look for price patterns like "dưới 5 triệu", "từ 2-5 triệu", "tối đa 3tr"
    min_price = None
    max_price = None
    
    # Pattern: dưới X triệu/tr
    under_match = re.search(r'dưới\s*(\d+(?:\.\d+)?)\s*(?:triệu|tr)', message_lower)
    if under_match:
        max_price = float(under_match.group(1)) * 1_000_000
    
    # Pattern: trên X triệu/tr
    over_match = re.search(r'trên\s*(\d+(?:\.\d+)?)\s*(?:triệu|tr)', message_lower)
    if over_match:
        min_price = float(over_match.group(1)) * 1_000_000
    
    # Pattern: từ X đến Y triệu
    range_match = re.search(r'từ\s*(\d+(?:\.\d+)?)\s*(?:đến|-)\s*(\d+(?:\.\d+)?)\s*(?:triệu|tr)', message_lower)
    if range_match:
        min_price = float(range_match.group(1)) * 1_000_000
        max_price = float(range_match.group(2)) * 1_000_000
    
    # Pattern: tối đa X triệu
    max_match = re.search(r'tối đa\s*(\d+(?:\.\d+)?)\s*(?:triệu|tr)', message_lower)
    if max_match:
        max_price = float(max_match.group(1)) * 1_000_000
    
    return min_price, max_price


def extract_guest_count(message):
    """Extract number of adults and children from message."""
    message_lower = message.lower()
    num_adults = None
    num_children = None
    
    # Pattern: X người lớn
    adults_match = re.search(r'(\d+)\s*(?:người lớn|adult)', message_lower)
    if adults_match:
        num_adults = int(adults_match.group(1))
    
    # Pattern: X trẻ em
    children_match = re.search(r'(\d+)\s*(?:trẻ em|trẻ|child)', message_lower)
    if children_match:
        num_children = int(children_match.group(1))
    
    # Pattern: X người (general)
    if not num_adults:
        people_match = re.search(r'(\d+)\s*người(?!\s*lớn)', message_lower)
        if people_match:
            num_adults = int(people_match.group(1))
    
    return num_adults, num_children


def detect_intent(message):
    """
    Enhanced intent detection with full backend filter support.
    Returns tuple: (intent, extracted_params)
    """
    message_lower = message.lower()
    params = {}
    
    # 1. Email lookup (Highest priority if email is present)
    email_match = re.search(r'[\w\.-]+@[\w\.-]+\.\w+', message)
    if email_match:
        email = email_match.group()
        return ('booking_lookup_by_email', {'email': email})

    # 2. Phone lookup
    phone_match = re.search(r'(0[3|5|7|8|9])+([0-9]{8})\b', message)
    if not phone_match:
        phone_match = re.search(r'\b\d{10}\b', message)
         
    if phone_match:
        phone = phone_match.group()
        return ('booking_lookup_by_phone', {'phone': phone})

    # 3. Booking ID lookup (UUID)
    uuid_match = re.search(r'[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}', message, re.IGNORECASE)
    if uuid_match:
        booking_id = uuid_match.group()
        return ('booking_lookup', {'booking_id': booking_id})
    
    # 4. Tour detail lookup (asking about specific tour)
    if any(kw in message_lower for kw in ['chi tiết tour', 'lịch trình tour', 'thông tin tour', 'mô tả tour']):
        # Try to extract tour name or ID
        return ('tour_detail', params)
    
    # 5. Tour search with filters
    # Check for region
    regions = {
        'miền bắc': 'NORTH', 'bắc': 'NORTH',
        'miền trung': 'CENTRAL', 'trung': 'CENTRAL', 
        'miền nam': 'SOUTH', 'nam': 'SOUTH'
    }
    for region_vn, region_code in regions.items():
        if region_vn in message_lower:
            params['region'] = region_code
            break
    
    # Check for category
    categories = {
        'phiêu lưu': 'ADVENTURE', 'mạo hiểm': 'ADVENTURE',
        'văn hóa': 'CULTURAL', 'lịch sử': 'CULTURAL',
        'biển': 'BEACH', 'bãi biển': 'BEACH',
        'núi': 'MOUNTAIN', 'leo núi': 'MOUNTAIN',
        'thành phố': 'CITY', 'city': 'CITY',
        'sinh thái': 'ECOTOURISM', 'eco': 'ECOTOURISM',
        'ẩm thực': 'FOOD', 'ăn uống': 'FOOD',
        'gia đình': 'FAMILY', 'family': 'FAMILY'
    }
    for cat_vn, cat_code in categories.items():
        if cat_vn in message_lower:
            params['category'] = cat_code
            break
    
    # Check for destination
    destinations = ['đà nẵng', 'hà nội', 'hồ chí minh', 'sài gòn', 'phú quốc', 'nha trang', 
                   'đà lạt', 'huế', 'hội an', 'sapa', 'sa pa', 'hạ long', 'quy nhơn', 
                   'phan thiết', 'mũi né', 'cần thơ', 'côn đảo', 'phong nha', 'ninh bình',
                   'vũng tàu', 'cát bà', 'tam đảo', 'bà nà', 'fansipan']
    
    for dest in destinations:
        if dest in message_lower:
            params['destination'] = dest
            break
    
    # Check for price range
    min_price, max_price = extract_price_from_message(message)
    if min_price:
        params['min_price'] = min_price
    if max_price:
        params['max_price'] = max_price
    
    # Check for guest count
    num_adults, num_children = extract_guest_count(message)
    if num_adults:
        params['num_adults'] = num_adults
    if num_children:
        params['num_children'] = num_children
    
    # Check for rating filter
    rating_match = re.search(r'(?:đánh giá|rating|sao)\s*(?:từ|trên|>=?)?\s*(\d(?:\.\d)?)', message_lower)
    if rating_match:
        params['min_rating'] = float(rating_match.group(1))
    
    # Determine intent based on collected params
    if params:
        return ('tour_search', params)
    
    # 6. General tour listing
    if any(kw in message_lower for kw in ['tour', 'du lịch', 'chuyến đi', 'điểm đến', 'xem tour', 'có tour', 'gợi ý']):
        return ('tour_list', {})
    
    # Default: general chat
    return ('general', {})


def get_context_data(intent, params):
    """Fetch relevant data from database based on intent."""
    if intent == 'tour_list':
        return {'tours_data': get_tours_summary(limit=5)}
    
    elif intent == 'tour_search':
        return {'tours_data': search_tours(
            destination=params.get('destination'),
            region=params.get('region'),
            category=params.get('category'),
            min_price=params.get('min_price'),
            max_price=params.get('max_price'),
            min_rating=params.get('min_rating'),
            num_adults=params.get('num_adults'),
            num_children=params.get('num_children'),
            limit=5
        )}
    
    elif intent == 'tour_detail':
        tour_id = params.get('tour_id')
        if tour_id:
            return {'tours_data': get_tour_details(tour_id)}
        return {'tours_data': get_tours_summary(limit=3)}
    
    elif intent == 'booking_lookup':
        booking_id = params.get('booking_id')
        if booking_id:
            return {'booking_data': get_booking_by_id(booking_id)}
    
    elif intent == 'booking_lookup_by_email':
        email = params.get('email')
        if email:
            return {'booking_data': get_bookings_by_email(email)}
            
    elif intent == 'booking_lookup_by_phone':
        phone = params.get('phone')
        if phone:
            return {'booking_data': get_bookings_by_phone(phone)}
    
    return None


def chat(message, history=None):
    """
    Process a chat message and return AI response.
    
    Args:
        message: User's current message
        history: List of previous messages [{"role": "user"|"assistant", "content": "..."}]
    
    Returns:
        str: AI response text
    """
    if history is None:
        history = []
    
    try:
        # Detect user intent and get relevant data
        intent, params = detect_intent(message)
        context_data = get_context_data(intent, params)
        
        # Build conversation contents for Gemini
        contents = []
        
        # Add conversation history
        for msg in history:
            role = "user" if msg.get("role") == "user" else "model"
            contents.append({
                "role": role,
                "parts": [{"text": msg.get("content", "")}]
            })
        
        # Build current user message with context
        user_message = message
        if context_data:
            context_text = build_context_prompt(**context_data)
            if context_text:
                user_message = f"{message}\n\n{context_text}"
        
        contents.append({
            "role": "user",
            "parts": [{"text": user_message}]
        })
        
        # Call Gemini API
        response = client.models.generate_content(
            model=MODEL,
            contents=contents,
            config={
                "system_instruction": SYSTEM_PROMPT,
                "temperature": 0.7,
                "max_output_tokens": 1024,
            }
        )
        
        return response.text
        
    except Exception as e:
        print(f"Error in chat service: {e}")
        raise e
