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
        SqlCommandTestHelper.parsePrestoQuery(command);
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
        String command = "SELECT account_id, cluster_id, cluster_inst_id, duration, available_cpu_secs, round(100*total_spark_time/available_cpu_secs,2) as percent_spark_utilization, round(100*total_zeppelin_time/duration,2) as percent_zeppelin_utilization from (select t5.*, t6.total_zeppelin_time FROM (SELECT t3.account_id, t3.cluster_id, t3.cluster_inst_id, t3.duration, t4.available_cpu_secs, t4.available_memory_secs, sum(time_taken) as total_spark_time FROM\n" +
                "(SELECT t1.account_id, t1.cluster_id, t1.cluster_inst_id, t1.time_taken, t2.duration FROM (SELECT y.account_id, x.* \n" +
                "FROM \n" +
                "(SELECT h.id AS id, h.status, h.account_id, 'coalesce(sql, program, cmdline)' as ssql FROM rstore.query_hists h JOIN rstore.spark_commands s ON h.command_id=s.id and h.dt=s.dt AND h.dt>='2019-01-20' AND status='done') y\n" +
                "RIGHT JOIN\n" +
                "(SELECT DISTINCT query_hist_id, status, sql_app_id, metric_app_id, cluster_id, cluster_inst_id, time_taken \n" +
                "   FROM (SELECT account_id, cluster_id, cluster_inst_id, json_extract(event_data,'$.executedQueryInfo.applicationId') as sql_app_id,  \n" +
                "         json_extract(event_data,'$.executedQueryInfo.queryhistId') as query_hist_id, json_extract(event_data,'$.executedQueryInfo.status') AS status, CAST(json_extract(event_data,'$.executedQueryInfo.timeTakenMs') AS BIGINT)/1000 as time_taken FROM processed_v2.spark WHERE event_date>='2019-01-20' AND event_type='CLUSTER.SPARK.METRICS.SQL')s\n" +
                "    LEFT JOIN\n" +
                "    (SELECT json_extract(event_data,'$.id') AS metric_app_id FROM processed_v2.spark WHERE event_date>='2019-01-20' AND \n" +
                "     event_type='CLUSTER.SPARK.METRICS') t\n" +
                "    ON s.sql_app_id = t.metric_app_id) x\n" +
                "ON CAST(x.query_hist_id AS VARCHAR) = CAST(y.id AS VARCHAR)) t1\n" +
                "JOIN\n" +
                "(SELECT cm.*, cl.account_id FROM (SELECT ci.id as cluster_inst_id, ci.cluster_id, to_unixtime(date_parse(down_at,  '%Y-%m-%d %H:%i:%s.%f')) - to_unixtime(date_parse(start_at,  '%Y-%m-%d %H:%i:%s.%f')) as duration FROM  rstore.cluster_configs cc JOIN rstore.cluster_insts ci \n" +
                "ON ci.cluster_config_id = cc.id \n" +
                "WHERE ci.start_at>='2019-01-01' and cc.use_spark=true) cm\n" +
                "JOIN rstore.clusters cl\n" +
                "ON cm.cluster_id = cl.id) t2\n" +
                "ON t1.account_id = t2.account_id\n" +
                "AND t1.cluster_inst_id = t2.cluster_inst_id\n" +
                "AND t1.cluster_id = t2.cluster_id) t3\n" +
                "JOIN\n" +
                "(select cluster_inst_id, sum(cpu_secs) as available_cpu_secs, sum(memory_secs) as available_memory_secs  from user_attribution.resource_availability_table where dt>='2019-01-20'  group by cluster_inst_id) t4\n" +
                "ON t3.cluster_inst_id = t4.cluster_inst_id\n" +
                "group by t3.account_id, t3.cluster_id, t3.cluster_inst_id, t3.duration, t4.available_cpu_secs, t4.available_memory_secs) t5\n" +
                "JOIN\n" +
                "(SELECT account_id, cluster_id, cluster_inst_id, sum(duration) as total_zeppelin_time FROM (select account_id, cluster_id, cluster_inst_id, max(event_time) - min(event_time) as duration from (select account_id, cluster_id, cluster_inst_id, CAST(coalesce(json_extract(event_data,'$.note.id'), json_extract(event_data,'$.infos.qbol_note_id')) AS INT) as id, coalesce(json_extract(event_data,'$.note.zeppelin_id'), json_extract(event_data,'$.infos.noteId')) as zeppelin_id, json_extract(event_data,'$.para.para_id') as para_id, CAST(json_extract(event_data,'$.event_type') AS VARCHAR) as event_type, CAST(json_extract(event_data,'$.event_time_ms') AS BIGINT)/1000 as event_time from processed_v2.zeppelin where event_date>='2019-01-20') s\n" +
                "where event_type = 'PARAGRAPH_EXECUTION_START'    or event_type = 'PARAGRAPH_EXECUTION_END'           \n" +
                "group by account_id, cluster_id, cluster_inst_id, id) t\n" +
                "group by account_id, cluster_id, cluster_inst_id) t6\n" +
                "ON t5.account_id = t6.account_id\n" +
                "AND t5.cluster_inst_id = t6.cluster_inst_id\n" +
                "AND t5.cluster_id = t6.cluster_id) \n" +
                "order by percent_zeppelin_utilization desc";
        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
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
