package com.qubole.tenali.parse;

import com.qubole.tenali.util.SqlCommandTestHelper;
import org.junit.Test;

public class CalciteAstTransformationTest {

    @Test
    public void testSimpleOrderByQuery() throws Exception {
        String command = "select a, tab.b from table1 tab";

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePresto(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }
}
