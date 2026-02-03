package br.com.joao.ans.infra.file;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class EnrichedCsvWriter implements AutoCloseable {

    private final BufferedWriter writer;

    public EnrichedCsvWriter(Path arquivoFinal) throws IOException {
        if (arquivoFinal.getParent() != null) {
            Files.createDirectories(arquivoFinal.getParent());
        }

        this.writer = Files.newBufferedWriter(arquivoFinal, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        writer.write("REG_ANS;CNPJ;RAZAO_SOCIAL;MODALIDADE;UF;DATA;TRIMESTRE;ANO;VALOR_DESPESA;STATUS_CNPJ");
        writer.newLine();
    }

    public void escreverLinha(String regAns, String cnpj, String razao, String modalidade, String uf,
                              String data, String trimestre, String ano, String valor, String statusCnpj) throws IOException {

        String linha = String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s",
                tratar(regAns), tratar(cnpj), tratar(razao), tratar(modalidade), tratar(uf),
                tratar(data), tratar(trimestre), tratar(ano), tratar(valor), tratar(statusCnpj));

        writer.write(linha);
        writer.newLine();
    }

    private String tratar(String valor) {
        return valor == null ? "" : valor;
    }

    @Override
    public void close() throws IOException {
        if (writer != null) writer.close();
    }
}