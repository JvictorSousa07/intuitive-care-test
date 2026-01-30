package br.com.joao;

import br.com.joao.ans.app.AnsDownloadApp;
import br.com.joao.ans.client.AnsClient;
import br.com.joao.ans.infra.HttpIO; // Importar

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            String baseUrl = "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/";
            Path pastaDownloads = Paths.get("downloads_ans");

            AnsClient client = new AnsClient(baseUrl);

            AnsDownloadApp app = new AnsDownloadApp(client);

            app.executar(pastaDownloads);

        } catch (Exception e) {
            logger.severe("Erro fatal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}