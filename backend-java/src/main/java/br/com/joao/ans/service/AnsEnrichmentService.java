package br.com.joao.ans.service;

import br.com.joao.ans.domain.Operadora;
import br.com.joao.ans.exception.AnsProcessingException;
import br.com.joao.ans.infra.file.EnrichedCsvWriter;
import br.com.joao.ans.util.CnpjUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class AnsEnrichmentService {

    private static final Logger logger = Logger.getLogger(AnsEnrichmentService.class.getName());
    private final CadastroService cadastroService;

    public AnsEnrichmentService(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    public void executar(Path arquivoConsolidado, Path pastaDownloads, Path arquivoFinal) {
        logger.info(">>> Fase 2: Iniciando Enriquecimento e Validação...");

        try {
            Map<String, Operadora> operadoras = cadastroService.carregarCadastro(pastaDownloads);

            try (Stream<String> linhas = Files.lines(arquivoConsolidado, StandardCharsets.UTF_8);
                 EnrichedCsvWriter writer = new EnrichedCsvWriter(arquivoFinal)) {

                linhas.skip(1).forEach(linha -> {
                    try {
                        enrichAndSave(linha, operadoras, writer);
                    } catch (IOException e) {
                        logger.severe("Erro ao processar linha: " + e.getMessage());
                    }
                });
            }

            logger.info(" Arquivo Final Gerado: " + arquivoFinal.toAbsolutePath());

        } catch (IOException e) {
            throw new AnsProcessingException("Erro crítico no enriquecimento de dados", e);
        }
    }

    private void enrichAndSave(String linha, Map<String, Operadora> operadoras, EnrichedCsvWriter writer) throws IOException {
        String[] colunas = linha.split(";");
        if (colunas.length < 5) return;

        String regAnsOriginal = colunas[0];
        String regAnsChave = regAnsOriginal.replace("\"", "").trim(); // Chave limpa para busca

        String data = colunas[1];
        String trimestre = colunas[2];
        String ano = colunas[3];
        String valor = colunas[4];

        Operadora op = operadoras.get(regAnsChave);

        if (op != null) {
            String cnpj = op.getCnpj();
            boolean valido = CnpjUtils.isValido(cnpj);
            String status = valido ? "VALIDO" : "INVALIDO";

            writer.escreverLinha(regAnsChave, cnpj, op.getRazaoSocial(), op.getModalidade(), op.getUf(),
                    data, trimestre, ano, valor, status);
        } else {
            writer.escreverLinha(regAnsChave, "NAO_ENCONTRADO", "NAO_ENCONTRADO", "", "",
                    data, trimestre, ano, valor, "DESCONHECIDO");
        }
    }
}