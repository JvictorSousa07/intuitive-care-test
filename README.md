# Intuitive Care - Desafio Técnico (Backend Java)

Este repositório contém a solução para o teste técnico de Backend da Intuitive Care. O projeto foi desenvolvido em **Java**.

## Status do Projeto
- [x] **Tarefa 1**: Integração, Processamento e Consolidação (ETL)
- [ ] **Tarefa 2**: Transformação, Validação e Enriquecimento
- [ ] **Tarefa 3**: Modelagem de Dados e SQL
- [ ] **Tarefa 4**: API e Visualização (Frontend)

---

## Arquitetura e Design Patterns (Tarefa 1)

Para garantir que o código fosse resiliente a mudanças (como novos formatos de arquivos) e performático, foram aplicados os seguintes padrões:

1.  **Layered Architecture (Camadas):**
    * `app`: Entrada da aplicação (Download).
    * `service`: Orquestração e Regras de Negócio (Consolidação).
    * `processor`: Lógica de transformação e filtros (CSV Parsing).
    * `infra`: Comunicação com mundo externo (File I/O, HTTP Client).

2.  **Strategy Pattern:**
    * Utilizado no `FileParserStrategy` para permitir que o sistema leia diferentes formatos (CSV, TXT) sem alterar a lógica principal.

3.  **Factory Pattern:**
    * Utilizado no `ParserFactory` para decidir dinamicamente qual estratégia de leitura usar com base na extensão do arquivo dentro do ZIP.

4.  **Singleton (Enum):**
    * Uso de Enum para centralizar códigos contábeis (ex: `EVENTOS_SINISTROS`).

---

## Trade-offs e Decisões Técnicas (Análise Crítica)

Conforme solicitado no desafio, abaixo estão as justificativas para as decisões de implementação:

### 1. Processamento: Memória vs. Streaming
**Decisão:** Processamento Incremental (Streaming).
* **Contexto:** Os arquivos da ANS podem ser grandes e múltiplos. Carregar todo o conteúdo em memória (`List<String>`) poderia causar `OutOfMemoryError`.
* **Solução:** Utilização de `ZipInputStream` combinado com `BufferedReader` e `BufferedWriter`.
* **Justificativa:** Isso mantém o consumo de RAM baixo e constante (O(1)), independente se o arquivo tem 10MB ou 1GB.

### 2. Inconsistência de Dados: Ausência de CNPJ
**Problema:** A Tarefa 1.3 solicita as colunas `CNPJ` e `Razão Social` no arquivo consolidado. Contudo, os arquivos fonte ("Demonstrações Contábeis") possuem apenas o campo `REG_ANS` (Registro da Operadora).
**Decisão:** Manter a coluna `REG_ANS` como chave primária nesta etapa.
* **Justificativa:** O dado de CNPJ não existe na fonte primária desta etapa. Tentar "chutar" ou deixar em branco geraria dados sujos. A conversão correta de `REG_ANS` para `CNPJ` será realizada através de um **JOIN** seguro com os dados cadastrais na **Tarefa 2.2**, garantindo a integridade da informação.

### 3. Resiliência a Estrutura de Diretórios
**Decisão:** Varredura Recursiva.
* **Solução:** O processador ignora a estrutura de pastas internas do ZIP e busca arquivos compatíveis em qualquer nível de profundidade.
* **Justificativa:** O PDF alerta que "alguns trimestres podem ter estruturas de diretório diferentes". Essa abordagem blinda o sistema contra essas variações.

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
3.  **O que vai acontecer:**
    * O sistema criará a pasta `downloads_ans`.
    * Baixará os arquivos `.zip` do FTP da ANS.
    * Processará e filtrará as despesas.
    * Gerará o arquivo final **`consolidado_despesas.csv`** na raiz do projeto.

---

