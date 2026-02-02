package br.com.joao.ans.service;

import br.com.joao.ans.exception.AnsProcessingException; // Import novo
import br.com.joao.ans.infra.file.AnsConsolidatedWriter;
import br.com.joao.ans.processor.AnsCsvProcessor;
import br.com.joao.ans.util.FileMetadataUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnsConsolidationService {

    private static final Logger logger = Logger.getLogger(AnsConsolidationService.class.getName());
    private final AnsCsvProcessor processor;

    public AnsConsolidationService(AnsCsvProcessor processor) {
        this.processor = processor;
    }

    public void executar(Path pastaEntrada, Path arquivoSaida) {
        logger.info(">>> Iniciando serviço de consolidação...");

        try {
            if (!Files.exists(pastaEntrada)) {
                throw new AnsProcessingException("Pasta de entrada não encontrada: " + pastaEntrada);
            }

            List<Path> arquivosZip = listarZips(pastaEntrada);
            if (arquivosZip.isEmpty()) {
                logger.warning(" Nenhum arquivo ZIP encontrado para processar em: " + pastaEntrada);
                return;
            }

            logger.info("Total de arquivos a processar: " + arquivosZip.size());

            try (AnsConsolidatedWriter writer = new AnsConsolidatedWriter(arquivoSaida)) {
                for (Path zip : arquivosZip) {
                    try {
                        processarArquivoIndividual(zip, writer);
                    } catch (Exception e) {
                        // Loga e continua (Resiliência), mas mantém o log severo
                        logger.log(Level.SEVERE, " Falha ao processar arquivo específico: " + zip.getFileName(), e);
                    }
                }
            }

            logger.info(" Consolidação finalizada com sucesso! Saída: " + arquivoSaida.toAbsolutePath());

        } catch (IOException e) {
            throw new AnsProcessingException("Falha crítica ao consolidar arquivos.", e);
        }
    }

    private List<Path> listarZips(Path pasta) throws IOException {
        try (Stream<Path> paths = Files.list(pasta)) {
            return paths.filter(p -> p.toString().toLowerCase().endsWith(".zip"))
                    .collect(Collectors.toList());
        }
    }

    private void processarArquivoIndividual(Path zip, AnsConsolidatedWriter writer) throws IOException {
        logger.info("   -> Processando: " + zip.getFileName());

        String nome = zip.getFileName().toString();
        String trimestre = FileMetadataUtils.extrairTrimestre(nome);
        String ano = FileMetadataUtils.extrairAno(nome);

        List<String[]> linhas = processor.processarZip(zip);

        int count = 0;
        for (String[] colunas : linhas) {
            if (colunas != null && colunas.length > 5) {
                writer.escreverLinha(colunas[1], colunas[0], trimestre, ano, colunas[5]);
                count++;
            }
        }
        logger.info("      Linhas importadas: " + count);
    }
}