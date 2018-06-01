package com.qubole.tenali.parse.exception;

import org.apache.commons.lang.StringUtils;

/**
 * Created by devjyotip on 5/12/18.
 */
public class CommandParseError extends RuntimeException {
  private String header;
  private String errorLine;
  private int charPositionInLine;
  private int line;
  private String offendingToken;
  private String detailedMessage;

  public CommandParseError(String offendingToken,
                           int line, int charPositionInLine,
                           String msg,
                           String errorLine) {
    super(msg);
    this.offendingToken = offendingToken;
    this.charPositionInLine = charPositionInLine;
    this.line = line;
    this.errorLine = errorLine;
    this.header = msg;
  }

  @Override
  public String getMessage() {
    if (detailedMessage == null) {
      if (offendingToken != null) {
        detailedMessage = getDetailedMessageWithLocation();
      } else {
        StringBuilder sb = new StringBuilder();
        sb.append("ERROR: ").append(header).append("\n");
        sb.append("LINE: ").append(errorLine);
        detailedMessage = sb.toString();
      }
    }

    return detailedMessage;
  }

  public String getMessageHeader(){
    return this.header;
  }

  private String getDetailedMessageWithLocation() {
    StringBuilder sb = new StringBuilder();
    int displayLimit = 80;
    String queryPrefix = "LINE " + line + ":" + charPositionInLine + " ";
    String prefixPadding = StringUtils.repeat(" ", queryPrefix.length());
    String locationString;

    int tokenLength = offendingToken.length();
    if(tokenLength > 0){
      locationString = StringUtils.repeat(" ", charPositionInLine) + StringUtils.repeat("^", tokenLength);
    } else {
      locationString = StringUtils.repeat(" ", charPositionInLine) + "^";
    }

    sb.append("ERROR: ").append(header).append("\n");
    sb.append(queryPrefix);

    if (errorLine.length() > displayLimit) {
      int padding = (displayLimit / 2);

      String dotSuffix = " ... ";
      int startPos = locationString.length() - padding - 1;
      if (startPos <= 0) {
        startPos = 0;
        sb.append(errorLine.substring(startPos, displayLimit)).append(dotSuffix).append("\n");
        sb.append(prefixPadding).append(locationString);
      } else if (errorLine.length() - (locationString.length() + padding) <= 0) {
        startPos = errorLine.length() - displayLimit - 1;
        sb.append(dotSuffix).append(errorLine.substring(startPos)).append("\n");
        sb.append(prefixPadding).append(StringUtils.repeat(" ", dotSuffix.length()))
            .append(locationString.substring(startPos));
      } else {
        sb.append(dotSuffix).append(errorLine.substring(startPos, startPos + displayLimit)).append(dotSuffix).append("\n");
        sb.append(prefixPadding).append(StringUtils.repeat(" ", dotSuffix.length()))
            .append(locationString.substring(startPos));
      }
    } else {
      sb.append(errorLine).append("\n");
      sb.append(prefixPadding).append(locationString);
    }
    return sb.toString();
  }
}
