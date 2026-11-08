package cn.xiaolin.has.client;

/**
 * Core internal API could not be used. Callers must not treat this as a successful write.
 */
public class CoreUnavailableException extends RuntimeException {

    public CoreUnavailableException(String message) {
        super(message);
    }

    public CoreUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
