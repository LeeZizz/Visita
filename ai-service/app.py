from flask import Flask, jsonify, request
from config import Config
import pymysql
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
import traceback

app = Flask(__name__)

# Global variables to store the model in memory
tfidf_matrix = None
cosine_sim = None
indices = None
tours_data = None

def get_db_connection():
    return pymysql.connect(
        host=Config.DB_HOST,
        user=Config.DB_USERNAME,
        password=Config.DB_PASSWORD,
        database=Config.DB_NAME,
        port=int(Config.DB_PORT),
        cursorclass=pymysql.cursors.DictCursor
    )

def train_model():
    global tfidf_matrix, cosine_sim, indices, tours_data
    
    print("Training Recommendation Model...")
    try:
        print("  -> Attempting to connect to DB...")
        conn = get_db_connection()
        print("  -> Connected! Executing query...")
        
        # Use cursor manually with pymysql to avoid pandas warning and ensure correct data format
        with conn.cursor() as cursor:
            query = "SELECT tour_id, title, description, destination, category FROM tours WHERE is_active = 1"
            cursor.execute(query)
            result = cursor.fetchall()
            
        conn.close()
        
        # Convert list of dicts to DataFrame
        tours_data = pd.DataFrame(result)
        print(f"  -> Query executed. Found {len(tours_data)} rows.")
        
        if tours_data.empty:
            print("No active tours found in database.")
            return

        print("  -> Preprocessing data...")
        tours_data['description'] = tours_data['description'].fillna('')
        tours_data['title'] = tours_data['title'].fillna('')
        tours_data['destination'] = tours_data['destination'].fillna('')
        
        tours_data['soup'] = tours_data['title'] + " " + tours_data['destination'] + " " + tours_data['description']
        
        print("  -> Vectorizing text (TF-IDF)...")
        tfidf = TfidfVectorizer(stop_words='english')
        tfidf_matrix = tfidf.fit_transform(tours_data['soup'])
        print(f"  -> Vectorization complete. Matrix shape: {tfidf_matrix.shape}")
        
        print("  -> Calculating Cosine Similarity...")
        cosine_sim = linear_kernel(tfidf_matrix, tfidf_matrix)
        print("  -> Cosine Similarity complete.")
        
        indices = pd.Series(tours_data.index, index=tours_data['tour_id']).drop_duplicates()
        print("  -> Index mapping complete.")
        
        # DEBUG: Print all loaded IDs to help user debug
        available_ids = tours_data['tour_id'].tolist()
        print(f"DEBUG: Loaded {len(available_ids)} IDs. First 5: {available_ids[:5]}")
        
        print(f"Model trained successfully with {len(tours_data)} tours.")
        
    except Exception as e:
        print("CRITICAL ERROR in train_model:")
        traceback.print_exc()

# Train model on startup
print("Starting application context...")
with app.app_context():
    train_model()
print("Finished loading model. Starting Flask server...")

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ok", "tours_loaded": len(tours_data) if tours_data is not None else 0})

@app.route('/recommend', methods=['GET'])
def recommend():
    tour_id = request.args.get('tour_id')
    
    if not tour_id:
        return jsonify({"error": "Missing tour_id parameter"}), 400
        
    if indices is None:
        return jsonify({"error": "Model not trained yet"}), 500

    if tour_id not in indices:
        return jsonify({"error": "Tour ID not found in database"}), 404

    try:
        # Get index of the tour
        idx = indices[tour_id]

        # Handle case where multiple tours might have same ID (unlikely but possible if DB inconsistent)
        if isinstance(idx, pd.Series):
            idx = idx.iloc[0]

        # Get pairwise similarity scores
        sim_scores = list(enumerate(cosine_sim[idx]))

        # Sort based on similarity scores
        sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)

        # Get the scores of the 5 most similar tours (ignoring the 0th which is itself)
        sim_scores = sim_scores[1:6]

        # Get the tour indices
        tour_indices = [i[0] for i in sim_scores]

        # Return top similar tour IDs
        result_ids = tours_data['tour_id'].iloc[tour_indices].tolist()
        
        return jsonify({
            "source_tour_id": tour_id,
            "recommendations": result_ids
        })
    except Exception as e:
        print(f"Error during recommendation: {e}")
        return jsonify({"error": str(e)}), 500

@app.route('/recommend/user', methods=['GET'])
def recommend_user():
    user_id = request.args.get('user_id')
    
    if not user_id:
        return jsonify({"error": "Missing user_id parameter"}), 400
        
    if indices is None:
        return jsonify({"error": "Model not trained yet"}), 500

    try:
        conn = get_db_connection()
        tours_liked = []
        
        # 1. Get Favorites
        with conn.cursor() as cursor:
            # Query Favorites
            query_fav = "SELECT tour_id FROM favorites WHERE user_id = %s"
            cursor.execute(query_fav, (user_id,))
            fav_rows = cursor.fetchall()
            for row in fav_rows:
                tours_liked.append(row['tour_id'])
                
            # Query History (VIEW or BOOK) to enrich profile
            query_hist = "SELECT tour_id FROM history WHERE user_id = %s ORDER BY timestamp DESC LIMIT 5"
            cursor.execute(query_hist, (user_id,))
            hist_rows = cursor.fetchall()
            for row in hist_rows:
                tours_liked.append(row['tour_id'])
                
        conn.close()
        
        # Unique liked tours
        tours_liked = list(set(tours_liked))
        
        if not tours_liked:
            return jsonify({
                "user_id": user_id,
                "message": "User has no history or favorites. Recommend popular tours instead.",
                "recommendations": [] # In future, return Top Popular tours here
            })

        # 2. Aggregate Similarity Scores
        # Create a zero-vector for scores
        total_scores = [0] * len(indices)
        
        valid_source_tours = 0
        
        for tour_id in tours_liked:
            if tour_id in indices:
                valid_source_tours += 1
                idx = indices[tour_id]
                if isinstance(idx, pd.Series): idx = idx.iloc[0]
                
                # Get similarity row for this tour
                sim_scores = cosine_sim[idx]
                
                # Add to total
                for i, score in enumerate(sim_scores):
                    total_scores[i] += score
                    
        if valid_source_tours == 0:
             return jsonify({"user_id": user_id, "recommendations": []})

        # 3. Sort and Filter
        # Pair indices with scores
        sim_scores_enum = list(enumerate(total_scores))
        
        # Sort desc
        sim_scores_enum = sorted(sim_scores_enum, key=lambda x: x[1], reverse=True)
        
        # Filter out tours user already knows (optional, but good for discovery)
        # And get Top 5
        final_recommendations = []
        count = 0
        
        known_indices = [indices[tid] if not isinstance(indices[tid], pd.Series) else indices[tid].iloc[0] for tid in tours_liked if tid in indices]
        
        for i, score in sim_scores_enum:
            if i not in known_indices:
                final_recommendations.append(i)
                count += 1
                if count >= 5:
                    break
        
        # Convert indices back to Tour IDs
        result_ids = tours_data['tour_id'].iloc[final_recommendations].tolist()

        return jsonify({
            "user_id": user_id,
            "based_on_tours": tours_liked,
            "recommendations": result_ids
        })
        
    except Exception as e:
        print(f"Error in recommend_user: {e}")
        traceback.print_exc()
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5000, debug=True)
