package br.com.joao;

import br.com.joao.ans.app.AnsDownloadApp;
import br.com.joao.ans.client.AnsClientImpl;
import br.com.joao.ans.exception.AnsConnectionException;
import br.com.joao.ans.exception.AnsDataNotFoundException;
import br.com.joao.ans.exception.AnsProcessingException;
import br.com.joao.ans.infra.file.CadastroCsvParser;
import br.com.joao.ans.processor.AnsCsvProcessor;
import br.com.joao.ans.processor.filters.FiltroDespesaContabil;
import br.com.joao.ans.service.AnsAggregationService;
import br.com.joao.ans.service.AnsConsolidationService;
import br.com.joao.ans.service.AnsEnrichmentService;
import br.com.joao.ans.service.CadastroService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            String urlDemonstracoes = "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/";
            String urlCadastro = "https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/";

            Path pastaDownloads = Paths.get("downloads_ans");
            Path arquivoConsolidado = Paths.get("consolidado_despesas.csv");
            Path arquivoFinal = Paths.get("despesas_enriquecidas.csv");
            Path arquivoAgregado = Paths.get("despesas_agregadas.csv");

            AnsClientImpl clientDemonstracoes = new AnsClientImpl(urlDemonstracoes);
            AnsClientImpl clientCadastro = new AnsClientImpl(urlCadastro);

            AnsCsvProcessor csvProcessor = new AnsCsvProcessor(new FiltroDespesaContabil());
            CadastroCsvParser cadastroParser = new CadastroCsvParser();

            AnsDownloadApp downloadApp = new AnsDownloadApp(clientDemonstracoes);

            AnsConsolidationService consolidationService = new AnsConsolidationService(csvProcessor);

            CadastroService cadastroService = new CadastroService(clientCadastro, cadastroParser);

            AnsEnrichmentService enrichmentService = new AnsEnrichmentService(cadastroService);
            AnsAggregationService aggregationService = new AnsAggregationService();

            downloadApp.executar(pastaDownloads);

            consolidationService.executar(pastaDownloads, arquivoConsolidado);

            logger.info(">>> Iniciando Fase 2: Enriquecimento...");
            enrichmentService.executar(arquivoConsolidado, pastaDownloads, arquivoFinal);
            aggregationService.executar(arquivoFinal, arquivoAgregado);

            logger.info("=== FIM DO PROCESSO ===");
            logger.info("Arquivo gerado: " + arquivoFinal.toAbsolutePath());


        } catch (AnsConnectionException e) {
            logger.log(Level.SEVERE, "Erro de conexão com a ANS: {0}", e.getMessage());

        } catch (AnsDataNotFoundException e) {
            logger.log(Level.WARNING, "Dados não encontrados: {0}", e.getMessage());

        } catch (AnsProcessingException e) {
            logger.log(Level.SEVERE, "Erro na Fase de Processamento: {0}", e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro inesperado na aplicação", e);

        }
    }
}