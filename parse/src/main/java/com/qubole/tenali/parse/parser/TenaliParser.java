package com.qubole.tenali.parse.parser;

import com.qubole.tenali.parse.parser.config.TenaliType;

import java.io.IOException;

/**
 * Created by devjyotip on 5/28/18.
 */
public interface TenaliParser {

    void parse(String command) throws IOException;
}
