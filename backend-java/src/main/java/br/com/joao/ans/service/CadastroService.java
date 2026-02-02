package br.com.joao.ans.service;

import br.com.joao.ans.client.AnsClientImpl;
import br.com.joao.ans.domain.Operadora;
import br.com.joao.ans.infra.file.CadastroCsvParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CadastroService {

    private static final Logger logger = Logger.getLogger(CadastroService.class.getName());

    private final AnsClientImpl client;
    private final CadastroCsvParser parser;

    public CadastroService(AnsClientImpl client, CadastroCsvParser parser) {
        this.client = client;
        this.parser = parser;
    }

    public Map<String, Operadora> carregarCadastro(Path pastaDestino) throws IOException {
        logger.info(">>> Fase 2.2: Baixando Cadastro de Operadoras...");

        String arquivoAlvo = encontrarArquivoNoServidor();
        logger.info("   Arquivo identificado: " + arquivoAlvo);

        Path arquivoLocal = client.baixarArquivo(arquivoAlvo, pastaDestino);

        Map<String, Operadora> mapa = parser.parse(arquivoLocal);

        logger.info("Cadastro carregado: " + mapa.size() + " operadoras em memória.");
        return mapa;
    }

    private String encontrarArquivoNoServidor() {
        List<String> arquivos = client.listarArquivos("");

        return arquivos.stream()
                .filter(f -> f.toLowerCase().endsWith(".csv") || f.toLowerCase().endsWith(".zip"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum arquivo de cadastro encontrado na ANS."));
    }
}