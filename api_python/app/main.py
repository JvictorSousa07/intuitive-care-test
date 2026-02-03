from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers import operadoras, estatisticas
import uvicorn

# Inicializa a API
app = FastAPI(
    title="API Intuitive Care - Clean Architecture",
    description="Backend estruturado com camadas (Router -> Service -> Repository)",
    version="1.0.0"
)

# Configura CORS (Permite acesso do Frontend Vue.js)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Registra as Rotas
app.include_router(operadoras.router)
app.include_router(estatisticas.router)

# Rota Raiz (Health Check)
@app.get("/")
def health_check():
    return {"status": "API Online", "docs": "/docs"}

if __name__ == "__main__":
    # Permite rodar o arquivo diretamente se necessário
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)