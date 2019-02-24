package com.qubole.tenali.parse;

import com.qubole.tenali.util.SqlCommandTestHelper;
import org.junit.Test;

public class CalciteAstTransformationTest {

    @Test
    public void testSimpleStructQuery() throws Exception {
        String command = "SELECT t.hbaserowkey as rowkey, regexp_extract(hbase.key, ':(.*)', 1) as col_name FROM ad.ad_aggregate_day_conversion_date_of_conversion t WHERE t.aggregationSubType.val != 12";


        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testMultipleJoinQuery() throws Exception {
        String command = "SELECT id, get_json(u.json, 'location') as info, to_date(created_at) as dt FROM db_users u JOIN (SELECT by_date.location, dt, num_users * 1000.0 / total_users as frac_of_location, num_users, total_users FROM (SELECT get_json(json, 'location') as location, to_date(created_at) as dt, count(*) num_users FROM db_users WHERE to_date(created_at) >= '2016-08-13'  AND to_date(created_at) <= '2016-08-13'  AND LENGTH(get_json(json, 'location')) > 3  GROUP BY get_json(json, 'location'), to_date(created_at)) by_date JOIN (SELECT get_json(json, 'location') as location, count(*) total_users FROM db_users  WHERE LENGTH(get_json(json, 'location')) > 3  GROUP BY get_json(json, 'location')  HAVING count(*) > 5000) common_locations ON common_locations.location = by_date.location) loc_frac_by_date ON get_json(u.json, 'location') = loc_frac_by_date.location AND to_date(u.created_at) = loc_frac_by_date.dt WHERE loc_frac_by_date.frac_of_location > 7 DISTRIBUTE BY dt";



        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parsePrestoQuery(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }
}
