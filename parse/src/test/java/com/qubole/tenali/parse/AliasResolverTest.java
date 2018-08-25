package com.qubole.tenali.parse;

import com.qubole.tenali.util.TestHelper;
import org.junit.Test;

public class AliasResolverTest {

    @Test
    public void testSimpleSelectQuery() throws Exception {
        String command = "select a, tab.b from table1 tab where tab.a>0";

        TestHelper.parse(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }
}
