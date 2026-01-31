package br.com.joao.ans.infra.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileParserStrategy {
    boolean suporta(String nomeArquivo);

    List<String[]> parse(InputStream inputStream, String termoOtimizacao) throws IOException;
}