from psycopg2.extras import RealDictCursor
from app.core.database import get_db_connection

class OperadoraRepository:

    def count_all(self, termo: str):
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("""
                       SELECT COUNT(*) as total
                       FROM operadoras
                       WHERE razao_social ILIKE %s OR cnpj ILIKE %s
                       """, (termo, termo))
        total = cursor.fetchone()[0]
        conn.close()
        return total

    def find_all_paginated(self, limit: int, offset: int, termo: str):
        conn = get_db_connection()
        cursor = conn.cursor(cursor_factory=RealDictCursor)
        cursor.execute("""
                       SELECT reg_ans, cnpj, razao_social, modalidade, uf
                       FROM operadoras
                       WHERE razao_social ILIKE %s OR cnpj ILIKE %s
                       ORDER BY razao_social ASC
                           LIMIT %s OFFSET %s
                       """, (termo, termo, limit, offset))
        results = cursor.fetchall()
        conn.close()
        return results

    def find_by_cnpj(self, cnpj_limpo: str):
        conn = get_db_connection()
        cursor = conn.cursor(cursor_factory=RealDictCursor)
        cursor.execute("""
                       SELECT * FROM operadoras
                       WHERE REPLACE(REPLACE(REPLACE(cnpj, '.', ''), '/', ''), '-', '') = %s
                       """, (cnpj_limpo,))
        result = cursor.fetchone()
        conn.close()
        return result

    def find_despesas_by_cnpj(self, cnpj_limpo: str):
        conn = get_db_connection()
        cursor = conn.cursor(cursor_factory=RealDictCursor)
        cursor.execute("""
                       SELECT d.data_evento, d.trimestre, d.ano, d.valor
                       FROM despesas d
                                JOIN operadoras o ON d.reg_ans = o.reg_ans
                       WHERE REPLACE(REPLACE(REPLACE(o.cnpj, '.', ''), '/', ''), '-', '') = %s
                       ORDER BY d.data_evento DESC
                       """, (cnpj_limpo,))
        results = cursor.fetchall()
        conn.close()
        return results

    def get_estatisticas_uf(self):
        conn = get_db_connection()
        cursor = conn.cursor(cursor_factory=RealDictCursor)
        cursor.execute("""
                       SELECT o.uf, SUM(d.valor) as total
                       FROM despesas d
                                JOIN operadoras o ON d.reg_ans = o.reg_ans
                       GROUP BY o.uf
                       ORDER BY total DESC
                           LIMIT 5
                       """)
        results = cursor.fetchall()
        conn.close()
        return results