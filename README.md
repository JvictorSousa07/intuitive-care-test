# Intuitive Care - Desafio Técnico (Backend Java)

Este repositório contém a solução para o teste técnico de Backend da Intuitive Care. O projeto foi desenvolvido em **Java**.

## Status do Projeto
- [x] **Tarefa 1**: Integração, Processamento e Consolidação (ETL)
- [x] **Tarefa 2**: Transformação, Validação e Enriquecimento
- [ ] **Tarefa 3**: Modelagem de Dados e SQL
- [ ] **Tarefa 4**: API e Visualização (Frontend)

---

## Arquitetura e Design Patterns

1.  **Layered Architecture (Camadas):**
    * `app`: Entrada da aplicação.
    * `service`: Regras de Negócio e Orquestração (`AnsEnrichmentService`, `CadastroService`).
    * `client`: Abstração de chamadas externas (HTTP/FTP).
    * `domain`: Entidades e Objetos de Valor (`Operadora`, `UF`, `OperadoraStats`).
    * `infra`: Implementações de baixo nível (File I/O, Parsers).

2.  **Injeção de Dependência (Manual):**
    * A classe `Main` atua como o orquestrador (*Root Composition*), instanciando as dependências (`Parsers`, `Clients`) e injetando-as nos `Services` via construtor. Isso eliminou o acoplamento forte e aumentou a testabilidade.

3.  **Enum Pattern & Type Safety:**
    * Utilização do Enum `UF` com lógica de *lookup* robusta. Isso impede que dados sujos (como datas ou números) sejam inseridos incorretamente nos campos de estado, garantindo a integridade do dado desde a leitura.

4.  **Strategy Pattern:**
    * Mantido no `FileParserStrategy` para flexibilidade na leitura de diferentes formatos dentro dos ZIPs.

---

## Trade-offs e Decisões Técnicas (Análise Crítica)

Abaixo estão as justificativas para as decisões de implementação das Tarefas 1 e 2:

### 1. Processamento: Memória vs. Streaming (Tarefa 1)
**Decisão:** Processamento Incremental (Streaming).
* **Solução:** Utilização de `ZipInputStream` combinado com `BufferedReader`.
* **Justificativa:** Os arquivos da ANS podem ser grandes. Carregar tudo em memória causaria `OutOfMemoryError`. O uso de Streams mantém o consumo de RAM constante (O(1)), independente do tamanho do arquivo.

### 2. Estratégia de Enriquecimento/Join (Tarefa 2)
**Decisão:** *In-Memory Hash Join*.
* **Implementação:** O arquivo de cadastro (`Relatorio_cadop.csv`) é carregado inteiramente em um `HashMap<String, Operadora>`, onde a chave é o `REG_ANS`.
* **Justificativa:** O arquivo de cadastro é pequeno (alguns MBs) comparado ao volume de despesas. Carregá-lo em memória permite acesso O(1) durante o processamento. Isso é drasticamente mais rápido do que fazer *Nested Loops* (O(n*m)) ou consultas em banco linha a linha.
* **Tratamento de Chaves:** Implementada limpeza explícita de aspas (`"`) nas chaves para garantir o *match* exato entre os arquivos CSV.

### 3. Validação de CNPJ (Tarefa 2)
**Decisão:** *Soft Validation* (Validação sem descarte).
* **Implementação:** Classe `CnpjUtils` verifica os dígitos verificadores (Módulo 11). Se inválido, a linha é persistida com o status `INVALIDO`.
* **Justificativa:** Em sistemas financeiros, a integridade do valor contábil é prioritária. Descartar uma despesa milionária por um erro de digitação no cadastro geraria "furos" no balanço contábil. A melhor abordagem é persistir e marcar para auditoria.

### 4. Robustez na Leitura de Dados (Tarefa 2)
**Problema:** O layout do CSV da ANS variava, fazendo com que datas (`1998-12-17`) fossem lidas incorretamente como UFs (Estados).
**Solução:** Implementação de um parser inteligente com `Enum`. O sistema varre as colunas em busca de siglas válidas (ex: SP, RJ) e ignora qualquer valor que não seja um estado brasileiro válido.

---

## Como Executar

### Pré-requisitos
* Java 11 ou superior
* Maven

### Passo a Passo
1.  Clone o repositório.
2.  Na raiz do projeto, execute a classe principal:
    ```bash
    br.com.joao.Main
    ```
3.  **O Fluxo de Execução:**
    * **Passo 1 (ETL):** O sistema baixa os ZIPs da ANS e gera o arquivo `consolidado_despesas.csv` (Dados brutos).
    * **Passo 2 (Enriquecimento):** Baixa o cadastro de operadoras, cruza os dados pelo `REG_ANS`, valida os CNPJs e gera o arquivo **`despesas_enriquecidas.csv`**.
    * **Passo 3 (Agregação):** Agrupa os valores por Operadora/UF, calcula média trimestral e desvio padrão, gerando o arquivo final **`despesas_agregadas.csv`**.

---