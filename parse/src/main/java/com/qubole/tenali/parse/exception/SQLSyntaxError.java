package com.qubole.tenali.parse.exception;

/**
 * Created by devjyotip on 5/12/18.
 */
public class SQLSyntaxError extends TenaliSQLParseException {
    SQLParserErrorType errorType = SQLParserErrorType.SQLPARSEERROR;
    String errorMessage;
    String detailedMessage;
    CommandParseError parseError;

    public SQLSyntaxError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public SQLSyntaxError(CommandParseError e) {
        this.errorMessage = e.getMessageHeader();
        this.parseError = e;
    }

    public SQLSyntaxError(Exception e) {
        this.errorMessage = e.getMessage();
    }

    public SQLSyntaxError(String message, Exception e) {
        this.errorMessage = e.getMessage();
        this.detailedMessage = String.format("%s: %s\n", errorType.toString(),
                message, this.errorMessage);
    }

    @Override
    public String getMessage() {
        if (detailedMessage == null) {
            if (parseError != null) {
                detailedMessage = String.format("%s: %s\n %s",
                        errorType.toString(), errorMessage, parseError.getMessage());
            } else {
                detailedMessage = String.format("%s: %s\n", errorType.toString(),
                        errorMessage);
            }
        }
        return detailedMessage;
    }
}