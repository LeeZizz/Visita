"""
Database query helpers for chatbot.
Fully aligned with Java backend entities.
"""
import pymysql
from config import Config


def get_db_connection():
    """Create database connection."""
    return pymysql.connect(
        host=Config.DB_HOST,
        user=Config.DB_USERNAME,
        password=Config.DB_PASSWORD,
        database=Config.DB_NAME,
        port=int(Config.DB_PORT),
        cursorclass=pymysql.cursors.DictCursor
    )


def get_tours_summary(limit=10):
    """Get summary of active tours with all backend fields."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT t.tour_id, t.title, t.description, t.itinerary,
                       t.destination, t.duration, t.region, t.category,
                       t.price_adult, t.price_child, t.capacity, t.availability,
                       t.start_date, t.end_date,
                       COALESCE(AVG(r.rating), 0) as average_rating,
                       COUNT(r.review_id) as review_count
                FROM tours t
                LEFT JOIN reviews r ON t.tour_id = r.tour_id
                WHERE t.is_active = 1 
                GROUP BY t.tour_id
                ORDER BY t.start_date ASC
                LIMIT %s
            """
            cursor.execute(query, (limit,))
            tours = cursor.fetchall()
        conn.close()
        return format_tours_for_display(tours)
    except Exception as e:
        print(f"Error getting tours summary: {e}")
        return None


def search_tours(destination=None, region=None, category=None, 
                 min_price=None, max_price=None, min_rating=None,
                 start_date_from=None, end_date_to=None,
                 num_adults=None, num_children=None, limit=5):
    """Search tours with full backend filter support."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT t.tour_id, t.title, t.description, t.itinerary,
                       t.destination, t.duration, t.region, t.category,
                       t.price_adult, t.price_child, t.capacity, t.availability,
                       t.start_date, t.end_date,
                       COALESCE(AVG(r.rating), 0) as average_rating,
                       COUNT(r.review_id) as review_count
                FROM tours t
                LEFT JOIN reviews r ON t.tour_id = r.tour_id
                WHERE t.is_active = 1
            """
            params = []
            
            if destination:
                query += " AND t.destination LIKE %s"
                params.append(f"%{destination}%")
            
            if region:
                query += " AND t.region = %s"
                params.append(region.upper())
            
            if category:
                query += " AND t.category = %s"
                params.append(category.upper())
            
            if min_price:
                query += " AND t.price_adult >= %s"
                params.append(min_price)
            
            if max_price:
                query += " AND t.price_adult <= %s"
                params.append(max_price)
            
            if start_date_from:
                query += " AND t.start_date >= %s"
                params.append(start_date_from)
            
            if end_date_to:
                query += " AND t.end_date <= %s"
                params.append(end_date_to)
            
            # Check availability for group size
            if num_adults or num_children:
                total_guests = (num_adults or 0) + (num_children or 0)
                if total_guests > 0:
                    query += " AND t.availability >= %s"
                    params.append(total_guests)
            
            query += " GROUP BY t.tour_id"
            
            if min_rating:
                query += " HAVING average_rating >= %s"
                params.append(min_rating)
            
            query += " ORDER BY t.price_adult ASC LIMIT %s"
            params.append(limit)
            
            cursor.execute(query, tuple(params))
            tours = cursor.fetchall()
        conn.close()
        return format_tours_for_display(tours)
    except Exception as e:
        print(f"Error searching tours: {e}")
        return None


def get_tour_details(tour_id):
    """Get full tour details including itinerary and images."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            # Get tour with ratings
            query = """
                SELECT t.tour_id, t.title, t.description, t.itinerary,
                       t.destination, t.duration, t.region, t.category,
                       t.price_adult, t.price_child, t.capacity, t.availability,
                       t.start_date, t.end_date,
                       COALESCE(AVG(r.rating), 0) as average_rating,
                       COUNT(r.review_id) as review_count
                FROM tours t
                LEFT JOIN reviews r ON t.tour_id = r.tour_id
                WHERE t.tour_id = %s AND t.is_active = 1
                GROUP BY t.tour_id
            """
            cursor.execute(query, (tour_id,))
            tour = cursor.fetchone()
            
            if tour:
                # Get tour images
                cursor.execute(
                    "SELECT image_url FROM tour_images WHERE tour_id = %s ORDER BY display_order",
                    (tour_id,)
                )
                images = cursor.fetchall()
                tour['images'] = [img['image_url'] for img in images]
        
        conn.close()
        
        if tour:
            return format_tour_detail_for_display(tour)
        return None
    except Exception as e:
        print(f"Error getting tour details: {e}")
        return None


def get_booking_by_id(booking_id):
    """Get booking details by ID with full backend fields."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT b.booking_id, b.booking_date, b.num_adults, b.num_children,
                       b.total_price, b.status, b.special_request, b.phone,
                       t.title as tour_title, t.destination, t.start_date, t.end_date,
                       t.duration, t.price_adult, t.price_child,
                       u.full_name, u.email,
                       p.code as promotion_code, p.discount_percent, p.discount_amount
                FROM bookings b
                JOIN tours t ON b.tour_id = t.tour_id
                JOIN users u ON b.user_id = u.user_id
                LEFT JOIN promotions p ON b.promotion_id = p.promotion_id
                WHERE b.booking_id = %s
            """
            cursor.execute(query, (booking_id,))
            booking = cursor.fetchone()
        conn.close()
        
        if booking:
            return format_booking_for_display(booking)
        return None
    except Exception as e:
        print(f"Error getting booking: {e}")
        return None


def get_bookings_by_email(email, limit=5):
    """Get bookings by user email with full details."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT b.booking_id, b.booking_date, b.num_adults, b.num_children,
                       b.total_price, b.status, b.special_request, b.phone,
                       t.title as tour_title, t.destination, t.start_date, t.end_date,
                       t.duration,
                       u.full_name, u.email,
                       p.code as promotion_code, p.discount_percent
                FROM bookings b
                JOIN tours t ON b.tour_id = t.tour_id
                JOIN users u ON b.user_id = u.user_id
                LEFT JOIN promotions p ON b.promotion_id = p.promotion_id
                WHERE u.email = %s
                ORDER BY b.booking_date DESC
                LIMIT %s
            """
            cursor.execute(query, (email, limit))
            bookings = cursor.fetchall()
        conn.close()
        
        if bookings:
            return format_bookings_list_for_display(bookings)
        return None
    except Exception as e:
        print(f"Error getting bookings by email: {e}")
        return None


def get_bookings_by_phone(phone, limit=5):
    """Get bookings by user phone number."""
    try:
        # Normalize phone number (remove spaces, dashes)
        phone_clean = phone.replace(" ", "").replace("-", "").replace(".", "")
        
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT b.booking_id, b.booking_date, b.num_adults, b.num_children,
                       b.total_price, b.status, b.special_request, b.phone,
                       t.title as tour_title, t.destination, t.start_date, t.end_date,
                       t.duration,
                       u.full_name, u.phone as user_phone,
                       p.code as promotion_code, p.discount_percent
                FROM bookings b
                JOIN tours t ON b.tour_id = t.tour_id
                JOIN users u ON b.user_id = u.user_id
                LEFT JOIN promotions p ON b.promotion_id = p.promotion_id
                WHERE REPLACE(REPLACE(u.phone, ' ', ''), '-', '') LIKE %s
                   OR REPLACE(REPLACE(b.phone, ' ', ''), '-', '') LIKE %s
                ORDER BY b.booking_date DESC
                LIMIT %s
            """
            phone_pattern = f"%{phone_clean[-9:]}%"
            cursor.execute(query, (phone_pattern, phone_pattern, limit))
            bookings = cursor.fetchall()
        conn.close()
        
        if bookings:
            return format_bookings_list_for_display(bookings)
        return None
    except Exception as e:
        print(f"Error getting bookings by phone: {e}")
        return None


def format_tours_for_display(tours):
    """Format tours data for AI context with full details."""
    if not tours:
        return "Không tìm thấy tour nào."
    
    region_map = {
        'NORTH': 'Miền Bắc',
        'CENTRAL': 'Miền Trung', 
        'SOUTH': 'Miền Nam'
    }
    
    category_map = {
        'ADVENTURE': 'Phiêu lưu',
        'CULTURAL': 'Văn hóa',
        'BEACH': 'Biển',
        'MOUNTAIN': 'Núi',
        'CITY': 'Thành phố',
        'ECOTOURISM': 'Sinh thái',
        'FOOD': 'Ẩm thực',
        'FAMILY': 'Gia đình'
    }
    
    lines = []
    for t in tours:
        price_adult = f"{t['price_adult']:,.0f}₫" if t['price_adult'] else "Liên hệ"
        price_child = f"{t['price_child']:,.0f}₫" if t['price_child'] else "Liên hệ"
        
        dates = ""
        if t.get('start_date') and t.get('end_date'):
            dates = f"Khởi hành: {t['start_date']} → {t['end_date']}"
        
        region = region_map.get(t.get('region'), t.get('region') or 'N/A')
        category = category_map.get(t.get('category'), t.get('category') or 'N/A')
        
        availability = t.get('availability', 0) or 0
        capacity = t.get('capacity', 0) or 0
        
        rating_text = ""
        if t.get('average_rating') and float(t['average_rating']) > 0:
            rating_text = f" | ⭐ {float(t['average_rating']):.1f}/5 ({t.get('review_count', 0)} đánh giá)"
        
        lines.append(
            f"🎯 {t['title']}\n"
            f"   📍 {t['destination']} ({region}) | 🏷️ {category}\n"
            f"   💰 Người lớn: {price_adult} | Trẻ em: {price_child}\n"
            f"   ⏱️ {t['duration'] or 'N/A'} | 👥 Còn {availability}/{capacity} chỗ\n"
            f"   📅 {dates}{rating_text}"
        )
    
    return "\n\n".join(lines)


def format_tour_detail_for_display(tour):
    """Format single tour with full details including itinerary."""
    if not tour:
        return None
    
    region_map = {
        'NORTH': 'Miền Bắc',
        'CENTRAL': 'Miền Trung', 
        'SOUTH': 'Miền Nam'
    }
    
    category_map = {
        'ADVENTURE': 'Phiêu lưu',
        'CULTURAL': 'Văn hóa',
        'BEACH': 'Biển',
        'MOUNTAIN': 'Núi',
        'CITY': 'Thành phố',
        'ECOTOURISM': 'Sinh thái',
        'FOOD': 'Ẩm thực',
        'FAMILY': 'Gia đình'
    }
    
    price_adult = f"{tour['price_adult']:,.0f}₫" if tour['price_adult'] else "Liên hệ"
    price_child = f"{tour['price_child']:,.0f}₫" if tour['price_child'] else "Liên hệ"
    region = region_map.get(tour.get('region'), tour.get('region') or 'N/A')
    category = category_map.get(tour.get('category'), tour.get('category') or 'N/A')
    
    availability = tour.get('availability', 0) or 0
    capacity = tour.get('capacity', 0) or 0
    
    rating_text = "Chưa có đánh giá"
    if tour.get('average_rating') and float(tour['average_rating']) > 0:
        rating_text = f"⭐ {float(tour['average_rating']):.1f}/5 ({tour.get('review_count', 0)} đánh giá)"
    
    result = (
        f"🎯 {tour['title']}\n"
        f"━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
        f"📍 Điểm đến: {tour['destination']} ({region})\n"
        f"🏷️ Loại tour: {category}\n"
        f"💰 Giá: Người lớn {price_adult} | Trẻ em {price_child}\n"
        f"⏱️ Thời gian: {tour['duration'] or 'N/A'}\n"
        f"📅 Khởi hành: {tour.get('start_date')} → {tour.get('end_date')}\n"
        f"👥 Còn trống: {availability}/{capacity} chỗ\n"
        f"📊 Đánh giá: {rating_text}\n"
    )
    
    if tour.get('description'):
        result += f"\n📝 Mô tả:\n{tour['description'][:500]}{'...' if len(tour.get('description', '')) > 500 else ''}\n"
    
    if tour.get('itinerary'):
        result += f"\n📋 Lịch trình:\n{tour['itinerary'][:800]}{'...' if len(tour.get('itinerary', '')) > 800 else ''}\n"
    
    return result


def format_booking_for_display(booking):
    """Format booking data for AI context with full details."""
    status_map = {
        'PENDING': 'Chờ xác nhận',
        'CONFIRMED': 'Đã xác nhận',
        'CANCELLED': 'Đã hủy',
        'COMPLETED': 'Hoàn thành'
    }
    
    status = status_map.get(booking['status'], booking['status'])
    total = f"{booking['total_price']:,.0f}₫" if booking['total_price'] else "N/A"
    
    # Calculate original price
    num_adults = booking.get('num_adults', 0) or 0
    num_children = booking.get('num_children', 0) or 0
    price_adult = booking.get('price_adult', 0) or 0
    price_child = booking.get('price_child', 0) or 0
    original_price = (num_adults * price_adult) + (num_children * price_child)
    
    result = (
        f"📋 CHI TIẾT ĐẶT TOUR\n"
        f"━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
        f"🔖 Mã booking: {booking['booking_id']}\n"
        f"🎯 Tour: {booking['tour_title']}\n"
        f"📍 Điểm đến: {booking['destination']}\n"
        f"📅 Lịch trình: {booking['start_date']} → {booking['end_date']}\n"
        f"⏱️ Thời gian: {booking.get('duration', 'N/A')}\n"
        f"━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
        f"👤 Khách hàng: {booking.get('full_name', 'N/A')}\n"
        f"📧 Email: {booking.get('email', 'N/A')}\n"
        f"📱 SĐT: {booking.get('phone', 'N/A')}\n"
        f"👥 Số khách: {num_adults} người lớn, {num_children} trẻ em\n"
        f"━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
        f"📆 Ngày đặt: {booking['booking_date']}\n"
    )
    
    # Show pricing breakdown
    if original_price > 0:
        result += f"💵 Giá gốc: {original_price:,.0f}₫\n"
    
    # Show promotion if applied
    if booking.get('promotion_code'):
        discount_info = ""
        if booking.get('discount_percent'):
            discount_info = f" (-{booking['discount_percent']}%)"
        elif booking.get('discount_amount'):
            discount_info = f" (-{booking['discount_amount']:,.0f}₫)"
        result += f"🎁 Mã giảm giá: {booking['promotion_code']}{discount_info}\n"
    
    result += (
        f"💰 Tổng thanh toán: {total}\n"
        f"📊 Trạng thái: {status}\n"
    )
    
    if booking.get('special_request'):
        result += f"📝 Yêu cầu đặc biệt: {booking['special_request']}\n"
    
    return result


def format_bookings_list_for_display(bookings):
    """Format multiple bookings for AI context with full details."""
    status_map = {
        'PENDING': 'Chờ xác nhận',
        'CONFIRMED': 'Đã xác nhận',
        'CANCELLED': 'Đã hủy',
        'COMPLETED': 'Hoàn thành'
    }
    
    if not bookings:
        return "Không tìm thấy đơn đặt tour nào."
    
    user_name = bookings[0].get('full_name', 'Khách hàng')
    lines = [f"📋 DANH SÁCH ĐẶT TOUR CỦA {user_name.upper()}\n{'━' * 35}\n"]
    
    for b in bookings:
        status = status_map.get(b['status'], b['status'])
        total = f"{b['total_price']:,.0f}₫" if b['total_price'] else "N/A"
        num_adults = b.get('num_adults', 0) or 0
        num_children = b.get('num_children', 0) or 0
        
        promo_text = ""
        if b.get('promotion_code'):
            promo_text = f" 🎁 {b['promotion_code']}"
            if b.get('discount_percent'):
                promo_text += f" (-{b['discount_percent']}%)"
        
        lines.append(
            f"🎯 {b['tour_title']}\n"
            f"   📍 {b['destination']} | ⏱️ {b.get('duration', 'N/A')}\n"
            f"   🔖 Mã: {b['booking_id'][:8]}...\n"
            f"   📅 Ngày đặt: {b['booking_date']}\n"
            f"   📆 Khởi hành: {b['start_date']} → {b['end_date']}\n"
            f"   👥 {num_adults} người lớn, {num_children} trẻ em\n"
            f"   💰 {total}{promo_text}\n"
            f"   📊 Trạng thái: {status}\n"
        )
    
    return "\n".join(lines)
