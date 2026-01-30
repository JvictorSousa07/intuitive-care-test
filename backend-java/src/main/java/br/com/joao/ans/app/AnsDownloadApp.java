package br.com.joao.ans.app;

import br.com.joao.ans.client.AnsClient;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class AnsDownloadApp {

    private static final Logger logger = Logger.getLogger(AnsDownloadApp.class.getName());

    private final AnsClient client;


    public AnsDownloadApp(AnsClient client) {
        this.client = client;
    }

    public void executar(Path pastaDestino) throws Exception {
        logger.info("=== INICIANDO DOWNLOAD ANS ===");

        List<String> links = client.buscarLinksDosUltimos3Trimestres();

        if (links.isEmpty()) {
            throw new IllegalStateException("Nenhum link encontrado");
        }

        for (String link : links) {
            client.baixarArquivo(link, pastaDestino);
        }

        logger.info("=== DOWNLOADS FINALIZADOS ===");
    }
}
