package br.com.joao;

import br.com.joao.ans.app.AnsDownloadApp;
import br.com.joao.ans.client.AnsClient;
import br.com.joao.ans.exception.AnsConnectionException;
import br.com.joao.ans.exception.AnsDataNotFoundException;
import br.com.joao.ans.processor.AnsCsvProcessor;
import br.com.joao.ans.processor.filters.FiltroDespesaContabil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            String baseUrl = "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/";
            Path pastaDownloads = Paths.get("downloads_ans");

            AnsClient client = new AnsClient(baseUrl);
            AnsDownloadApp app = new AnsDownloadApp(client);
            app.executar(pastaDownloads);

            logger.info(">>> Testando leitura dos ZIPs...");

            AnsCsvProcessor processor = new AnsCsvProcessor(new FiltroDespesaContabil());

            try (Stream<Path> paths = Files.list(pastaDownloads)) {
                paths.filter(p -> p.toString().endsWith(".zip"))
                        .forEach(zip -> {
                            try {
                                System.out.println("\nArquivo: " + zip.getFileName());

                                long inicio = System.currentTimeMillis();
                                List<String[]> linhas = processor.processarZip(zip);
                                long fim = System.currentTimeMillis();

                                logger.info("Processado em: " + (fim - inicio) + "ms");
                                logger.info("Linhas de 'Evento/Sinistro' encontradas: " + linhas.size());

                                if (!linhas.isEmpty()) {
                                    logger.info("Amostra (Linha 1): " + Arrays.toString(linhas.get(0)));
                                } else {
                                    logger.warning("Nenhuma linha encontrada neste arquivo.");
                                }

                            } catch (Exception e) {
                                logger.log(Level.SEVERE, " Erro ao ler arquivo " + zip.getFileName(), e);
                            }
                        });
            }


        } catch (AnsConnectionException e) {
            logger.log(Level.SEVERE, "Erro de conexão com a ANS: {0}", e.getMessage());

        } catch (AnsDataNotFoundException e) {
            logger.log(Level.WARNING, "Dados não encontrados: {0}", e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro inesperado na aplicação", e);

        }
    }
}