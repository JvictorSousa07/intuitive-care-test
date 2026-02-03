from pydantic import BaseModel
from typing import List, Optional

# Esse arquivo serve para DOCUMENTAR o formato dos dados
# Mesmo que o banco devolva direto, é bom ter isso para o Swagger (docs automática)

class OperadoraDTO(BaseModel):
    reg_ans: str
    cnpj: str
    razao_social: str
    modalidade: str
    uf: str

class PaginacaoMeta(BaseModel):
    total: int
    page: int
    limit: int
    pages: int

class OperadoraResponse(BaseModel):
    data: List[OperadoraDTO]
    meta: PaginacaoMeta