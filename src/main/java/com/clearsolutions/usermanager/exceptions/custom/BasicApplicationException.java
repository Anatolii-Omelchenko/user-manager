package com.clearsolutions.usermanager.exceptions.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom application exception used as a base for other exceptions in the application.
 * Extends RuntimeException and holds an HttpStatus code.
 */
@Getter
public class BasicApplicationException extends RuntimeException {

    /**
     * The HttpStatus associated with the exception.
     */
    private final HttpStatus httpStatus;

    /**
     * Additional information about the exception.
     * This field holds details about the class and method where the exception was thrown.
     */
    private final String additionalInfo;

    /**
     * Constructs BasicApplicationException object
     * with the specified message and HTTP status.
     *
     * @param message        Error message explaining the reason for the exception.
     * @param httpStatus     The HTTP status associated with the exception.
     */
    public BasicApplicationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.additionalInfo = getAdditionalInfo();
    }

    /**
     * Retrieves additional information about the exception.
     *
     * @return A string with information about where the exception occurred.
     */
    private String getAdditionalInfo() {
        StackTraceElement[] stackTraceElements = this.getStackTrace();
        if (stackTraceElements.length > 0) {
            StackTraceElement firstStackTraceElement = stackTraceElements[0];
            String className = firstStackTraceElement.getClassName();
            String methodName = firstStackTraceElement.getMethodName();
            int lineNumber = firstStackTraceElement.getLineNumber();
            return "\n Exception was thrown in: " + className + "." + methodName + "() at line " + lineNumber;
        } else return "\n No additional info.";
    }

    @Override
    public String getMessage() {
        return super.getMessage().concat(additionalInfo);
    }
}
