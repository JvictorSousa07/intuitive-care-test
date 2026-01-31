package br.com.joao.ans.processor.filters;

import br.com.joao.ans.domain.ContaContabil;

public class FiltroDespesaContabil implements CsvFiltro {

    @Override
    public boolean aceitar(String[] colunas) {
        return colunas != null &&
                colunas.length > 2 &&
                colunas[2].replace("\"", "").equals(ContaContabil.DESPESAS_EVENTOS_SINISTROS.getCodigo());
    }

    @Override
    public String getTermoOtimizacao() {
        return ContaContabil.DESPESAS_EVENTOS_SINISTROS.getCodigo();
    }
}