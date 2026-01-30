package br.com.joao.ans.infra;

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

    public String baixarHtml(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro HTTP " + response.statusCode());
        }
        return response.body();
    }

    public Path baixarArquivo(String url, Path destino) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro HTTP " + response.statusCode());
        }

        Files.createDirectories(destino);
        Path arquivo = destino.resolve(url.substring(url.lastIndexOf("/") + 1));
        Files.copy(response.body(), arquivo, StandardCopyOption.REPLACE_EXISTING);
        return arquivo;
    }
}
