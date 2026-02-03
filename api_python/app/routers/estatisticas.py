from fastapi import APIRouter
from app.services.operadora_service import OperadoraService

router = APIRouter(prefix="/api/estatisticas", tags=["Estatísticas"])
service = OperadoraService()

@router.get("")
def grafico():
    dados = service.obter_dados_grafico()
    return {"distribuicao_uf": dados}