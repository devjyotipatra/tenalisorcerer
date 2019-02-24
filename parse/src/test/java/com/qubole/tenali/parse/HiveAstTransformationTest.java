package com.qubole.tenali.parse;

import com.qubole.tenali.parse.sql.datamodel.TenaliAstNode;
import com.qubole.tenali.util.SqlCommandTestHelper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class HiveAstTransformationTest {

    @Test

    public void testSimpleLateralView() throws Exception {
        String command = "select name, myq from table1 lateral view explode(qual, gual) q as myq1, myq2";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSlightlyComplexLateralView() throws Exception {
        String command = //"insert overwrite table tenaliV2.usagemap partition (submit_time, source)\n" +
                "                        select  sub.query, sub.q_id, usagetable.tbl, usagetable.col, usagetable.usg, sub.account_id,\n" +
                "                        sub.submit_time, sub.source as source from (select n.query as query, n.query_hists_id as q_id,\n" +
                "                        n.q_ast as q_ast, n.account_id as account_id, n.submit_time as submit_time, n.source as source from tenaliV2.galaxy\n" +
                "                        as n where n.submit_time in ( '2019-02-20' ) and n.account_id IN (7845,4020) ) as sub lateral view\n" +
                "                        usageUDTFfromHiveTables(sub.query,sub.q_ast, sub.account_id) usagetable as tbl, col, usg;";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testComplexJoinQuery2() throws Exception {
        String command = "select s2.acc_id, tag1, s1.id from (select tab1.account_id acc_id, tab1.tag tag1, tab2.id from rstore.query_hists tab1 join rstore.cluster_nodes  tab2 on tab1.account_id=tab2.account_id where tab2.account_id>0) s1 " +
                "join (select id, account_id as acc_id, created_at, deleted_at from rstore.clusters where id>100) s2 on s1.acc_id=s2.acc_id";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testComplexJoinQuery3() throws Exception {
        String command = "SELECT account_id, count(*) as cnt FROM \n" +
                "(SELECT h.id AS id, h.dt, h.account_id, h.status, coalesce(program, cmdline) as _sql FROM rstore.query_hists h JOIN rstore.spark_commands s ON h.command_id=s.id and h.dt=s.dt AND h.dt>='2019-02-01' AND status='done') y\n" +
                "LEFT JOIN\n" +
                "(SELECT DISTINCT query_hist_id, sql_app_id, command_id\n" +
                "   FROM (SELECT account_id, command_id, cluster_id, cluster_inst_id, json_extract(event_data,'$.executedQueryInfo.applicationId') as sql_app_id,  \n" +
                "\t\t json_extract(event_data,'$.executedQueryInfo.queryhistId') as query_hist_id, json_extract(event_data,'$.executedQueryInfo.status') AS status, json_extract(event_data,'$.executedQueryInfo.timeTakenMs') as time_taken FROM processed_v2.spark WHERE event_date>='2019-02-01' AND event_type='CLUSTER.SPARK.METRICS.SQL')s\n" +
                "    LEFT JOIN\n" +
                "    (SELECT json_extract(event_data,'$.id') AS metric_app_id FROM processed_v2.spark WHERE event_date>='2019-02-01' AND \n" +
                "     event_type='CLUSTER.SPARK.METRICS') t\n" +
                "    ON s.sql_app_id = t.metric_app_id) x\n" +
                "ON CAST(x.query_hist_id AS VARCHAR) = CAST(y.id AS VARCHAR)\n" +
                "WHERE query_hist_id is not NULL and _sql is not null and length(_sql)>50\n" +
                "group by account_id\n" +
                "order by 1 desc\n";
        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testMultipleJoinQuery() throws Exception {
        String command = "SELECT id, get_json(u.json, 'location') as info, to_date(created_at) as dt FROM db_users u JOIN (SELECT by_date.location, dt, num_users * 1000.0 / total_users as frac_of_location, num_users, total_users FROM (SELECT get_json(json, 'location') as location, to_date(created_at) as dt, count(*) num_users FROM db_users WHERE to_date(created_at) >= '2016-08-13'  AND to_date(created_at) <= '2016-08-13'  AND LENGTH(get_json(json, 'location')) > 3  GROUP BY get_json(json, 'location'), to_date(created_at)) by_date JOIN (SELECT get_json(json, 'location') as location, count(*) total_users FROM db_users  WHERE LENGTH(get_json(json, 'location')) > 3  GROUP BY get_json(json, 'location')  HAVING count(*) > 5000) common_locations ON common_locations.location = by_date.location) loc_frac_by_date ON get_json(u.json, 'location') = loc_frac_by_date.location AND to_date(u.created_at) = loc_frac_by_date.dt WHERE loc_frac_by_date.frac_of_location > 7 DISTRIBUTE BY dt";



        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testPositionalOrdinates() throws Exception {
        String command = "select s2.acc_id, tag1, count(DISTINCT acc_id) as cnt from (select tab1.account_id acc_id, tab1.tag tag1, tab2.id from rstore.query_hists tab1 join rstore.cluster_nodes  tab2 on tab1.account_id=tab2.account_id where tab2.account_id>0) s1 " +
                "join (select id, account_id as acc_id, created_at, deleted_at from rstore.clusters where id>100) s2 on s1.acc_id=s2.acc_id group by 1,2 order by 3";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


        @Test
        public void testSelect() throws Exception {
            String query = "SELECT logdate, site_key, survey_id, placement_key, question_id, answer_id, " +
                    "tm_client_id, auction_id, exposed, correct FROM marketing.stats_log_survey " +
                    "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
                    "AND logdate>'2015-09-01' GROUP BY logdate, site_key, survey_id, placement_key, " +
                    "question_id, answer_id, tm_client_id, auction_id, exposed, correct";

            //TenaliAstNode node = evaluateJsonAndGetNode(resultDirPath.concat("/AST1.json"), query);
            //assertThat("node is not a select node", node instanceof SelectNode);
            SqlCommandTestHelper.transformHiveAst(query);
        }


        @Test
        public void testComplex() throws Exception {
            String query = "select to_date(qh.created_at) as dt,  count(qh.id) as num_queries " +
                    "from query_hists qh join user_info ui on qh.qbol_user_id = ui.qu_id " +
                    "join canonical_accounts externals on externals.id = ui.a_id " +
                    "where to_date(qh.created_at) >= '2019-01-01' " +
                    "and command_type = 'HiveCommand' and qlog like 'temp_temp' " +
                    "and   customer_name like '${customer=%}' " +
                    "group by  to_date(qh.created_at) " ;
            //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
            //assertThat("node is not a select node", node instanceof SelectNode);
            //assertThat("from should be table node",
            //        ((SelectNode) node).from.get(0) instanceof JoinNode);

            SqlCommandTestHelper.transformHiveAst(query);
        }


    @Test
    public void testCTE() throws Exception {
        String query = "-- Comment here \nWITH Sales_CTE \n" +
                "AS  \n" +
                "-- Define the CTE query  \n" +
                "(  \n" +
                "    SELECT SalesPersonID, SalesOrderID, YEAR(OrderDate) AS SalesYear  \n" +
                "    FROM Sales.SalesOrderHeader  \n" +
                "    WHERE SalesPersonID IS NOT NULL  \n" +
                ")  \n" +
                "-- Define the outer query referencing the CTE name.  \n" +
                "SELECT SalesPersonID, COUNT(SalesOrderID) AS TotalSales, SalesYear  \n" +
                "FROM Sales_CTE  \n" +
                "GROUP BY SalesYear, SalesPersonID  \n" +
                "ORDER BY SalesPersonID, SalesYear;  " ;
        //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
        //assertThat("node is not a select node", node instanceof SelectNode);
        //assertThat("from should be table node",
        //        ((SelectNode) node).from.get(0) instanceof JoinNode);

        SqlCommandTestHelper.transformHiveAst(query);
    }

    }
