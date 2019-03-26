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
        String command = "insert overwrite table tenaliV2.usagemap partition (submit_time, source)\n" +
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
        String command = "select s2.acc_id, tag1, s1.id from " +
                "(select tab1.account_id acc_id, tab1.tag tag1, tab2.id from rstore.query_hists tab1 join rstore.cluster_nodes  tab2 on tab1.account_id=tab2.account_id where tab2.account_id>0) s1 " +
                "join " +
                "(select id, account_id as acc_id, created_at, deleted_at from rstore.clusters where id>100) s2 on s1.acc_id=s2.acc_id";

        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testComplexJoinQuery3() throws Exception {
        String command = "SELECT account_id, count(*) as cnt FROM \n" +
                "(SELECT h.id AS id, h.dt, h.account_id, h.status, coalesce(program, cmdline) as qsql FROM rstore.query_hists h JOIN rstore.spark_commands s ON h.command_id=s.id and h.dt=s.dt AND h.dt>='2019-02-01' AND status='done') y\n" +
                "LEFT JOIN\n" +
                "(SELECT DISTINCT query_hist_id, sql_app_id, command_id\n" +
                "   FROM (SELECT account_id, command_id, cluster_id, cluster_inst_id, json_extract(event_data,'$.executedQueryInfo.applicationId') as sql_app_id,  \n" +
                "\t\t json_extract(event_data,'$.executedQueryInfo.queryhistId') as query_hist_id, json_extract(event_data,'$.executedQueryInfo.status') AS status, json_extract(event_data,'$.executedQueryInfo.timeTakenMs') as time_taken FROM processed_v2.spark WHERE event_date>='2019-02-01' AND event_type='CLUSTER.SPARK.METRICS.SQL')s\n" +
                "    LEFT JOIN\n" +
                "    (SELECT json_extract(event_data,'$.id') AS metric_app_id FROM processed_v2.spark WHERE event_date>='2019-02-01' AND \n" +
                "     event_type='CLUSTER.SPARK.METRICS') t\n" +
                "    ON s.sql_app_id = t.metric_app_id) x\n" +
                "ON x.query_hist_id = y.id \n" +
                "WHERE query_hist_id is not NULL and qsql is not null and length(qsql)>50\n" +
                "group by account_id\n" +
                "order by 1 desc\n";
        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testMultipleJoinQuery() throws Exception {
        String command = "SELECT id, get_json(u.json, 'location') as info, to_date(created_at) as dt FROM db_users u JOIN " +
                "(SELECT by_date.location, dt, num_users * 1000.0 / total_users as frac_of_location, num_users, total_users FROM " +
                "(SELECT get_json(json, 'location') as location, to_date(created_at) as dt, count(*) num_users FROM db_users " +
                "WHERE to_date(created_at) >= '2016-08-13'  AND to_date(created_at) <= '2016-08-13'  AND LENGTH(get_json(json, 'location')) > 3  " +
                "GROUP BY get_json(json, 'location'), to_date(created_at)) by_date " +
                "JOIN " +
                "(SELECT get_json(json, 'location') as location, " +
                "count(*) total_users FROM db_users  WHERE LENGTH(get_json(json, 'location')) > 3  GROUP BY get_json(json, 'location')  " +
                "HAVING count(*) > 5000) common_locations ON common_locations.location = by_date.location) loc_frac_by_date " +
                "ON get_json(u.json, 'location') = loc_frac_by_date.location AND to_date(u.created_at) = loc_frac_by_date.dt " +
                "WHERE loc_frac_by_date.frac_of_location > 7 DISTRIBUTE BY dt limit 100";

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.transformHiveAst(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testPositionalOrdinates() throws Exception {
        String command = "select s2.acc_id, tag1, count(DISTINCT acc_id) as cnt from " +
                "(select tab1.account_id acc_id, tab1.tag tag1, tab2.id from rstore.query_hists tab1 join rstore.cluster_nodes  tab2 " +
                "on tab1.account_id=tab2.account_id where tab2.account_id>0) s1 " +
                "join " +
                "(select id, account_id as acc_id, created_at, deleted_at from rstore.clusters where id>100) s2 on s1.acc_id=s2.acc_id " +
                "group by 1,2 order by 3";

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


    @Test
    public void testCTAS() throws Exception {
        String query = "CREATE TABLE myflightinfo2007 AS\n" +
                "    SELECT Year, Month, DepTime, ArrTime, FlightNum, Origin, Dest FROM FlightInfo2007\n" +
                "    WHERE (Month = 7 AND DayofMonth = 3) AND (Origin='JFK' AND Dest='ORD');" ;
        //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
        //assertThat("node is not a select node", node instanceof SelectNode);
        //assertThat("from should be table node",
        //        ((SelectNode) node).from.get(0) instanceof JoinNode);

        SqlCommandTestHelper.transformHiveAst(query);
    }


    @Test
    public void testComplexJoin2() throws Exception {
        String query = "SELECT matched_events.dt, 'api' AS environment, matched_events.account_id, COALESCE(total_events, 0) AS total_events, total_queries,  matched_queries " +
                "FROM   (SELECT presto_queries.dt AS dt,  account_id, Count(*) AS total_queries,  Sum(CASE  WHEN presto_events.command_id IS NULL THEN 0  ELSE 1  END) AS matched_queries  " +
                "        FROM   (SELECT dt, id, account_id  FROM   rstore.query_hists WHERE  dt >= '2019-02-21' AND dt <= '2019-02-28' AND command_type = 'PrestoCommand') presto_queries  " +
                " LEFT JOIN (SELECT command_id AS command_id,  Count(*)  FROM   processed_v2.presto  WHERE  event_date >= '2019-02-21' AND event_date <= '2019-02-28' GROUP  BY event_date,  command_id,  account_id) presto_events  " +
                "   ON presto_queries.id = presto_events.command_id  " +
                "        GROUP  BY presto_queries.dt, presto_queries.account_id) matched_events  " +
                "  LEFT JOIN (SELECT event_date,  account_id,  Count(*) AS total_events  FROM   processed_v2.presto  WHERE  event_date >= '2019-02-21' and event_date <= '2019-02-28' " +
                "                  GROUP  BY account_id,  event_date) total_events  ON total_events.event_date = matched_events.dt  AND total_events.account_id = matched_events.account_id " ;
        //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
        //assertThat("node is not a select node", node instanceof SelectNode);
        //assertThat("from should be table node",
        //        ((SelectNode) node).from.get(0) instanceof JoinNode);

        SqlCommandTestHelper.transformHiveAst(query);
    }


    @Test
    public void testDropTable() throws Exception {
        String query = "use mydefault;\u0006drop table if exists tmp_klynch_ebay_conversions_1550485623;\u0006drop table if exists tmp_klynch_ebay_conversions_1550485742;\u0006drop table if exists tmp_klynch_ebay_conversions_1550485862;\u0006drop table if exists gap_gdx_unique_staging_21404_1550506236;\u0006drop table if exists gap_gdx_unique_staging_22881_1550507276;\u0006drop table if exists gap_gdx_unique_staging_24201_1550508248;\u0006drop table if exists gap_gdx_unique_staging_25288_1550509187" ;
        //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
        //assertThat("node is not a select node", node instanceof SelectNode);
        //assertThat("from should be table node",
        //        ((SelectNode) node).from.get(0) instanceof JoinNode);

        SqlCommandTestHelper.transformHiveAst(query);
    }

    @Test
    public void testSample1() throws Exception {
        String query = "\u0006alter table mm_viewability_events_test recover partitions;\u0006\u0006select \u0006    report_timestamp,\u0006    to_date(report_timestamp) as impression_date,\u0006    campaign_name, \u0006    a.campaign_id, \u0006    strat.goal_type as strategy_goal_type,\u0006 case when length(delphi_metadata)<1 then 'tree brain'\u0006   when delphi_metadata like '%_sb%' then 'shared brain'\u0006   when delphi_metadata like '%_tf%' then 'log brain' else delphi_metadata end as brain_type, \u0006    case when watermark=0 then 'Non-Watermark' else 'Watermark' end as wm_flag,\u0006 is_viewable as viewable_impressions, \u0006 is_measurable as measurable_impressions, \u0006    1 as impressions,\u0006 clicks as clicks, \u0006 total_spend_cpm/1000 as spend, \u0006-- sum(total_spend_cpm)/count(a.auction_id) as cpm, \u0006-- coalesce(sum(total_spend_cpm)/sum(is_viewable),0) as vcpm, \u0006-- coalesce(sum(in_view_100)/sum(is_measurable),0) as in_view_rate, \u0006-- coalesce(sum(clicks)/count(a.auction_id),0) as ctr, \u0006-- coalesce(sum(total_spend_cpm/1000)/ sum(clicks),0) as cpc, \u0006    video_start as video_start, \u0006    video_complete as video_complete, \u0006    pv_conversions, \u0006    pc_conversions, \u0006    pv_revenue, \u0006    pc_revenue, \u0006    device, \u0006    case when channel_type in (8,9) then 'in-app' else 'web' end as inventory_type\u0006-- coalesce(sum(video_complete)/sum(video_start),0) as vcr\u0006\u0006 from \u0006\u0006 (select \u0006            report_timestamp,\u0006   delphi_metadata,\u0006   auction_id,\u0006   campaign_id, \u0006   campaign_name, \u0006            strategy_id,\u0006   watermark, \u0006   total_spend_cpm, \u0006            device,\u0006            channel_type\u0006from \u0006 mm_impressions_ct imp\u0006 where organization_id = 100977\u0006 and impression_date between date_sub('2019-03-04',1) and date_add('2019-03-05',1)\u0006    and to_date(report_timestamp) between '2019-03-04' and '2019-03-05'\u0006 and campaign_id in (614838,617477)\u0006 ) a \u0006join t1_meta_strategy_rs strat on a.strategy_id = strat.id\u0006\u0006left join \u0006\u0006  (\u0006  select \u0006  imp_auction_id, \u0006     event_timestamp_gmt,\u0006  case when viewability_event_name = 'is_viewable' then 1 else 0 end as is_viewable, \u0006    case when viewability_event_name = 'is_measurable' then 1 else 0 end as is_measurable, \u0006    case when viewability_event_name = 'is_viewable_100%' then 1 else 0 end as in_view_100\u0006 \u0006 from \u0006  mm_viewability_events_test \u0006 where organization_id in (100977)\u0006 and event_date between date_sub('2019-03-04',1) and date_add('2019-03-05',1)\u0006    and to_date(event_report_timestamp) between '2019-03-04' and '2019-03-05'\u0006 and campaign_id in (614838,617477)\u0006 ) b\u0006\u0006on a.auction_id = b.imp_auction_id\u0006\u0006left join \u0006(\u0006  select \u0006  imp_auction_id, \u0006  sum(case when event_subtype = 'q4' then 1 else 0 end) as video_complete,\u0006  sum(case when event_subtype = 'vst' then 1 else 0 end) as video_start, \u0006  sum(case when event_type = 'click' then 1 else 0 end) as clicks, \u0006        sum(case when event_type = 'conversion' and pv_pc_flag = 'V' then 1 else 0 end) as pv_conversions, \u0006         sum(case when event_type = 'conversion' and pv_pc_flag = 'C' then 1 else 0 end) as pc_conversions, \u0006        sum(case when pv_pc_flag = 'V' then mm_v1 else 0 end) as pv_revenue, \u0006        sum(case when pv_pc_flag = 'C' then mm_v1 else 0 end) as pc_revenue\u0006 from \u0006  mm_attributed_events_ct\u0006 where organization_id in (100977)\u0006 and event_date between date_sub('2019-03-04',1) and date_add('2019-03-05',1)\u0006    and to_date(event_report_timestamp) between '2019-03-04' and '2019-03-05'\u0006 and campaign_id in (614838,617477)\u0006    \u0006    group by imp_auction_id\u0006 ) c \u0006\u0006 on a.auction_id = c.imp_auction_id\u0006\u0006\u0006\u0006\n";

        //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
        //assertThat("node is not a select node", node instanceof SelectNode);
        //assertThat("from should be table node",
        //        ((SelectNode) node).from.get(0) instanceof JoinNode);

        SqlCommandTestHelper.transformHiveAst(query);
    }


    @Test
    public void testSample2() throws Exception {
        String query = "select      report_timestamp,     to_date(report_timestamp) as impression_date,     campaign_name,      a.campaign_id,      strat.goal_type as strategy_goal_type,  \n" +
                "case when length(delphi_metadata)<1 then 'tree brain'    \n" +
                "when delphi_metadata like '%_sb%' then 'shared brain'    \n" +
                "when delphi_metadata like '%_tf%' then 'log brain' else delphi_metadata end as brain_type,      \n" +
                "case when watermark=0 then 'Non-Watermark' else 'Watermark' end as wm_flag,  \n" +
                "is_viewable as viewable_impressions,   is_measurable as measurable_impressions,      1 as impressions,  clicks as clicks,   total_spend_cpm/1000 as spend,  \n" +
                "-- sum(total_spend_cpm)/count(a.auction_id) as cpm,  -- coalesce(sum(total_spend_cpm)/sum(is_viewable),0) as vcpm,  -- coalesce(sum(in_view_100)/sum(is_measurable),0) as in_view_rate,  \n" +
                "-- coalesce(sum(clicks)/count(a.auction_id),0) as ctr,  -- coalesce(sum(total_spend_cpm/1000)/ sum(clicks),0) as cpc,      video_start as video_start,      \n" +
                "video_complete as video_complete,      pv_conversions,      pc_conversions,      pv_revenue,      pc_revenue,      device,      \n" +
                "case when channel_type in (8,9) then 'in-app' else 'web' end as inventory_type \n" +
                "-- coalesce(sum(video_complete)/sum(video_start),0) as vcr \n  " +
                "from \u0006\u0006 " +
                "(select \u0006  report_timestamp,\u0006   delphi_metadata,\u0006   auction_id,\u0006   campaign_id, " +
                "\u0006   campaign_name, \u0006            strategy_id,\u0006   watermark, \u0006   total_spend_cpm, \u0006            " +
                "device,\u0006            channel_type\u0006from \u0006 mm_impressions_ct imp\u0006 where organization_id = 100977\u0006 " +
                "and impression_date between date_sub('2019-03-04',1) and date_add('2019-03-05',1)\u0006    and to_date(report_timestamp) " +
                "between '2019-03-04' and '2019-03-05'\u0006 and campaign_id in (614838,617477)\u0006 ) a \u0006" +
                "" +
                "join t1_meta_strategy_rs strat on a.strategy_id = strat.id\u0006\u0006" +
                "" +
                "left join \u0006\u0006  (\u0006  select \u0006  imp_auction_id, \u0006     event_timestamp_gmt,\u0006  case when viewability_event_name = 'is_viewable' then 1 " +
                "else 0 end as is_viewable, \u0006    case when viewability_event_name = 'is_measurable' then 1 else 0 end as is_measurable, \u0006    " +
                "case when viewability_event_name = 'is_viewable_100%' then 1 else 0 end as in_view_100\u0006 \u0006 from \u0006  mm_viewability_events_test \u0006 where organization_id in (100977)\u0006 " +
                "and event_date between date_sub('2019-03-04',1) and date_add('2019-03-05',1)\u0006    and to_date(event_report_timestamp) between '2019-03-04' and '2019-03-05'\u0006 and campaign_id in (614838,617477)\u0006 ) b\u0006\u0006" +
                "on a.auction_id = b.imp_auction_id\u0006\u0006" +
                "" +
                "left join \u0006(\u0006  select \u0006  imp_auction_id, \u0006  sum(case when event_subtype = 'q4' then 1 else 0 end) as video_complete,\u0006  sum(case when event_subtype = 'vst' then 1 else 0 end) as video_start, " +
                "\u0006  sum(case when event_type = 'click' then 1 else 0 end) as clicks, \u0006        sum(case when event_type = 'conversion' and pv_pc_flag = 'V' then 1 else 0 end) as pv_conversions, \u0006         sum(case when event_type = 'conversion' and pv_pc_flag = 'C' then 1 else 0 end) as pc_conversions, \u0006        " +
                "sum(case when pv_pc_flag = 'V' then mm_v1 else 0 end) as pv_revenue, \u0006        sum(case when pv_pc_flag = 'C' then mm_v1 else 0 end) as pc_revenue\u0006 from \u0006  mm_attributed_events_ct\u0006 where organization_id in (100977)\u0006 and event_date between date_sub('2019-03-04',1) and date_add('2019-03-05',1)\u0006    " +
                "and to_date(event_report_timestamp) between '2019-03-04' and '2019-03-05'\u0006 and campaign_id in (614838,617477)\u0006    \u0006    group by imp_auction_id\u0006 ) c \u0006\u0006 on a.auction_id = c.imp_auction_id\u0006";


        //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
        //assertThat("node is not a select node", node instanceof SelectNode);
        //assertThat("from should be table node",
        //        ((SelectNode) node).from.get(0) instanceof JoinNode);

        SqlCommandTestHelper.transformHiveAst(query);
    }


    @Test
    public void testSample3() throws Exception {
        String query = "\u0006  select\u0006   at.pv_time_lag,\u0006 at.pixel_id,\u0006 e.v10,\u0006 at.campaign_id,\u0006 at.event_report_timestamp,\u0006 at.device_type,\u0006 at.campaign_name,\u0006 at.event_timestamp_gmt,\u0006 e.s9,\u0006 e.s8,\u0006 at.pixel_name,\u0006 e.s2,\u0006 e.s1,\u0006 e.s7,\u0006 e.s6,\u0006 e.s5,\u0006 e.s4,\u0006 at.organization_name,\u0006 at.strategy_name,\u0006 at.event_date,\u0006 e.s10,\u0006 at.agency_id,\u0006 at.organization_id,\u0006 e.v1,\u0006 at.agency_name,\u0006 e.v3,\u0006 e.v4,\u0006 e.v5,\u0006 e.v6,\u0006 e.v7,\u0006 e.v8,\u0006 e.v9,\u0006 at.impression_timestamp_gmt,\u0006 at.advertiser_id,\u0006 at.pc_time_lag,\u0006 at.country,\u0006 e.v2,\u0006 e.s3,\u0006 at.strategy_id,\u0006 at.browser\u0006  from\u0006   (\u0006     select * from\u0006     mm_events \u0006   ) e\u0006   join \u0006   (\u0006     select * from\u0006     mm_attributed_events_ac\u0006   ) at\u0006   on \u0006      e.pixel_id = at.pixel_id \u0006      and e.mm_uuid = at.mm_uuid\u0006      and e.timestamp_gmt = at.event_timestamp_gmt\u0006  where\u0006   at.advertiser_id in (206401)\u0006 and at.organization_id in (101607) and e.organization_id in (101607)\u0006 and at.agency_id in (117415)\u0006 and at.pixel_id in (1299912)\u0006 and at.event_type = 'conversion'\u0006 and at.event_date between '2019-03-04' and '2019-03-04'\u0006 and e.event_date between '2019-03-04' and '2019-03-04'\u0006\n";


        //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
        //assertThat("node is not a select node", node instanceof SelectNode);
        //assertThat("from should be table node",
        //        ((SelectNode) node).from.get(0) instanceof JoinNode);

        SqlCommandTestHelper.transformHiveAst(query);
    }


    @Test
    public void testSample4() throws Exception {
        String query = "set hive.cli.print.header=false;set hive.resultset.use.unique.column.names=false; " +
                "select  customer,  cluster,  data,  dt,  ingestedtime  from   (select  customer,  cluster,  data,  dt,  ingestedtime,  row_number() over (partition by get_json_object(cluster,'$.clusterid') order by cast(regexp_replace(regexp_replace(ingestedtime, 'T',' '),'Z','') as timestamp) desc) rnum   from dsa_production.aiq_raw_optimized  where dt='2018-01-23'  and source='get_compute_inventory') a   where rnum=1; ";
        SqlCommandTestHelper.transformHiveAst(query);
    }

    @Test
    public void testSample5() throws Exception {
        String query = " SELECT    logdate as the_date,    hour as the_hour,    campaign_key,    campaign_placement_id,    campaign_placement_name,    campaign_placement_key,    stats.ad_key,    a.ad_template,    opt_metric,    threshold,    SUM(complete100) AS Complete100,    SUM(streams_viewed) AS Streams_viewed  FROM    \n" +
                "(       SELECT          logdate,          hour,          campaign_key,          placement_key,          ad_key,          IF(Isnull(streams_viewed), 0, streams_viewed) AS Streams_viewed,          IF(Isnull(complete100), 0, complete100) AS Complete100        FROM          core.stats_log        WHERE          logdate = '2019-03-10'           and hour = '11'     )    stats     \n" +
                "join       \n" +
                "(          SELECT             campaign_placement.campaign_placement_key AS campaign_placement_key,             campaign_placement.campaign_placement_id AS campaign_placement_id,             campaign_placement.campaign_placement_name AS campaign_placement_name,             opt_metric,             threshold           FROM             promo2.campaign_placement             \n" +
                " join                \n" +
                "(                   SELECT                      campaign_placement_id,                      opt_metric,                      threshold                    FROM                      promo2.campaign_placement_optimization_filter                    WHERE                      upper(opt_metric) LIKE '%COMPLETIONS_KPI%'                       AND status = 'Active'                )                selected_placements                \n" +
                " ON campaign_placement.campaign_placement_id = selected_placements.campaign_placement_id       )       selected_placements_decorated       \n" +
                " ON selected_placements_decorated.campaign_placement_key = stats.placement_key     \n" +
                "join       promo2.ad a        ON stats.ad_key = a.ad_key     GROUP BY    logdate,    hour,    campaign_key,    campaign_placement_id,    campaign_placement_name,    campaign_placement_key,    stats.ad_key,    a.ad_template,    opt_metric,    threshold";
        //Node node = evaluateJsonAndGetNode(resultDirPath.concat("/AST2.json"), query);
        //assertThat("node is not a select node", node instanceof SelectNode);
        //assertThat("from should be table node",
        //        ((SelectNode) node).from.get(0) instanceof JoinNode);

        SqlCommandTestHelper.transformHiveAst(query);
    }


}
