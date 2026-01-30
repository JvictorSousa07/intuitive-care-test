package br.com.joao.ans.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnsHtmlParser {

    public List<String> extrairLinksDeAnos(String html) {
        return extrair(html, "(\\d{4})/");
    }

    public List<String> extrairLinksDeZip(String html) {

        return extrair(html, "([^\"]*\\.zip)");
    }

    private List<String> extrair(String html, String regexPattern) {
        List<String> links = new ArrayList<>();

        Pattern pattern = Pattern.compile("href=[\"'](" + regexPattern + ")[\"']");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            links.add(matcher.group(1));
        }
        return links;
    }
}