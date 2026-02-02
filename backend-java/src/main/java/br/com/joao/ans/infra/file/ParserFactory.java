package br.com.joao.ans.infra.file;

import java.util.Arrays;
import java.util.List;

public class ParserFactory {

    private final List<FileParserStrategy> estrategias;

    public ParserFactory() {
        this.estrategias = Arrays.asList(new TextFileParser());
    }

    public FileParserStrategy obterParser(String nomeArquivo) {
        return estrategias.stream()
                .filter(e -> e.suporta(nomeArquivo))
                .findFirst()
                .orElse(null);
    }
}