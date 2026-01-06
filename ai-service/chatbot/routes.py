"""
Flask routes for chatbot API.
"""
from flask import Blueprint, request, jsonify
from chatbot.service import chat


chatbot_bp = Blueprint('chatbot', __name__, url_prefix='/api/chatbot')


@chatbot_bp.route('/chat', methods=['POST'])
def chat_endpoint():
    """
    Chat endpoint for AI assistant.
    
    Request body:
        {
            "message": "User's message",
            "history": [
                {"role": "user", "content": "Previous user message"},
                {"role": "assistant", "content": "Previous AI response"}
            ]
        }
    
    Response:
        {
            "response": "AI response text"
        }
    """
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({"error": "Request body is required"}), 400
        
        message = data.get('message')
        if not message or not message.strip():
            return jsonify({"error": "Message is required"}), 400
        
        history = data.get('history', [])
        
        # Validate history format
        if not isinstance(history, list):
            return jsonify({"error": "History must be an array"}), 400
        
        # Call chat service
        response_text = chat(message.strip(), history)
        
        return jsonify({"response": response_text})
        
    except Exception as e:
        print(f"Error in chat endpoint: {e}")
        return jsonify({"error": "Internal server error", "details": str(e)}), 500
