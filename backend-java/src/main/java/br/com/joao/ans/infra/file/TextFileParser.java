package br.com.joao.ans.infra.file;

import br.com.joao.ans.util.CsvUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextFileParser implements FileParserStrategy {

    @Override
    public boolean suporta(String nomeArquivo) {
        if (nomeArquivo == null) return false;
        String nome = nomeArquivo.toLowerCase();
        return nome.endsWith(".csv") || nome.endsWith(".txt");
    }

    @Override
    public List<String[]> parse(InputStream inputStream, String termoOtimizacao) throws IOException {
        List<String[]> linhas = new ArrayList<>();


        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));


        String linha;
        while ((linha = reader.readLine()) != null) {
            if (termoOtimizacao == null || linha.contains(termoOtimizacao)) {
                String separador = linha.indexOf(';') != -1 ? ";" : ",";
                linhas.add(CsvUtils.parse(linha, separador));
            }
        }
        return linhas;
    }
}