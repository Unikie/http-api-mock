package com.centeractive.ws;

/**
 * Top-level exception type thrown by soap-ws
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SoapException(String s) {
        super(s);
    }

    public SoapException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SoapException(Throwable throwable) {
        super(throwable.getMessage(), throwable);
    }
}
