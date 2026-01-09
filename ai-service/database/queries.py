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
        'BEACH': 'Biển đảo',
        'CITY': 'Thành phố',
        'CULTURE': 'Văn hóa',
        'CULTURAL': 'Văn hóa',
        'EXPLORATION': 'Phiêu lưu',
        'ADVENTURE': 'Mạo hiểm',
        'NATURE': 'Thiên nhiên',
        'FOOD': 'Ẩm thực',
        'MOUNTAIN': 'Núi',
        'ECOTOURISM': 'Sinh thái',
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
        'BEACH': 'Biển đảo',
        'CITY': 'Thành phố',
        'CULTURE': 'Văn hóa',
        'CULTURAL': 'Văn hóa',
        'EXPLORATION': 'Phiêu lưu',
        'ADVENTURE': 'Mạo hiểm',
        'NATURE': 'Thiên nhiên',
        'FOOD': 'Ẩm thực',
        'MOUNTAIN': 'Núi',
        'ECOTOURISM': 'Sinh thái',
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
