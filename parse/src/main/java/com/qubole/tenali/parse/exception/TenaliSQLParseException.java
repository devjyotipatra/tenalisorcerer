package com.qubole.tenali.parse.exception;

/**
 * Created by devjyotip on 5/12/18.
 */
public class SQLParseException extends RuntimeException {

  private final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(SQLParseException.class);

  /**
   * SQL Parser Error Types
   */
  public enum SQLParserErrorType {
    SQLPARSEERROR,
    VALIDATEERROR,
    RELCONVERSIONERROR
  }


  public SQLParseException() {
    super();
  }

  public SQLParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public SQLParseException(String message) {
    super(message);
  }

  public SQLParseException(Throwable cause) {
    super(cause);
  }

  public SQLParseException(String message, SQLParseException ex) {
    super(message + "\n" + ex.getMessage(), ex.getCause());
  }
}
