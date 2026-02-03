from pydantic import BaseModel
from typing import List, Optional

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