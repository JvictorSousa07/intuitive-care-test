package br.com.joao.ans.domain;

public enum ContaContabil {

    DESPESAS_EVENTOS_SINISTROS("411411151");

    private final String codigo;

    ContaContabil(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

}