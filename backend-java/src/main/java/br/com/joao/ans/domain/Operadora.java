package br.com.joao.ans.domain;

public class Operadora {
    private final String registroAns;
    private final String cnpj;
    private final String razaoSocial;
    private final String modalidade;
    private final String uf;

    public Operadora(String registroAns, String cnpj, String razaoSocial, String modalidade, String uf) {
        this.registroAns = registroAns;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.modalidade = modalidade;
        this.uf = uf;
    }

    public String getRegistroAns() { return registroAns; }
    public String getCnpj() { return cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getModalidade() { return modalidade; }
    public String getUf() { return uf; }
}