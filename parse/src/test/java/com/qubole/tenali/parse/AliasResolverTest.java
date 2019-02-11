package com.qubole.tenali.parse;

import com.qubole.tenali.util.SqlCommandTestHelper;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AliasResolverTest {

    @Test
    public void testSimpleSelectQuery() throws Exception {
        String command = "select account_id, tab1.tag, tab2.id from rstore.query_hists tab1 join rstore.cluster_nodes  tab2 on tab1.account_id=tab2.account_id where tab2.account_id>0";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePresto(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleJoinQuery() throws Exception {
        String command = "select a, tab.b from table1 tab1 join table2 tab2 on tab1.x=tab2.y where tab.a>0";

        //{"type":"select","from":{"type":"join","joinType":"INNER","leftNode":{"type":"as","aliasName":"TAB1","value":{"type":"identifier","name":"TABLE1"}},"rightNode":{"type":"as","aliasName":"TAB2","value":{"type":"identifier","name":"TABLE2"}},"joinCondition":{"type":"operator","operator":"=","operands":{"type":"list","operandlist":[{"type":"identifier","name":"TAB1.X"},{"type":"identifier","name":"TAB2.Y"}]}}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":89793374}

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePresto(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }
}
