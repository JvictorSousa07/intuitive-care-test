package br.com.joao.ans.exception;

public class AnsProcessingException extends RuntimeException {

    public AnsProcessingException(String message) {
        super(message);
    }

    public AnsProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}