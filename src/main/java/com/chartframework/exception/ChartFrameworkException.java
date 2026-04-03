package com.chartframework.exception;

/**
 * Base runtime exception for all chart framework errors.
 * Using an unchecked exception keeps the public API clean while
 * still allowing callers to handle framework-specific failures if needed.
 */
public class ChartFrameworkException extends RuntimeException {

    public ChartFrameworkException(String message) {
        super(message);
    }

    public ChartFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
