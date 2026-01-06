"""
Chatbot service with Gemini AI integration.
"""
import re
from google import genai
from config import Config
from chatbot.prompts import SYSTEM_PROMPT, build_context_prompt
from database.queries import (
    get_tours_summary, search_tours, get_booking_by_id, get_payment_status,
    get_bookings_by_email, get_bookings_by_phone, get_payments_by_email
)


# Initialize Gemini client
client = genai.Client(api_key=Config.GEMINI_API_KEY)
MODEL = "gemini-2.5-flash"


def detect_intent(message):
    """
    Simple intent detection based on keywords.
    Returns tuple: (intent, extracted_params)
    """
    message_lower = message.lower()
    
    # 1. Email lookup (Highest priority if email is present)
    email_match = re.search(r'[\w\.-]+@[\w\.-]+\.\w+', message)
    if email_match:
        email = email_match.group()
        # Check context keywords
        if any(kw in message_lower for kw in ['thanh toán', 'payment', 'tiền', 'hóa đơn']):
             return ('payment_lookup_by_email', {'email': email})
        return ('booking_lookup_by_email', {'email': email})

    # 2. Phone lookup
    phone_match = re.search(r'(0[3|5|7|8|9])+([0-9]{8})\b', message) # Simple regex for VN phone
    if not phone_match:
         # Try simpler 10-digit number if user omits leading zero or format is different
         phone_match = re.search(r'\b\d{10}\b', message)
         
    if phone_match:
        phone = phone_match.group()
        return ('booking_lookup_by_phone', {'phone': phone})

    # 3. Booking ID lookup (UUID)
    # Try to extract booking ID (UUID format)
    uuid_match = re.search(r'[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}', message, re.IGNORECASE)
    if uuid_match:
        booking_id = uuid_match.group()
        if any(kw in message_lower for kw in ['thanh toán', 'payment', 'trả tiền']):
            return ('payment_status', {'booking_id': booking_id})
        return ('booking_lookup', {'booking_id': booking_id})
    
    # 4. Tour search intent
    destinations = ['đà nẵng', 'hà nội', 'hồ chí minh', 'sài gòn', 'phú quốc', 'nha trang', 
                   'đà lạt', 'huế', 'hội an', 'sapa', 'hạ long', 'quy nhơn', 'phan thiết']
    
    for dest in destinations:
        if dest in message_lower:
            return ('tour_search', {'destination': dest})
    
    # 5. General tour listing
    if any(kw in message_lower for kw in ['tour', 'du lịch', 'chuyến đi', 'điểm đến', 'xem tour', 'có tour']):
        return ('tour_list', {})
    
    # Default: general chat
    return ('general', {})


def get_context_data(intent, params):
    """Fetch relevant data from database based on intent."""
    if intent == 'tour_list':
        return {'tours_data': get_tours_summary(limit=5)}
    
    elif intent == 'tour_search':
        destination = params.get('destination')
        return {'tours_data': search_tours(destination=destination, limit=5)}
    
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

    elif intent == 'payment_lookup_by_email':
        email = params.get('email')
        if email:
            return {'payment_data': get_payments_by_email(email)}
    
    elif intent == 'payment_status':
        booking_id = params.get('booking_id')
        if booking_id:
            return {
                'booking_data': get_booking_by_id(booking_id),
                'payment_data': get_payment_status(booking_id)
            }
    
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
