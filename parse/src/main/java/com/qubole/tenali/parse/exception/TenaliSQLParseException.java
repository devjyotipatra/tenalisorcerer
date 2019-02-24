package com.qubole.tenali.parse.exception;

/**
 * Created by devjyotip on 5/12/18.
 */
public class TenaliSQLParseException extends RuntimeException {

  private final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(TenaliSQLParseException.class);

  /**
   * SQL Parser Error Types
   */
  public enum SQLParserErrorType {
    SQLPARSEERROR,
    VALIDATEERROR,
    RELCONVERSIONERROR
  }


  public TenaliSQLParseException() {
    super();
  }

  public TenaliSQLParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public TenaliSQLParseException(String message) {
    super(message);
  }

  public TenaliSQLParseException(Throwable cause) {
    super(cause);
  }

  public TenaliSQLParseException(String message, TenaliSQLParseException ex) {
    super(message + "\n" + ex.getMessage(), ex.getCause());
  }
}
