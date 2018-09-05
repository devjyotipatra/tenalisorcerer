package com.qubole.tenali.parse;


import com.qubole.tenali.util.SqlCommandTestHelper;
import org.junit.Test;


public class TenaliCommandLexerTest {

    @Test
    public void testSimpleSelectQuery() throws Exception {
        String command = "select to_date(qh.created_at) as dt,  count(qh.id) as num_queries " +
                "from query_hists qh join user_info ui on qh.qbol_user_id = ui.qu_id " +
                "join canonical_accounts externals on externals.id = ui.a_id " +
                "where to_date(qh.created_at) >= date_sub(from_unixtime(unix_timestamp()),30) " +
                "and command_type = 'HiveCommand' and qlog like '%\\\"EXECUTION_ENGINE\\\":\\\"tez\\\"%' " +
                "and   customer_name like \"${customer=%}\" " +
                "group by  to_date(qh.created_at) " +
                "order by dt, mt asc";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);

        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testHavingQuery() throws Exception {
        String command = "SELECT   JobTitle,\n" +
                "         MaritalStatus,\n" +
                "         AVG(VacationHours)\n" +
                "FROM     HumanResources.Employee AS E\n" +
                "GROUP BY JobTitle, MaritalStatus\n" +
                "HAVING   x in (select x from tab1)";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleSqlQuery0() throws Exception {
        String command = "SELECT a, b from tab where a>0 and b>0";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleSqlQuery1() throws Exception {
        String command = "SELECT a, b, c, count(*) as cnt from tab where a>0 group by a, b, c order by a, b";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleSqlQuery2() throws Exception {
        String command = "; \n\r\n\r\n ;SELECT distinct tab.X1 AS a, X2 b from tab where a1>0 and a2!=0 and a3=9";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testNoCommentSingleQuery() throws Exception {
        String command = "; ; SELECT logdate, site_key, survey_id, placement_key, question_id" +
                " FROM ( select count(*) from marketing.stats_log_survey " +
            "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
            "AND logdate>'2015-09-01') GROUP BY logdate, site_key, survey_id;";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testSingleCommentSingleQuery() throws Exception {
        String command = "-- some random * comment here\n" +
            "\n\n\n  use tenaliv2; " +
            "add jar s3://something_here/some_more_thing.jar; " +
            " SELECT logdate, site_key, survey_id, placement_key, question_id, answer_id, " +
            "tm_client_id, auction_id, exposed, correct FROM marketing.stats_log_survey " +
            "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
            "AND logdate>'2015-09-01' GROUP BY logdate, site_key, survey_id, placement_key, " +
            "question_id, answer_id, tm_client_id, auction_id, exposed, correct;";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 3", cctx.getListQueryContext().size()==3);
    }

    @Test
    public void testBlockCommentSingleQuery() throws Exception {
        String command = "/* some more random comments \n\n here */ \n\n\r\n use tenaliv2; add jar s3://something_here/some_more_thing.jar; -- some random * comment here \n\r\n SELECT logdate, site_key, survey_id, placement_key, question_id, answer_id, " +
            "tm_client_id, auction_id, exposed, correct FROM marketing.stats_log_survey " +
            "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
            "AND logdate>'2015-09-01' GROUP BY logdate, site_key, survey_id, placement_key, " +
            "question_id, answer_id, tm_client_id, auction_id, exposed, correct;";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==3);
    }

    @Test
    public void testBlockCommentMultipleQueries() throws Exception {
        String command = "/* some random comments \n\n here */" +
            "-- some random * comment here \n\r\n SELECT logdate, site_key, survey_id, placement_key, question_id, answer_id, " +
            "tm_client_id, auction_id, exposed, correct FROM marketing.stats_log_survey " +
            "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
            "AND logdate>'2015-09-01' GROUP BY logdate, site_key, survey_id, placement_key, " +
            "question_id, answer_id, tm_client_id, auction_id, exposed, correct;\n\r\n\n\r\n" +
            "/* some more random comments \n\n here \n\n*/" +
            "select to_date(qh.created_at) as dt,  count(qh.id) as num_queries " +
            "from query_hists qh join user_info ui on qh.qbol_user_id = ui.qu_id " +
            "join canonical_accounts externals on externals.id = ui.a_id " +
            "where to_date(qh.created_at) >= date_sub(from_unixtime(unix_timestamp()),30) " +
            "and command_type = 'HiveCommand' and qlog like '%\\\"EXECUTION_ENGINE\\\":\\\"tez\\\"%' " +
            "and   customer_name like \"${customer=%}\" " +
            "group by  to_date(qh.created_at) " +
            "order by dt asc";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 3", cctx.getListQueryContext().size()==3);
    }

    @Test
    public void testMultiSelectSingleQuery() throws Exception {
        String command = "SELECT * FROM db_users u JOIN (SELECT * FROM (SELECT get_json(json, 'location') as " +
            "location, to_date(created_at) as dt, count(*) num_users FROM db_users WHERE to_date(created_at) >= '2016-08-13'  " +
            "AND to_date(created_at) <= '2016-08-13'  AND LENGTH(get_json(json, 'location')) > 3  GROUP BY get_json(json, 'location'), " +
            "to_date(created_at)) by_date JOIN (SELECT get_json(json, 'location') as location, count(*) total_users FROM db_users  " +
            "WHERE LENGTH(get_json(json, 'location')) > 3  GROUP BY get_json(json, 'location')  HAVING count(*) > 5000) " +
            "common_locations ON common_locations.location = by_date.location) loc_frac_by_date ON " +
            "get_json(u.json, 'location') = loc_frac_by_date.location AND to_date(u.created_at) = loc_frac_by_date.dt " +
            "WHERE loc_frac_by_date.frac_of_location > 7 DISTRIBUTE BY dt;\n\r\t;" +
            "SELECT * FROM db_users u JOIN (SELECT * FROM table1) v on u.a=v.b;SELECT a,b, count(*) from ab where s='jrenv' GROUP BY a, b";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
    }

    @Test
    public void testValidPrestoLateral() throws Exception {
        String command = "SELECT *\n\n FROM DEPT,\n\n LATERAL TABLE(RAMP(DEPT.DEPTNO)) adid";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testLateralSubquery() throws Exception {
        String command = "SELECT * FROM foo, LATERAL (SELECT * FROM bar WHERE bar.id = foo.bar_id) ss;";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleLateralViewExplode() throws Exception {
       // String command = "SELECT pageid, adid FROM pageAds LATERAL table explode(adid_list) adTable AS adid\n\r\n\r\n ; ";

        String command = "SELECT  ci.id AS cluster_instance_id, ci.cluster_id, unix_timestamp(ci.start_at) AS start_at, " +
                " unix_timestamp(ci.down_at) AS down_at, _timestamp AS metric_time, _type AS metric_type,\n" +
                "CASE _type\n" +
                "   WHEN \"MEMORY_STATS\" THEN arr(total_available_memory, total_used_memory)\n" +
                "   WHEN \"VCORES_STATS\" THEN arr(total_available_vcores, total_used_vcores)\n" +
                "END AS total \n" +
                "FROM (SELECT cluster_id, m.total_available_memory, m.total_used_memory, v.total_available_vcores, v.total_used_vcores, e._type, " +
                "_int(e._timestamp/1000) AS _timestamp FROM processed.yarn \n" +
                ",LATERAL Table( json_tuple(event_data, 'eventData', 'type', 'timestamp')) AS _eventData, _type, _timestamp \n" +
                ",LATERAL Table( json_tuple(e._eventData, 'total_memory_mb', 'used_memory_mb'))  total_available_memory, total_used_memory\n" +
                ",LATERAL Table( json_tuple(e._eventData, 'total_vcores', 'used_vcores'))  total_available_vcores, total_used_vcores\n" +
                "WHERE event_date>=\"$start_date\" AND  event_date<\"$end_date\" AND _type in (\"MEMORY_STATS\", \"VCORES_STATS\")) s \n" +
                "JOIN rstore.cluster_insts ci ON s.cluster_id=ci.cluster_id\n" +
                "WHERE _timestamp BETWEEN unix_timestamp(ci.start_at) AND unix_timestamp(ci.down_at)";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSqlDS() throws Exception {
        String command = "WITH a AS (\n" +
                "  SELECT substr(name, 1, 3) x FROM tab1 )\n" +
                "SELECT *\n" +
                "FROM a\n" +
                "WHERE x = 'foo'";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testCtasQuery() throws Exception {
        String command = "CREATE TABLE tabx AS " +
                "SELECT a, count(s.b) as cnt from (select c as a, d as b from tab where c>0 and d<0)s";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleNestedSql1() throws Exception {
        String command = "SELECT a, count(s.b) as cnt from (select c as a, d as b from tab where c>0 and d<0)s group by a order by cnt";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleNestedSql2() throws Exception {
        String command = "SELECT s.a, b, e, count(s.b) as cnt from (select c as a, d as b from tab1 where c>0 and d<0)s join \n" +
                "(select a, f from tab2)t on s.a=t.e \n" +
                "group by s.a, b, e order by cnt";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSqlIf() throws Exception {
        String command = "SELECT IF(CARDINALITY(my_array) >= 3, my_array[3], NULL)\n FROM tab1";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testLateralViewSimple() throws Exception {
        String command = "SELECT student, score\n" +
                "FROM tests CROSS JOIN UNNEST(scores) AS t (score);";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testWindowFunctionSimple() throws Exception {
        String command = "select id, ts\n" +
                "      ,case when ts-lag(ts,1,ts) over(partition by id order by ts) > 3000 then 1 else 0 end as col\n" +
                "      from tbl\n";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testWindowFunction() throws Exception {
        String command = "select id,ts,1+sum(col) over(partition by id order by ts) as rnk\n" +
                "from (select id,ts\n" +
                "      ,case when ts-lag(ts,1,ts) over(partition by id order by ts) > 3000 then 1 else 0 end as col\n" +
                "      from tbl\n" +
                "     ) t";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testWindowFunctionComplex() throws Exception {
        String command = "select venuestate, venueseats, venuename,\n" +
                "first_value(venuename)\n" +
                "over(partition by venuestate\n" +
                "order by venueseats desc\n" +
                "rows between unbounded preceding and unbounded following)\n" +
                "from (select * from venue where venueseats >0)\n" +
                "group by venuestate order by venuestate";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseAnsiSql(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }
}