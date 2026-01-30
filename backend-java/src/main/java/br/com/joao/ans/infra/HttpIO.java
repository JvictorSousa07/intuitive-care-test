package br.com.joao.ans.infra;

import br.com.joao.ans.exception.AnsConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class HttpIO {

    private final HttpClient client;

    public HttpIO(HttpClient client) {
        this.client = client;
    }

    public HttpIO() {
        this(HttpClient.newHttpClient());
    }

    public String baixarHtml(String url){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            validarStatus(response.statusCode(), url);

            return response.body();

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            throw new AnsConnectionException("Falha de conexão ao acessar: " + url, e);
        }
    }

    public Path baixarArquivo(String url, Path destino){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            validarStatus(response.statusCode(), url);

            if (!Files.exists(destino)) {
                Files.createDirectories(destino);
            }

            String nomeArquivo = url.substring(url.lastIndexOf("/") + 1);
            Path arquivo = destino.resolve(nomeArquivo);

            Files.copy(response.body(), arquivo, StandardCopyOption.REPLACE_EXISTING);
            return arquivo;

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            throw new AnsConnectionException("Falha ao baixar arquivo: " + url, e);
        }
    }

    private void validarStatus(int statusCode, String url) {
        if (statusCode != 200) {
            throw new AnsConnectionException("Erro HTTP " + statusCode + " ao acessar " + url);
        }
    }
}