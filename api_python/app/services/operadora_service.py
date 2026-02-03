from app.repositories.operadora_repository import OperadoraRepository

class OperadoraService:
    def __init__(self):
        self.repository = OperadoraRepository()

    def listar_operadoras(self, page, limit, search):
        offset = (page - 1) * limit
        termo_busca = f"%{search}%"

        total = self.repository.count_all(termo_busca)
        dados = self.repository.find_all_paginated(limit, offset, termo_busca)

        # Lógica de cálculo de páginas
        total_pages = (total // limit) + (1 if total % limit > 0 else 0)

        return {
            "data": dados,
            "meta": {
                "total": total,
                "page": page,
                "limit": limit,
                "pages": total_pages
            }
        }

    def buscar_detalhes(self, cnpj):
        # Regra de negócio: Limpar formatação do CNPJ antes de buscar
        cnpj_limpo = cnpj.replace(".", "").replace("/", "").replace("-", "")
        return self.repository.find_by_cnpj(cnpj_limpo)

    def buscar_historico(self, cnpj):
        cnpj_limpo = cnpj.replace(".", "").replace("/", "").replace("-", "")
        return self.repository.find_despesas_by_cnpj(cnpj_limpo)

    def obter_dados_grafico(self):
        return self.repository.get_estatisticas_uf()