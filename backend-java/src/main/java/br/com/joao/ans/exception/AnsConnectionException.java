package br.com.joao.ans.exception;

public class AnsConnectionException extends RuntimeException {
    public AnsConnectionException(String message) {
        super(message);
    }

    public AnsConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}