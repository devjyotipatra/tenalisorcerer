package com.qubole.tenali.parse;

import java.io.IOException;

/**
 * Created by devjyotip on 5/28/18.
 */
public interface TenaliParser {

  public void parse(String command) throws IOException;
}
