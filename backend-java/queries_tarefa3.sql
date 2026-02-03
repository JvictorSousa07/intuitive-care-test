-- TAREFA 3: BANCO DE DADOS E ANÁLISE (PostgreSQL)
-- Arquivo: queries_tarefa3.sql
-- Descrição: Modelagem, ETL via SQL e Queries Analíticas.

-- ============================================================================
-- 1. MODELAGEM (DDL)
-- ============================================================================

-- TRADE-OFF TÉCNICO: NORMALIZAÇÃO
-- Escolha: Opção B (Tabelas Normalizadas Separadas)
-- Justificativa:
-- 1. Volume de dados: Percebi que a Razão Social se repete várias vezes. Ao separar em
--    'operadoras' e 'despesas', eu reduzo drasticamente o armazenamento (Storage).
-- 2. Atualização: Se uma operadora mudar de nome, preciso atualizar apenas 1 registro na tabela
--    dimensão, evitando updates custosos em milhões de linhas de despesas.
-- 3. Complexidade: Garanto que as queries analíticas fiquem performáticas usando JOINs indexados.

-- TRADE-OFF TÉCNICO: TIPOS DE DADOS
-- 1. Monetários: Optei por DECIMAL(15,2) em vez de FLOAT.
--    Justificativa: Sei que FLOAT tem problemas de precisão em ponto flutuante. Para dados financeiros,
--    considerei a precisão exata do DECIMAL obrigatória para evitar "furos" de centavos.
-- 2. Datas: Decidi usar DATE em vez de VARCHAR.
--    Justificativa: Isso me permite usar funções nativas do banco (EXTRACT, RANGE) e criar índices de tempo eficientes.

-- Tabela 1: Dados Cadastrais das Operadoras
CREATE TABLE IF NOT EXISTS operadoras (
    reg_ans VARCHAR(10) PRIMARY KEY,
    cnpj VARCHAR(20),
    razao_social VARCHAR(255),
    modalidade VARCHAR(100),
    uf CHAR(2)
);
CREATE INDEX IF NOT EXISTS idx_operadoras_uf ON operadoras(uf);

-- Tabela 2: Dados Consolidados de Despesas
CREATE TABLE IF NOT EXISTS despesas (
    id SERIAL PRIMARY KEY,
    reg_ans VARCHAR(10),
    data_evento DATE,
    trimestre VARCHAR(2),
    ano INT,
    valor DECIMAL(15,2),
    FOREIGN KEY (reg_ans) REFERENCES operadoras(reg_ans)
);
CREATE INDEX IF NOT EXISTS idx_despesas_tri_ano ON despesas(trimestre, ano);

-- Tabela 3: Dados Agregados (Resultado da Tarefa 2.3)
CREATE TABLE IF NOT EXISTS despesas_agregadas (
    id SERIAL PRIMARY KEY,
    razao_social VARCHAR(255),
    uf CHAR(2),
    total_despesas DECIMAL(15,2),
    media_trimestral DECIMAL(15,2),
    desvio_padrao DECIMAL(15,2)
);

-- ============================================================================
-- 2. CARGA DE DADOS (DML com Tabela Temporária)
-- ============================================================================

-- ANÁLISE CRÍTICA E TRATAMENTO DE INCONSISTÊNCIAS:
-- Notei que o comando COPY do PostgreSQL é estrito (falha se o tipo de dado não bater).
-- Abordagem: Utilizei uma Tabela Temporária (Staging).
-- 1. Valores NULL: Importei tudo como TEXT para tratar na inserção final.
-- 2. Strings em campos numéricos: O CSV usa vírgula (,) e o banco usa ponto (.).
--    Tratamento: Importei como string e usei REPLACE(valor, ',', '.') no CAST.
-- 3. Datas: Converti as strings YYYY-MM-DD explicitamente usando TO_DATE para garantir a integridade.

-- 2.1 STAGING: Importação de Operadoras
CREATE TEMP TABLE temp_operadoras (
    reg_ans TEXT, cnpj TEXT, razao_social TEXT, modalidade TEXT, uf TEXT, 
    data_reg TEXT, trimestre TEXT, ano TEXT, valor TEXT, status TEXT
);

-- Nota: Ajuste o caminho '/caminho/para/...' conforme seu ambiente.
COPY temp_operadoras FROM 'C:/TESTE INTUITIVE CARE/intuitive-care-test/despesas_enriquecidas.csv' WITH (FORMAT csv, HEADER true, DELIMITER ';', QUOTE '"', ENCODING 'UTF8');
INSERT INTO operadoras (reg_ans, cnpj, razao_social, modalidade, uf)
SELECT DISTINCT reg_ans, cnpj, razao_social, modalidade, uf
FROM temp_operadoras
ON CONFLICT (reg_ans) DO NOTHING; 

-- 2.2 STAGING: Importação de Despesas
INSERT INTO despesas (reg_ans, data_evento, trimestre, ano, valor)
SELECT 
    reg_ans,
    TO_DATE(data_reg, 'YYYY-MM-DD'),
    trimestre,
    CAST(ano AS INTEGER),
    CAST(REPLACE(valor, ',', '.') AS DECIMAL(15,2))
FROM temp_operadoras;

TRUNCATE temp_operadoras; 

-- 2.3 STAGING: Importação de Agregados
CREATE TEMP TABLE temp_agregadas (
    razao_social TEXT, uf TEXT, total TEXT, media TEXT, desvio TEXT
);

-- Nota: Ajuste o caminho '/caminho/para/...' conforme seu ambiente.
COPY temp_agregadas FROM 'C:/TESTE INTUITIVE CARE/intuitive-care-test/despesas_agregadas.csv' WITH (FORMAT csv, HEADER true, DELIMITER ';', ENCODING 'UTF8');
INSERT INTO despesas_agregadas (razao_social, uf, total_despesas, media_trimestral, desvio_padrao)
SELECT 
    razao_social,
    uf,
    CAST(REPLACE(total, ',', '.') AS DECIMAL(15,2)),
    CAST(REPLACE(media, ',', '.') AS DECIMAL(15,2)),
    CAST(REPLACE(desvio, ',', '.') AS DECIMAL(15,2))
FROM temp_agregadas;

-- ============================================================================
-- 3. QUERIES ANALÍTICAS
-- ============================================================================

-- Query 1: Top 5 operadoras com maior crescimento percentual
-- DESAFIO: Operadoras sem dados em todos os trimestres.
-- Tratamento: Utilizei JOIN (INNER JOIN). Dessa forma, excluo automaticamente operadoras que
-- não existem em um dos períodos comparados, evitando cálculos de crescimento infinitos
-- ou erros de divisão por zero.
WITH limites AS (
    SELECT 
        MIN(trimestre) as tri_inicial, 
        MAX(trimestre) as tri_final 
    FROM despesas 
    WHERE data_evento BETWEEN '2025-01-01' AND '2025-12-31'
),
vendas_inicial AS (
    SELECT d.reg_ans, SUM(d.valor) as total_ini
    FROM despesas d 
    JOIN limites l ON d.trimestre = l.tri_inicial
    WHERE d.data_evento BETWEEN '2025-01-01' AND '2025-12-31'
    GROUP BY d.reg_ans
),
vendas_final AS (
    SELECT d.reg_ans, SUM(d.valor) as total_fim
    FROM despesas d 
    JOIN limites l ON d.trimestre = l.tri_final
    WHERE d.data_evento BETWEEN '2025-01-01' AND '2025-12-31'
    GROUP BY d.reg_ans
)
SELECT 
    o.razao_social,
    vi.total_ini,
    vf.total_fim,
    ROUND(((vf.total_fim - vi.total_ini) / vi.total_ini * 100), 2) as crescimento_pct
FROM vendas_inicial vi
JOIN vendas_final vf ON vi.reg_ans = vf.reg_ans
JOIN operadoras o ON vi.reg_ans = o.reg_ans
WHERE vi.total_ini > 0 
  AND vf.total_fim > 0 -- Garante que tem dados nos dois pontas
ORDER BY crescimento_pct DESC
LIMIT 5;

-- Query 2: Distribuição por UF (Top 5) e Médias
-- Desafio Adicional: Média por operadora (não apenas total).
-- Minha Solução: Calculei dividindo a soma total pela contagem distinta de registros ANS (SUM / COUNT DISTINCT).
SELECT 
    o.uf,
    SUM(d.valor) AS total_estado,
    ROUND(AVG(d.valor), 2) AS media_por_lancamento,
    ROUND(
        SUM(d.valor) / NULLIF(COUNT(DISTINCT d.reg_ans), 0),
        2
    ) AS media_por_operadora
FROM despesas d
JOIN operadoras o ON d.reg_ans = o.reg_ans
GROUP BY o.uf
ORDER BY total_estado DESC
LIMIT 5;

-- Query 3: Operadoras acima da média em >= 2 trimestres
-- TRADE-OFF TÉCNICO (Minha Abordagem):
-- Opção Escolhida: CTEs.
-- Justificativa: Preferi usar CTEs em vez de subqueries aninhadas para priorizar a legibilidade
-- e manutenibilidade do código. Assim, consigo separar a lógica de cálculo da média mensal
-- da lógica de filtragem das operadoras.
WITH media_mensal AS (
    SELECT trimestre, AVG(total_op) as media_geral_tri
    FROM (SELECT reg_ans, trimestre, SUM(valor) as total_op FROM despesas GROUP BY reg_ans, trimestre) sub
    GROUP BY trimestre
),
desempenho AS (
    SELECT 
        d.reg_ans, 
        d.trimestre,
        SUM(d.valor) as total_op,
        m.media_geral_tri
    FROM despesas d
    JOIN media_mensal m ON d.trimestre = m.trimestre
    GROUP BY d.reg_ans, d.trimestre, m.media_geral_tri
)
SELECT o.razao_social, COUNT(*) as qtd_trimestres_acima
FROM desempenho d
JOIN operadoras o ON d.reg_ans = o.reg_ans
WHERE d.total_op > d.media_geral_tri
GROUP BY o.razao_social
HAVING COUNT(*) >= 2;