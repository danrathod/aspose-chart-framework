package com.chartframework.exception;

/**
 * Thrown when a {@link com.chartframework.model.ChartRequest} fails
 * pre-flight validation (missing fields, invalid coordinates, empty data, etc.).
 */
public class ChartValidationException extends ChartFrameworkException {

    public ChartValidationException(String message) {
        super(message);
    }

    public ChartValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
