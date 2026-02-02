package br.com.joao.ans.infra.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AnsConsolidatedWriter implements AutoCloseable {

    private final BufferedWriter writer;

    public AnsConsolidatedWriter(Path arquivoSaida) throws IOException {
        if (arquivoSaida.getParent() != null) {
            Files.createDirectories(arquivoSaida.getParent());
        }

        this.writer = Files.newBufferedWriter(arquivoSaida, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Cabeçalho
        writer.write("REG_ANS;DATA;TRIMESTRE;ANO;VALOR_DESPESA");
        writer.newLine();
    }

    public void escreverLinha(String regAns, String data, String trimestre, String ano, String valor) throws IOException {
        String sRegAns = regAns != null ? regAns : "";
        String sData = data != null ? data : "";
        String sValor = valor != null ? valor : "0";

        String linha = String.format("%s;%s;%s;%s;%s", sRegAns, sData, trimestre, ano, sValor);
        writer.write(linha);
        writer.newLine();
    }

    @Override
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}