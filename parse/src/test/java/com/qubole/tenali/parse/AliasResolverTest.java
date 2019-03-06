package com.qubole.tenali.parse;

import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import com.qubole.tenali.util.SqlCommandTestHelper;
import org.apache.calcite.sql.SqlNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AliasResolverTest {

    @Test
    public void testSimpleJoinQuery() throws Exception {
        String command = "select tab2.account_id, tab1.tag, tab2.id, user_loc from rstore.query_hists tab1 join rstore.cluster_nodes  tab2 on tab1.account_id=tab2.account_id where tab2.account_id>0";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformPrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testComplexJoinQuery1() throws Exception {
        String command = "select s2.acc_id, tag1, s1.id from (select tab1.account_id acc_id, tab1.tag tag1, tab2.id from rstore.query_hists tab1 join rstore.cluster_nodes  tab2 on tab1.account_id=tab2.account_id where tab2.account_id>0) s1 ";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testComplexJoinQuery2() throws Exception {
        String command = "select s2.acc_id, tag1, s1.id from (select tab1.account_id acc_id, tab1.tag tag1, tab2.id from rstore.query_hists tab1 join rstore.cluster_nodes  tab2 on tab1.account_id=tab2.account_id where tab2.account_id>0) s1 " +
                "join (select id, account_id as acc_id, created_at, deleted_at from rstore.clusters where id>100) s2 on s1.acc_id=s2.acc_id";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testComplexJoinQuery3() throws Exception {
        String command = "SELECT language, account_id, count(*) as cnt FROM \n" +
                "(SELECT h.id AS id, h.dt, h.account_id, h.status, language, coalesce(sql, program, cmdline) as sql FROM rstore.query_hists h JOIN rstore.spark_commands s ON h.command_id=s.id and h.dt=s.dt AND h.dt>='2019-02-01' AND status='done') y\n" +
                "LEFT JOIN\n" +
                "(SELECT DISTINCT query_hist_id, sql_app_id, command_id\n" +
                "   FROM (SELECT account_id, command_id, cluster_id, cluster_inst_id, json_extract(event_data,'$.executedQueryInfo.applicationId') as sql_app_id,  \n" +
                "\t\t json_extract(event_data,'$.executedQueryInfo.queryhistId') as query_hist_id, json_extract(event_data,'$.executedQueryInfo.status') AS status, json_extract(event_data,'$.executedQueryInfo.timeTakenMs') as time_taken FROM processed_v2.spark WHERE event_date>='2019-02-01' AND event_type='CLUSTER.SPARK.METRICS.SQL')s\n" +
                "    LEFT JOIN\n" +
                "    (SELECT json_extract(event_data,'$.id') AS metric_app_id FROM processed_v2.spark WHERE event_date>='2019-02-01' AND \n" +
                "     event_type='CLUSTER.SPARK.METRICS') t\n" +
                "    ON s.sql_app_id = t.metric_app_id) x\n" +
                "ON x.query_hist_id = y.id\n" +
                "WHERE query_hist_id is not NULL and sql is not null and length(sql)>50\n" +
                "group by language, account_id\n" +
                "order by cnt desc";
        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testSimpleSelectQuery() throws Exception {
        String command = "select account_id, hist.id from rstore.query_hists hist where hist.dt>0";

        //{"type":"select","from":{"type":"join","joinType":"INNER","leftNode":{"type":"as","aliasName":"TAB1","value":{"type":"identifier","name":"TABLE1"}},"rightNode":{"type":"as","aliasName":"TAB2","value":{"type":"identifier","name":"TABLE2"}},"joinCondition":{"type":"operator","operator":"=","operands":{"type":"list","operandlist":[{"type":"identifier","name":"TAB1.X"},{"type":"identifier","name":"TAB2.Y"}]}}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":89793374}

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleSubQuery() throws Exception {
        String command = "select acc_id, tmp.id, dt, tmp.tag from (select account_id as acc_id, hist.id, tag, dt, hist.submit_time from rstore.query_hists hist where hist.dt>0) tmp";

        //{"type":"select","from":{"type":"join","joinType":"INNER","leftNode":{"type":"as","aliasName":"TAB1","value":{"type":"identifier","name":"TABLE1"}},"rightNode":{"type":"as","aliasName":"TAB2","value":{"type":"identifier","name":"TABLE2"}},"joinCondition":{"type":"operator","operator":"=","operands":{"type":"list","operandlist":[{"type":"identifier","name":"TAB1.X"},{"type":"identifier","name":"TAB2.Y"}]}}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":89793374}

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testSimpleNestedSubQuery() throws Exception {
        String command = "select account_id_id, id, dt2, tag from (select acc_id as account_id_id, tmp.id, dt as dt2, tmp.tag from (select account_id as acc_id, hist.id, tag, dt, hist.submit_time from rstore.query_hists hist where hist.dt>0) tmp)";

        String result = "{\"type\":\"select\",\"from\":{\"type\":\"select\",\"from\":{\"type\":\"select\",\"from\":{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS\"},\"columns\":{\"type\":\"list\",\"operandlist\":[{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ACCOUNT_ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.DT\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.SUBMIT_TIME\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.TAG\"}]},\"vid\":73971918},\"columns\":{\"type\":\"list\",\"operandlist\":[{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ACCOUNT_ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.DT\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.TAG\"}]},\"vid\":82411964},\"columns\":{\"type\":\"list\",\"operandlist\":[{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ACCOUNT_ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.DT\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.TAG\"}]},\"vid\":64610793}\n";
        //SqlCommandTestHelper.parseHive(command);
        SqlNode ast = SqlCommandTestHelper.parsePrestoQuery(command);
        assertEquals(ast.toString(), result);
    }

}
