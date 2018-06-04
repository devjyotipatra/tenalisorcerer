package com.qubole.tenali.parse;

import com.qubole.tenali.util.LexerTestHelper;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ParseSQLTest {

    private final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ParseSQLTest.class);
    @Test
    public void TestSQL() {
        List<String> sqlQueries = listAllFiles("src/test/resources/");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("failedQueries.txt"));
            for (String sql : sqlQueries) {
                try {
                    LexerTestHelper.parse(sql);
                }
                catch(Exception e) {
                    logger.error("Error while parsing this sql : " + sql) ;
                    bufferedWriter.append(sql) ;
                    bufferedWriter.append("\n");
                    //e.printStackTrace();
                }
            }
            bufferedWriter.close();
        }
        catch(Exception e) {

        }

    }

    public String readContent(Path filePath) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        StringBuilder sb = new StringBuilder() ;
        for(String line: lines) {
            sb.append(line) ;
            sb.append(" ") ;
        }
        return sb.toString() ;
    }

    public List<String> listAllFiles(String path){
        logger.debug("In listAllfiles(String path) method");
        List<String> sqlQueries = new ArrayList<>() ;
        try(Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        sqlQueries.add(readContent(filePath));
                    } catch (Exception e) {
                        logger.error("Error while reading the file : " + filePath.getFileName());
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sqlQueries;
    }

}
