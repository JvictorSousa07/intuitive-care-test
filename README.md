# Intuitive Care - Desafio Técnico (Fullstack)

Este repositório contém a solução completa para o teste técnico da Intuitive Care. O projeto abrange desde o ETL de dados da ANS (Java), modelagem de banco de dados (SQL) até a visualização em uma aplicação Web (Python + Vue.js).

## Status do Projeto
- [x] **Tarefa 1**: Integração, Processamento e Consolidação (ETL - Java)
- [x] **Tarefa 2**: Transformação, Validação e Enriquecimento (Java)
- [x] **Tarefa 3**: Modelagem de Dados e SQL (PostgreSQL)
- [x] **Tarefa 4**: API e Visualização (Python FastAPI + Vue.js)

---

## 1. Backend Java (ETL e Processamento)

Responsável por baixar, processar e sanitizar os dados da ANS.

### Arquitetura e Design Patterns
* **Layered Architecture:** Separação clara entre `domain` (entidades), `service` (regras de negócio) e `infra` (parsers de arquivo).
* **Injeção de Dependência Manual:** A classe `Main` atua como orquestrador, injetando dependências via construtor para facilitar testes.
* **Strategy Pattern:** Utilizado no `FileParserStrategy` para suportar diferentes formatos de arquivos dentro dos ZIPs da ANS.

### Trade-offs e Decisões Técnicas (Java)

#### 1.1 Processamento: Memória vs. Streaming
* **Decisão:** Processamento Incremental (Streaming).
* **Solução:** Uso de `ZipInputStream` + `BufferedReader`.
* **Justificativa:** Para evitar `OutOfMemoryError` com arquivos grandes, o streaming mantém o consumo de RAM constante (O(1)), processando linha a linha sem carregar o arquivo todo.

#### 1.2 Estratégia de Enriquecimento (Join)
* **Decisão:** *In-Memory Hash Join*.
* **Solução:** Carregamento do CSV de cadastro (`Relatorio_cadop.csv`) em um `HashMap`.
* **Justificativa:** O arquivo de cadastro é pequeno comparado ao de despesas. O acesso via Hash Map é O(1), sendo drasticamente mais rápido que *Nested Loops* ou consultas repetitivas em banco.

#### 1.3 Validação de CNPJ
* **Decisão:** *Soft Validation* (Persistir inválidos).
* **Justificativa:** Em contextos financeiros/contábeis, descartar registros pode gerar "furos" no balanço. Optei por persistir a linha marcando-a como `INVALIDO` para posterior auditoria, garantindo a integridade do montante total.

---

## 2. Banco de Dados e SQL (Tarefa 3)

Os scripts SQL localizados no arquivo `queries_tarefa3.sql` são responsáveis por estruturar o banco e responder às perguntas de negócio.

### Trade-offs e Decisões de Modelagem

#### 2.1 Normalização vs Desnormalização
* **Decisão:** Modelagem Normalizada (Tabelas Relacionais).
* **Estrutura:** Criei uma tabela `operadoras` (dimensão) separada da tabela `despesas` (fatos).
* **Justificativa:**
    1.  **Integridade:** Garante que não existam despesas órfãs sem uma operadora válida.
    2.  **Espaço:** Evita a repetição desnecessária dos dados cadastrais (Razão Social, UF) em milhões de linhas de despesas.
    3.  **Manutenção:** Se uma operadora mudar de nome, atualizamos apenas 1 registro.

#### 2.2 Tipos de Dados Monetários
* **Decisão:** `DECIMAL(15, 2)` (PostgreSQL).
* **Justificativa:** Jamais utilizar `FLOAT` para dinheiro devido a erros de precisão em ponto flutuante (ex: 0.1 + 0.2 resultando em 0.300000004). O `DECIMAL` garante a precisão exata dos centavos exigida em relatórios contábeis.

---

## 3. Aplicação Web (Tarefa 4)

Solução Fullstack para visualização dos dados processados.

### 3.1 Backend: Python com FastAPI (Clean Arch)
* **Organização:** `Routers` (Endpoints), `Services` (Regra de Negócio), `Repositories` (SQL Puro).
* **Framework (FastAPI):** Escolhido pela performance assíncrona e geração automática de documentação (Swagger), superior ao Flask para APIs modernas.
* **SQL Puro:** Optei por não usar ORM para demonstrar controle total sobre as queries SQL e otimizar a performance de leitura (`read-heavy`).
* **Paginação:** Implementada via `LIMIT/OFFSET` no banco para não sobrecarregar a memória do servidor.

### 3.2 Frontend: Vue.js + Vite
Interface moderna e componentizada.
* **Componentização:** Separação em `GraficoUF.vue` (Chart.js), `OperadoraModal.vue` e `Home.vue`.
* **Estilo:** Uso de **TailwindCSS** para produtividade e design responsivo.
* **UX:** Feedback visual de *loading* e tratamento de erros de API.

---

## Como Executar o Projeto Completo

### Pré-requisitos
* Java + e Maven
* Python 3.8+
* Node.js e NPM
* PostgreSQL

### Passo 1: Processamento Java (ETL)
```bash
# Na raiz do projeto
mvn clean install
java -jar target/intuitive-care-test-1.0-SNAPSHOT.jar
# Isso gerará os CSVs na pasta raiz
````

Passo 2: Banco de Dados
Crie um banco de dados chamado ans_db.

Execute o script queries_tarefa3.sql no seu cliente SQL (pgAdmin/DBeaver) para criar as tabelas e importar os CSVs gerados no passo anterior.

### Passo 3: API Python

```bash
cd api_python
pip install -r requirements.txt
python -m app.main
# A API rodará em http://localhost:8000
```

### Passo 4: Frontend Vue.js

```bash cd frontend_vue
npm install
npm run dev
# Acesse o link gerado (ex: http://localhost:5173)
```