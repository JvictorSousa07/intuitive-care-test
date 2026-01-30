package br.com.joao;

import br.com.joao.ans.app.AnsDownloadApp;
import br.com.joao.ans.client.AnsClient;
import br.com.joao.ans.exception.AnsConnectionException;
import br.com.joao.ans.exception.AnsDataNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
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

        } catch (AnsConnectionException e) {
            logger.log(Level.SEVERE, "Erro de conexão com a ANS: {0}", e.getMessage());

        } catch (AnsDataNotFoundException e) {
            logger.log(Level.WARNING, "Dados não encontrados: {0}", e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro inesperado na aplicação", e);

        }
    }
}