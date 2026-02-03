import psycopg2
from app.core.config import DB_CONFIG

def get_db_connection():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        print(f"Erro ao conectar no banco: {e}")
        return None