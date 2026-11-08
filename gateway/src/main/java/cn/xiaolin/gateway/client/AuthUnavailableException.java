package cn.xiaolin.gateway.client;

public class AuthUnavailableException extends RuntimeException {

    public AuthUnavailableException(String message) {
        super(message);
    }

    public AuthUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
