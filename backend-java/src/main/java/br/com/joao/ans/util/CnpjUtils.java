package br.com.joao.ans.util;

public class CnpjUtils {

    private CnpjUtils() {}

    public static String limpar(String cnpj) {
        if (cnpj == null) return "";
        return cnpj.replaceAll("\\D", "");
    }

    public static boolean isValido(String cnpj) {
        String cnpjLimpo = limpar(cnpj);

        if (cnpjLimpo.length() != 14 || cnpjLimpo.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            char dig13 = calcularDigito(cnpjLimpo, 12);
            char dig14 = calcularDigito(cnpjLimpo, 13);

            return dig13 == cnpjLimpo.charAt(12) && dig14 == cnpjLimpo.charAt(13);
        } catch (Exception e) {
            return false;
        }
    }

    public static String formatar(String cnpj) {
        String c = limpar(cnpj);
        if (c.length() != 14) return cnpj;
        return c.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    private static char calcularDigito(String str, int pesoMax) {
        int soma = 0;
        int peso = 2;

        for (int i = pesoMax - 1; i >= 0; i--) {
            int num = (str.charAt(i) - '0');
            soma += num * peso;
            peso++;
            if (peso > 9) peso = 2;
        }

        int r = soma % 11;
        if (r < 2) return '0';
        return (char) ((11 - r) + '0');
    }
}