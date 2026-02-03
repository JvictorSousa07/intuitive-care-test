package br.com.joao.ans.service;

import br.com.joao.ans.domain.OperadoraStats;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnsAggregationService {

    private static final Logger logger = Logger.getLogger(AnsAggregationService.class.getName());

    public void executar(Path arquivoEntrada, Path arquivoSaida) {
        logger.info(">>> Fase 2.3: Iniciando Agregação Estatística...");

        Map<String, OperadoraStats> acumulador = new HashMap<>();

        try (Stream<String> linhas = Files.lines(arquivoEntrada, StandardCharsets.UTF_8)) {
            linhas.skip(1)
                    .forEach(linha -> processarLinha(linha, acumulador));
        } catch (IOException e) {
            logger.severe("Erro ao ler arquivo: " + e.getMessage());
            return;
        }

        List<OperadoraStats> listaOrdenada = acumulador.values().stream()
                .sorted(Comparator.comparing(OperadoraStats::getTotal).reversed())
                .collect(Collectors.toList());

        try (BufferedWriter writer = Files.newBufferedWriter(arquivoSaida, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write("RAZAO_SOCIAL;UF;TOTAL_DESPESAS;MEDIA_TRIMESTRAL;DESVIO_PADRAO");
            writer.newLine();

            for (OperadoraStats stat : listaOrdenada) {
                writer.write(String.format("%s;%s;%s;%s;%s",
                        stat.getRazaoSocial(),
                        stat.getUf(),
                        stat.getTotal().toString().replace(".", ","),
                        stat.getMedia().toString().replace(".", ","),
                        stat.getDesvioPadrao().toString().replace(".", ",")));
                writer.newLine();
            }
            logger.info("Agregação concluída: " + arquivoSaida.toAbsolutePath());

        } catch (IOException e) {
            logger.severe("Erro ao salvar: " + e.getMessage());
        }
    }

    private void processarLinha(String linha, Map<String, OperadoraStats> acumulador) {
        try {
            String[] col = linha.split(";");
            if (col.length < 9) return;

            String razao = col[2].replace("\"", "").trim();
            String uf = col[4].replace("\"", "").trim();
            String valorStr = col[8].replace("\"", "").replace(",", ".").trim();

            if (razao.equals("NAO_ENCONTRADO") || valorStr.isEmpty()) return;

            BigDecimal valor = new BigDecimal(valorStr);
            String chave = razao + "|" + uf;

            acumulador.computeIfAbsent(chave, k -> new OperadoraStats(razao, uf))
                    .adicionarValor(valor);

        } catch (Exception e) {
        }
    }
}