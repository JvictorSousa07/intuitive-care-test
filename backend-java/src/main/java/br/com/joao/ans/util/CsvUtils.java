package br.com.joao.ans.util;

public class CsvUtils {

    public static String[] parse(String linha, String separador) {
        if (linha == null || linha.isEmpty()) return new String[0];
        return linha.split(separador);
    }
}