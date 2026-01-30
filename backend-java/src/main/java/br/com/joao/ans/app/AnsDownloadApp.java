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

    public void executar(Path pastaDestino) {
        logger.info("=== INICIANDO DOWNLOAD ANS ===");

        List<String> links = client.buscarLinksDosUltimos3Trimestres();

        for (String link : links) {
            logger.info("Baixando arquivo: " + link);
            client.baixarArquivo(link, pastaDestino);
        }

        logger.info("=== DOWNLOADS FINALIZADOS ===");
    }
}
