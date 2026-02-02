package br.com.joao.ans.infra.scraping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnsHtmlScraper {

    public List<String> extrairLinksDeAnos(String html) {
        return extrair(html, "(\\d{4})/");
    }

    public List<String> extrairLinksDeZip(String html) {

        return extrair(html, "([^\"]*\\.zip)");
    }

    public List<String> extrairLinksGerais(String html) {
        return extrair(html, "[^\"]+");
    }

    private List<String> extrair(String html, String regexPattern) {
        List<String> links = new ArrayList<>();

        Pattern pattern = Pattern.compile("href=[\"'](" + regexPattern + ")[\"']");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String link = matcher.group(1);

            if (!link.startsWith("?") && !link.equals("/")) {
                links.add(link);
            }
        }
        return links;
    }
}