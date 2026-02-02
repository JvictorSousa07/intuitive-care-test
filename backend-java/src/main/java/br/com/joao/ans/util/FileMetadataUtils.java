package br.com.joao.ans.util;

public class FileMetadataUtils {

    private FileMetadataUtils() {}

    public static String extrairTrimestre(String nomeArquivo) {
        if (nomeArquivo == null) return "N/A";
        return nomeArquivo.contains("T") ? nomeArquivo.substring(0, 2) : "N/A";
    }

    public static String extrairAno(String nomeArquivo) {
        if (nomeArquivo == null) return "N/A";
        String ano = nomeArquivo.replaceAll("\\D+", "");
        return ano.isEmpty() ? "N/A" : ano;
    }
}