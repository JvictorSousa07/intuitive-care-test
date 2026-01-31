package br.com.joao.ans.processor.filters;

public interface CsvFiltro {
    boolean aceitar(String[] colunas);

    default String getTermoOtimizacao() {
        return null;
    }
}
