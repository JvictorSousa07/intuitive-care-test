from fastapi import APIRouter, HTTPException, Query
from typing import List
from app.services.operadora_service import OperadoraService
from app.schemas.operadora_schema import OperadoraResponse, OperadoraDTO

router = APIRouter(prefix="/api/operadoras", tags=["Operadoras"])
service = OperadoraService()

@router.get("", response_model=OperadoraResponse)
def listar(page: int = 1, limit: int = 10, search: str = ""):
    return service.listar_operadoras(page, limit, search)

@router.get("/{cnpj}", response_model=OperadoraDTO)
def detalhes(cnpj: str):
    operadora = service.buscar_detalhes(cnpj)
    if not operadora:
        raise HTTPException(status_code=404, detail="Operadora não encontrada")
    return operadora

@router.get("/{cnpj}/despesas")
def despesas(cnpj: str):
    return service.buscar_historico(cnpj)