package com.qubole.tenali.parse;


import com.qubole.tenali.util.LexerTestHelper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;



public class TenaliCommandLexerTest {

    @Test
    public void testNoQuery() throws Exception {
        String command = ";\n\r\n\r\n ; ";

        LexerTestHelper.parse(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testNoCommentSingleQuery() throws Exception {
        String command = "; ; SELECT logdate, site_key, survey_id, placement_key, question_id, answer_id, " +
            "tm_client_id, auction_id, exposed, correct FROM marketing.stats_log_survey " +
            "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
            "AND logdate>'2015-09-01' GROUP BY logdate, site_key, survey_id, placement_key, " +
            "question_id, answer_id, tm_client_id, auction_id, exposed, correct;";

        LexerTestHelper.parse(command);
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

        LexerTestHelper.parse(command);
        //assertThat("correct number of queries is 3", cctx.getListQueryContext().size()==3);
    }

    @Test
    public void testBlockCommentSingleQuery() throws Exception {
        String command = "/* some more random comments \n\n here */ \n\n\r\n use tenaliv2; add jar s3://something_here/some_more_thing.jar; -- some random * comment here \n\r\n SELECT logdate, site_key, survey_id, placement_key, question_id, answer_id, " +
            "tm_client_id, auction_id, exposed, correct FROM marketing.stats_log_survey " +
            "WHERE survey_id='ZmOwqVvLFKtWroPKX5VN' AND event IN ('svyresp','svystart','svycomp') " +
            "AND logdate>'2015-09-01' GROUP BY logdate, site_key, survey_id, placement_key, " +
            "question_id, answer_id, tm_client_id, auction_id, exposed, correct;";

        LexerTestHelper.parse(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==3);
    }

    @Test
    public void testBlockCommentMultipleQueries() throws Exception {
        String command = "/* some random comments \n\n here */" +
            "add jar s3://something_here/some_more_thing.jar; " +
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

        LexerTestHelper.parse(command);
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

        LexerTestHelper.parse(command);
    }
}