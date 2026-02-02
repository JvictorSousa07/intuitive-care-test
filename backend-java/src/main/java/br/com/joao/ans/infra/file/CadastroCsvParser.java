package br.com.joao.ans.infra.file;

import br.com.joao.ans.domain.Operadora;
import br.com.joao.ans.domain.UF;
import br.com.joao.ans.util.CnpjUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CadastroCsvParser {

    public Map<String, Operadora> parse(Path arquivo) throws IOException {
        Map<String, Operadora> mapaOperadoras = new HashMap<>();

        try (Stream<String> linhas = Files.lines(arquivo, Charset.forName("ISO-8859-1"))) {
            linhas.skip(1)
                    .forEach(linha -> parseLinha(linha, mapaOperadoras));
        }

        return mapaOperadoras;
    }

    private void parseLinha(String linha, Map<String, Operadora> mapa) {
        String[] colunas = linha.split(";");

        if (colunas.length >= 3) {
            String regAns = limpar(colunas[0]);
            String cnpj = limpar(colunas[1]);
            String razao = limpar(colunas[2]);

            UF uf = UF.ND;

            for (String col : colunas) {
                uf = UF.from(limpar(col)).orElse(null);
                if (uf != null) {
                    break;
                }
            }

            if (uf == null) {
                uf = UF.ND;
            }

            String modalidade = "Médico-Hospitalar";

            Operadora op = new Operadora(
                    regAns,
                    CnpjUtils.limpar(cnpj),
                    razao,
                    modalidade,
                    uf.name()
            );

            mapa.put(regAns, op);
        }
    }

    private String limpar(String valor) {
        if (valor == null) return "";
        return valor.replace("\"", "").trim();
    }
}
