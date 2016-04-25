package com.centeractive.ws;

/**
 * Default exception thrown by the SoapBuilder.
 *
 * @author Tom Bujok
 * @since 1.0.0
 */
public class SoapBuilderException extends SoapException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SoapBuilderException(String message) {
        super(message);
    }

    public SoapBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoapBuilderException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
