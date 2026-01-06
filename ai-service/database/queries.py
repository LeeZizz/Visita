"""
Database query helpers for chatbot.
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
    """Get summary of active tours."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT tour_id, title, destination, duration, 
                       price_adult, price_child, capacity, start_date, end_date, category
                FROM tours 
                WHERE is_active = 1 
                ORDER BY start_date ASC
                LIMIT %s
            """
            cursor.execute(query, (limit,))
            tours = cursor.fetchall()
        conn.close()
        return format_tours_for_display(tours)
    except Exception as e:
        print(f"Error getting tours summary: {e}")
        return None


def search_tours(destination=None, max_price=None, category=None, limit=5):
    """Search tours with filters."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT tour_id, title, destination, duration, 
                       price_adult, price_child, capacity, start_date, end_date, category
                FROM tours 
                WHERE is_active = 1
            """
            params = []
            
            if destination:
                query += " AND destination LIKE %s"
                params.append(f"%{destination}%")
            
            if max_price:
                query += " AND price_adult <= %s"
                params.append(max_price)
            
            if category:
                query += " AND category = %s"
                params.append(category)
            
            query += " ORDER BY price_adult ASC LIMIT %s"
            params.append(limit)
            
            cursor.execute(query, tuple(params))
            tours = cursor.fetchall()
        conn.close()
        return format_tours_for_display(tours)
    except Exception as e:
        print(f"Error searching tours: {e}")
        return None


def get_booking_by_id(booking_id):
    """Get booking details by ID."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT b.booking_id, b.booking_date, b.num_adults, b.num_children,
                       b.total_price, b.status, b.special_request,
                       t.title as tour_title, t.destination, t.start_date, t.end_date
                FROM bookings b
                JOIN tours t ON b.tour_id = t.tour_id
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


def get_payment_status(booking_id):
    """Get payment status for a booking."""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT p.payment_id, p.amount, p.payment_method, 
                       p.payment_date, p.status, p.transaction_id
                FROM payments p
                WHERE p.booking_id = %s
                ORDER BY p.payment_date DESC
            """
            cursor.execute(query, (booking_id,))
            payments = cursor.fetchall()
        conn.close()
        
        if payments:
            return format_payments_for_display(payments)
        return None
    except Exception as e:
        print(f"Error getting payment status: {e}")
        return None


def format_tours_for_display(tours):
    """Format tours data for AI context."""
    if not tours:
        return "Không tìm thấy tour nào."
    
    lines = []
    for t in tours:
        price = f"{t['price_adult']:,.0f}₫" if t['price_adult'] else "Liên hệ"
        dates = ""
        if t.get('start_date') and t.get('end_date'):
            dates = f" | Khởi hành: {t['start_date']} - {t['end_date']}"
        
        lines.append(
            f"• {t['title']} ({t['destination']})\n"
            f"  Giá người lớn: {price} | Thời gian: {t['duration'] or 'N/A'}{dates}"
        )
    
    return "\n".join(lines)


def format_booking_for_display(booking):
    """Format booking data for AI context."""
    status_map = {
        'PENDING': 'Chờ xác nhận',
        'CONFIRMED': 'Đã xác nhận',
        'CANCELLED': 'Đã hủy',
        'COMPLETED': 'Hoàn thành'
    }
    
    status = status_map.get(booking['status'], booking['status'])
    total = f"{booking['total_price']:,.0f}₫" if booking['total_price'] else "N/A"
    
    return (
        f"Mã booking: {booking['booking_id']}\n"
        f"Tour: {booking['tour_title']} ({booking['destination']})\n"
        f"Ngày đặt: {booking['booking_date']}\n"
        f"Số khách: {booking['num_adults']} người lớn, {booking['num_children'] or 0} trẻ em\n"
        f"Tổng tiền: {total}\n"
        f"Trạng thái: {status}\n"
        f"Lịch trình: {booking['start_date']} - {booking['end_date']}"
    )


def format_payments_for_display(payments):
    """Format payment data for AI context."""
    status_map = {
        'PENDING': 'Chờ thanh toán',
        'SUCCESS': 'Thành công',
        'FAILED': 'Thất bại',
        'REFUNDED': 'Đã hoàn tiền'
    }
    
    lines = []
    for p in payments:
        status = status_map.get(p['status'], p['status'])
        amount = f"{p['amount']:,.0f}₫" if p['amount'] else "N/A"
        
        lines.append(
            f"• Thanh toán #{p['payment_id'][:8]}...\n"
            f"  Số tiền: {amount} | Phương thức: {p['payment_method'] or 'N/A'}\n"
            f"  Trạng thái: {status} | Ngày: {p['payment_date']}"
        )
    
    return "\n".join(lines)
