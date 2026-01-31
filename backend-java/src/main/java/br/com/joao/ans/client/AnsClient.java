package br.com.joao.ans.client;

import br.com.joao.ans.exception.AnsDataNotFoundException;
import br.com.joao.ans.infra.HttpIO;
import br.com.joao.ans.infra.scraping.AnsHtmlScraper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnsClient {

    private final String baseUrl;
    private final HttpIO http;
    private final AnsHtmlScraper parser;

    public AnsClient(String baseUrl, HttpIO http, AnsHtmlScraper parser) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.http = http;
        this.parser = parser;
    }

    public AnsClient(String baseUrl) {
        this(baseUrl, new HttpIO(), new AnsHtmlScraper());
    }

    public List<String> buscarLinksDosUltimos3Trimestres() {
        String htmlPrincipal = http.baixarHtml(baseUrl);

        List<String> anos = parser.extrairLinksDeAnos(htmlPrincipal);
        anos.sort(Collections.reverseOrder());

        List<String> links = new ArrayList<>();

        for (String anoBruto : anos) {
            if (links.size() >= 3) break;

            String ano = anoBruto.replace("/", "");

            String htmlAno = http.baixarHtml(baseUrl + ano + "/");
            List<String> zips = parser.extrairLinksDeZip(htmlAno);
            zips.sort(Collections.reverseOrder());

            for (String zip : zips) {
                if (links.size() >= 3) break;
                if (zip.toUpperCase().contains("T" + ano)) {
                    links.add(baseUrl + ano + "/" + zip);
                }
            }
        }

        if (links.isEmpty()) {
            throw new AnsDataNotFoundException("Nenhum arquivo ZIP de trimestre encontrado nos anos: " + anos);
        }
        return links;
    }

    public Path baixarArquivo(String url, Path destino) {
        return http.baixarArquivo(url, destino);
    }
}
