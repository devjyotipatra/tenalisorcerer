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

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);

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
        //SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testDDLQuery() throws Exception {
        String command = "set hive.cli.print.header=false;\u0006set hive.resultset.use.unique.column.names=false; \u0006SET hive.execution.engine=tez;\u0006SET mapreduce.map.memory.mb=4096;\u0006SET mapreduce.map.java.opts=-Xmx3276m;\u0006SET mapreduce.reduce.memory.mb=8192;\u0006SET mapreduce.reduce.java.opts=-Xmx6553m;\u0006SET hive.tez.container.size=8192;\u0006SET hive.tez.java.opts=-Xmx6553m;\u0006SET tez.runtime.io.sort.mb=3276;" +
                "\u0006DROP TABLE IF EXISTS tlc_nci_intraday_history.lc_bankruptcy_TEMPTBL`;\u0006\u0006DROP VIEW IF EXISTS tlc_nci_intraday_history.`lc_bankruptcy_TEMP`;" +
                "\u0006\u0006CREATE EXTERNAL TABLE IF NOT EXISTS tlc_nci_intraday_history.`lc_bankruptcy_TEMPTBL` (\u0006 `ID` BIGINT, -- NUMBER\u0006 `CREATE_D` TIMESTAMP, -- DATE\u0006 `STATUS` TINYINT, -- NUMBER\u0006 `SOURCE_PENDING_BK` TINYINT, -- NUMBER\u0006 `PENDING_BK_D` TIMESTAMP, -- DATE\u0006 `NOTIFICATION_RECEIVED_D` TIMESTAMP, " +
                "-- DATE\u0006 `CASE_NUMBER` STRING, -- VARCHAR2\u0006 `USBC_DISTRICT` STRING, -- VARCHAR2\u0006 `USBC_STATE` STRING, -- VARCHAR2\u0006 `BANKRUPTCY_CHAPTER` TINYINT, -- NUMBER\u0006 `FILING_D` TIMESTAMP, -- DATE\u0006 `ATTORNEY_INFORMATION` STRING, -- VARCHAR2\u0006 `TRUSTEE_NAME` STRING, -- VARCHAR2\u0006 `MEETING_CREDITORS_D` TIMESTAMP, -- DATE\u0006 `PROOF_OF_CLAIM_DUE_D` TIMESTAMP, -- DATE\u0006 `PROOF_OF_CLAIM_FILED_D` TIMESTAMP, -- DATE\u0006 `PREF_TRANSFER_PAY_AMOUNT` DECIMAL(22,12), -- NUMBER\u0006 `PLAN_DISTR` STRING, -- VARCHAR2\u0006 `DISCHARGE_D` TIMESTAMP, -- DATE\u0006 `DISMISSAL_D` TIMESTAMP, -- DATE\u0006 `REOPENED_D` TIMESTAMP, -- DATE\u0006 `PREVIOUS_BK_D` TIMESTAMP, -- DATE\u0006 `JOINT_FILING_NAME` STRING, -- VARCHAR2\u0006 `JOINT_FILING_SSN` STRING, -- VARCHAR2\u0006 `COMMENTS` STRING, -- VARCHAR2\u0006 `MEETING_CREDITORS_TIME` TIMESTAMP, -- DATE\u0006 `PROOF_OF_CLAIM_ENABLE` TINYINT, -- NUMBER\u0006 `JOINT_FILING_ENABLE` TINYINT, -- NUMBER\u0006 `PLAN_DISTR_AMT` DECIMAL(22,12), -- NUMBER\u0006 `PLAN_DISTR_PERC` DECIMAL(10,2), -- NUMBER\u0006 `JOINT_FILING_SSN_ENC` STRING, -- VARCHAR2\u0006 `STATUS_D` TIMESTAMP, -- DATE\u0006 `AID` BIGINT, -- NUMBER\u0006 `ATTORNEY_PHONE_ENC` STRING, -- VARCHAR2\u0006 `TRUSTEE_PHONE_ENC` STRING, -- VARCHAR2\u0006 `MODIFIED_D` TIMESTAMP, -- TIMESTAMP(6)\u0006 `ATTORNEY_PHONE` STRING, -- VARCHAR2\u0006 `TRUSTEE_PHONE` STRING -- VARCHAR2\u0006)\u0006ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\001'\u0006STORED AS INPUTFORMAT 'com.hadoop.mapred.DeprecatedLzoTextInputFormat'\u0006OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'\u0006LOCATION 'hdfs://alpha-ingest.lcdp.lendingcloud.us/data/platform-db/temp_nci/tlc_nci_intraday.lc_bankruptcy';\u0006\u0006CREATE VIEW IF NOT EXISTS tlc_nci_intraday_history.`lc_bankruptcy_TEMP` AS SELECT\u0006 `ID`,\u0006 `CREATE_D`,\u0006 `STATUS`,\u0006 `SOURCE_PENDING_BK`,\u0006 `PENDING_BK_D`,\u0006 `NOTIFICATION_RECEIVED_D`,\u0006 `CASE_NUMBER`,\u0006 `USBC_DISTRICT`,\u0006 `USBC_STATE`,\u0006 `BANKRUPTCY_CHAPTER`,\u0006 `FILING_D`,\u0006 `ATTORNEY_INFORMATION`,\u0006 `TRUSTEE_NAME`,\u0006 `MEETING_CREDITORS_D`,\u0006 `PROOF_OF_CLAIM_DUE_D`,\u0006 `PROOF_OF_CLAIM_FILED_D`,\u0006 `PREF_TRANSFER_PAY_AMOUNT`,\u0006 `PLAN_DISTR`,\u0006 `DISCHARGE_D`,\u0006 `DISMISSAL_D`,\u0006 `REOPENED_D`,\u0006 `PREVIOUS_BK_D`,\u0006 `JOINT_FILING_NAME`,\u0006 `JOINT_FILING_SSN`,\u0006 `COMMENTS`,\u0006 `MEETING_CREDITORS_TIME`,\u0006 `PROOF_OF_CLAIM_ENABLE`,\u0006 `JOINT_FILING_ENABLE`,\u0006 `PLAN_DISTR_AMT`,\u0006 `PLAN_DISTR_PERC`,\u0006 udf.lcencrypt(udf.lcdecrypt(`JOINT_FILING_SSN_ENC`,'db_npi_key'),'edh_fallback_fek',true) as `JOINT_FILING_SSN_ENC`,\u0006 `STATUS_D`,\u0006 `AID`,\u0006 udf.lcencrypt(udf.lcdecrypt(`ATTORNEY_PHONE_ENC`,'low_npi-pii_fek'),'edh_fallback_fek',true) as `ATTORNEY_PHONE_ENC`,\u0006 udf.lcencrypt(udf.lcdecrypt(`TRUSTEE_PHONE_ENC`,'low_npi-pii_fek'),'edh_fallback_fek',true) as `TRUSTEE_PHONE_ENC`,\u0006 `MODIFIED_D`,\u0006 `ATTORNEY_PHONE`,\u0006 `TRUSTEE_PHONE`\u0006FROM tlc_nci_intraday_history.`lc_bankruptcy_TEMPTBL` ";

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseHive(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleSqlQuery1() throws Exception {
        String command = "SELECT a, b, c, count(*) as cnt from tab where a>0 group by a, b, c order by a, b";

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleSqlQuery2() throws Exception {
        String command = "; \n\r\n\r\n ;SELECT distinct tab.X1 AS a, X2 b from tab where a1>0 and a2!=0 and a3=9";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testNoCommentSingleQuery() throws Exception {
        String command = "; ; SELECT logdate, site_key, survey_id, placement_key, question_id" +
                " FROM ( select count(*) from marketing.stats_log_survey " +
            "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
            "AND logdate>'2015-09-01') s  GROUP BY logdate, site_key, survey_id;";

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testSingleCommentSingleQuery() throws Exception {
        String command = "--some random * comment here" +
            "\n\n\n  use tenaliv2; " +
            "add jar s3://something_here/some_more_thing.jar; " +
            " SELECT logdate, site_key, survey_id, placement_key, question_id, answer_id, " +
            "tm_client_id, auction_id, exposed, correct FROM marketing.stats_log_survey " +
            "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
            "AND logdate>'2015-09-01' GROUP BY logdate, site_key, survey_id, placement_key, " +
            "question_id, answer_id, tm_client_id, auction_id, exposed, correct;";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
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
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==3);
    }

    @Test
    public void testBlockCommentMultipleQueries() throws Exception {
        String command = "/* some random comments \n\n here */" +
            "-- some random * comment here \n\r\n SELECT logdate, site_key, survey_id, placement_key, question_id, answer_id, " +
            "tm_client_id, auction_id, exposed, correct FROM marketing. stats_log_survey " +
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
        //SqlCommandTestHelper.parsePrestoQuery(command);
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
        SqlCommandTestHelper.parsePrestoQuery(command);
    }

    @Test
    public void testValidPrestoLateral() throws Exception {
        String command = "SELECT *\n\n FROM DEPT,\n\n LATERAL TABLE(RAMP(DEPT.DEPTNO)) adid";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testLateralSubquery() throws Exception {
        String command = "SELECT * FROM foo, LATERAL (SELECT * FROM bar WHERE bar.id = foo.bar_id) ss;";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
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
        SqlCommandTestHelper.parsePrestoQuery(command);
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
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testCtasQuery() throws Exception {
        String command = "CREATE TABLE tabx AS " +
                "SELECT a, count(s.b) as cnt from (select c as a, d as b from tab where c>0 and d<0)s";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleNestedSql1() throws Exception {
        String command = "SELECT a, count(s.b) as cnt from (select c as a, d as b from tab where c>0 and d<0)s group by a order by cnt";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleNestedSql2() throws Exception {
        String command = "SELECT s.a, b, e, count(s.b) as cnt from (select c as a, d as b from tab1 where c>0 and d<0)s join \n" +
                "(select a, f from tab2)t on s.a=t.e \n" +
                "group by s.a, b, e order by cnt";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSqlIf() throws Exception {
        String command = "SELECT IF(CARDINALITY(my_array) >= 3, my_array[3], NULL)\n FROM tab1";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testLateralViewSimple() throws Exception {
        String command = "SELECT student, score\n" +
                "FROM tests CROSS JOIN UNNEST(scores) AS t (score);";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testWindowFunctionSimple() throws Exception {
        String command = "select id, ts\n" +
                "      ,case when ts-lag(ts,1,ts) over(partition by id order by ts) > 3000 then 1 else 0 end as col\n" +
                "      from tbl\n";

        SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
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
        SqlCommandTestHelper.parsePrestoQuery(command);
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
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }
}