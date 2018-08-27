package com.qubole.tenali.parse.parser;


import com.qubole.tenali.parse.parser.config.Command;
import com.qubole.tenali.parse.parser.config.TenaliType;

/**
 * Created by devjyotip on 5/18/18.
 */
public abstract class Parsers {

    public TenaliParser getParser(TenaliType type) {
        TenaliParser parser = null;

        if(type.getValue() instanceof TenaliType.CommandType) {
            Command command = (Command) type.getValue();

        }

        return parser;
    }
}

