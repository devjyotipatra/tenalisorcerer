package com.qubole.tenali.parse;

import com.qubole.tenali.parse.config.CommandType;
import com.qubole.tenali.parse.config.QueryContext;
import com.qubole.tenali.parse.config.QueryType;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by devjyotip on 5/28/18.
 */
public interface TenaliParser {

    //ParseObject<T> parse(QueryType queryType, String command) throws IOException;

    QueryContext parse(QueryType queryType, String command) throws IOException;

    void prepare();

    /*public static class ParseObject<T> {
        T obj;

        CommandType commandType;

        QueryType queryType;

        String errorMessage;


        public ParseObject(CommandType commandType, QueryType queryType) {
            this.commandType = commandType;
            this.queryType = queryType;
        }


        public void setParseObject(T obj) {
            this.obj= obj;
        }


        public T getParseObject() {
            return obj;
        }

        public void setParseErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getParseErrorMessage() {
            return errorMessage;
        }


        public CommandType getCommandType() {
            return commandType;
        }

        public QueryType getQueryType() {
            return queryType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (getClass() != obj.getClass())) {
                return false;
            }

            ParseObject o = (ParseObject) obj;

            return Objects.equals(this.obj, o.obj) &&
                    Objects.equals(this.errorMessage, o.errorMessage);
        }

        @Override
        public int hashCode() {
            int hashCode;
            if(obj != null) {
                hashCode = obj.toString().hashCode();
            } else {
                hashCode = errorMessage.hashCode();
            }

            return hashCode;
        }


        @Override
        public String toString() {
            String str = null;
            if(obj != null) {
                str = obj.toString();
            } else {
                str = errorMessage;
            }

            return str;
        }
    }*/
}
