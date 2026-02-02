package br.com.joao.ans.client;

import java.nio.file.Path;
import java.util.List;

public interface AnsClient {
    List<String> listarArquivos(String subDiretorio);
    Path baixarArquivo(String url, Path destino);
}