package br.com.joao.ans.processor;

import br.com.joao.ans.infra.file.FileParserStrategy;
import br.com.joao.ans.infra.file.ParserFactory;
import br.com.joao.ans.processor.filters.CsvFiltro;
import br.com.joao.ans.processor.filters.FiltroDespesaContabil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AnsCsvProcessor {

    private static final Logger logger = Logger.getLogger(AnsCsvProcessor.class.getName());


    private final CsvFiltro filtro;
    private final ParserFactory parserFactory;

    public AnsCsvProcessor(CsvFiltro filtro) {
        this.filtro = filtro;
        this.parserFactory = new ParserFactory();
    }

    public AnsCsvProcessor() {
        this(new FiltroDespesaContabil());
    }

    public List<String[]> processarZip(Path arquivoZip) throws IOException {
        List<String[]> resultadoTotal = new ArrayList<>();
        String termoRapido = filtro.getTermoOtimizacao();

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(arquivoZip))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (entry.isDirectory()) {
                    zis.closeEntry();
                    continue;
                }

                FileParserStrategy parser = parserFactory.obterParser(entry.getName());

                if (parser != null) {
                    logger.info("   -> Processando compatível: " + entry.getName());

                    List<String[]> linhasCruas = parser.parse(zis, termoRapido);

                    for (String[] colunas : linhasCruas) {
                        if (filtro.aceitar(colunas)) {
                            resultadoTotal.add(colunas);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        return resultadoTotal;
    }
}