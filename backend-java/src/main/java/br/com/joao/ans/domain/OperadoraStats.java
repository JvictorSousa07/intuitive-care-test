package br.com.joao.ans.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class OperadoraStats {
    private final String razaoSocial;
    private final String uf;
    private final List<BigDecimal> valores = new ArrayList<>();

    public OperadoraStats(String razaoSocial, String uf) {
        this.razaoSocial = razaoSocial;
        this.uf = uf;
    }

    public void adicionarValor(BigDecimal valor) {
        this.valores.add(valor);
    }

    public BigDecimal getTotal() {
        return valores.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMedia() {
        if (valores.isEmpty()) return BigDecimal.ZERO;
        return getTotal().divide(BigDecimal.valueOf(valores.size()), MathContext.DECIMAL128)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDesvioPadrao() {
        if (valores.size() <= 1) return BigDecimal.ZERO;

        BigDecimal media = getMedia();
        BigDecimal somaQuadrados = BigDecimal.ZERO;

        for (BigDecimal valor : valores) {
            BigDecimal diferenca = valor.subtract(media);
            somaQuadrados = somaQuadrados.add(diferenca.pow(2));
        }

        BigDecimal variancia = somaQuadrados.divide(BigDecimal.valueOf(valores.size() - 1), MathContext.DECIMAL128);

        return variancia.sqrt(MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
    }

    public String getRazaoSocial() { return razaoSocial; }
    public String getUf() { return uf; }
}