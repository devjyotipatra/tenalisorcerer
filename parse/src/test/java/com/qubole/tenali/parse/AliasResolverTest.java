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
        SqlCommandTestHelper.parseHive(command);
        //SqlCommandTestHelper.transformPrestoQuery(command);
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
        SqlCommandTestHelper.parseHive(command);
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
                "group by 1, 2\n" +
                "order by 3 desc";
        //"{"type":"select","from":{"type":"as","aliasName":"TAB","value":{"type":"identifier","name":"TABLE1"}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":18868150}"
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseHive(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testComplexCTASQuery() throws Exception {
        String command = "create table dw_temp_swap.transactions  stored as orc   as Select lt.loan_id , lt.credit_line_id , case when lt.credit_line_id is not null then 'credit_line'     when prod_type.hsa_uuid is not null then 'hard_secured'     when prod_type.ara_uuid is not null then 'auto_refi'     else 'unsecured' end as product_type, lt.id as loan_task_id , lt.status as loan_task_status , lt.type as loan_task_type , cast(lt.eff_date as date) as loan_task_eff_date , from_utc_timestamp(lt.created_at, 'CST') as loan_task_created_time , from_utc_timestamp(lt.updated_at, 'CST') as loan_task_updated_time , lt2.id as parent_loan_task_id , lt2.type as parent_loan_task_type , case when lt.type in ('take_payment', 'add_credit_breakdown') and lt.status = 'completed' then 1 else 0 end as completed_transaction , pt.id as payment_transaction_id , pt.status as payment_transaction_status , cast(pt.eff_date as date) as pt_eff_date , from_utc_timestamp(pt.created_at, 'CST') as payment_transaction_created_time , pt.debit_account as debit_account , pt.credit_account as credit_account , round(pt.amount_cents / 100.0, 2) as payment_transaction_amount , cast(ad.prev_biz_day as date) as payment_transaction_eff_prev_biz_date , cast(ad.next_biz_day as date) as payment_transaction_eff_next_biz_date , payments.method as payment_method , payments2.method as parent_payment_method , cast(coalesce(payments.eff_date, payments2.eff_date) as timestamp) as payment_eff_date , payments.installment_id , round(pt.amount_cents/100.0,2)*acc.is_exp_fees_late_credit as is_exp_fees_late_credit , round(pt.amount_cents/100.0,2)*acc.is_exp_fees_nsf_credit as is_exp_fees_nsf_credit , round(pt.amount_cents/100.0,2)*acc.is_exp_interest_credit as is_exp_interest_credit , round(pt.amount_cents/100.0,2)*acc.is_exp_principal_credit as is_exp_principal_credit , round(pt.amount_cents/100.0,2)*acc.is_exp_refunds as is_exp_refunds , round(pt.amount_cents/100.0,2)*acc.is_exp_unearned_cash_credit as is_exp_unearned_cash_credit , round(pt.amount_cents/100.0,2)*acc.is_rev_admin_fee as is_rev_admin_fee , round(pt.amount_cents/100.0,2)*acc.is_rev_fees_late as is_rev_fees_late , round(pt.amount_cents/100.0,2)*acc.is_rev_fees_nsf as is_rev_fees_nsf , round(pt.amount_cents/100.0,2)*acc.bank_is_rev_admin_fee as bank_is_rev_admin_fee , round(pt.amount_cents/100.0,2)*acc.is_exp_bank_fee as is_exp_bank_fee , round(pt.amount_cents/100.0,2)*acc.is_exp_bank_interest as is_exp_bank_interest, round(pt.amount_cents/100.0,2)*acc.bs_a_fees_late_ar as bs_a_fees_late_ar , round(pt.amount_cents/100.0,2)*acc.bs_a_fees_nsf_ar as bs_a_fees_nsf_ar , round(pt.amount_cents/100.0,2)*acc.bs_a_interest_ar as bs_a_interest_ar , round(pt.amount_cents/100.0,2)*acc.bs_a_principal_ar as bs_a_principal_ar , round(pt.amount_cents/100.0,2)*acc.bs_l_refunds_escrow as bs_l_refunds_escrow , round(pt.amount_cents/100.0,2)*acc.bs_l_refunds_escrow_fees_late as bs_l_refunds_escrow_fees_late , round(pt.amount_cents/100.0,2)*acc.bs_l_refunds_escrow_fees_nsf as bs_l_refunds_escrow_fees_nsf , round(pt.amount_cents/100.0,2)*acc.bs_l_refunds_escrow_general as bs_l_refunds_escrow_general , round(pt.amount_cents/100.0,2)*acc.bs_l_refunds_escrow_interest as bs_l_refunds_escrow_interest , round(pt.amount_cents/100.0,2)*acc.bs_l_refunds_escrow_principal as bs_l_refunds_escrow_principal , round(pt.amount_cents/100.0,2)*acc.bs_l_refunds_escrow_unearned_cash as bs_l_refunds_escrow_unearned_cash , round(pt.amount_cents/100.0,2)*acc.bs_l_refunds_payable as bs_l_refunds_payable , round(pt.amount_cents/100.0,2)*acc.bs_l_unearned_cash as bs_l_unearned_cash , round(pt.amount_cents/100.0,2)*acc.bank_bs_a_principal_ar as bank_bs_a_principal_ar , round(pt.amount_cents/100.0,2)*acc.bs_a_transaction_fee as bs_a_transaction_fee , round(pt.amount_cents/100.0,2)*acc.cf_fees_late_refi_payoff as cf_fees_late_refi_payoff , round(pt.amount_cents/100.0,2)*acc.cf_fees_late_take as cf_fees_late_take , round(pt.amount_cents/100.0,2)*acc.cf_fees_nsf_refi_payoff as cf_fees_nsf_refi_payoff , round(pt.amount_cents/100.0,2)*acc.cf_fees_nsf_take as cf_fees_nsf_take , round(pt.amount_cents/100.0,2)*acc.cf_interest_refi_payoff as cf_interest_refi_payoff , round(pt.amount_cents/100.0,2)*acc.cf_interest_take as cf_interest_take , round(pt.amount_cents/100.0,2)*acc.cf_issue as cf_issue , round(pt.amount_cents/100.0,2)*acc.cf_issue_purchase as cf_issue_purchase , round(pt.amount_cents/100.0,2)*acc.cf_issue_refi as cf_issue_refi , round(pt.amount_cents/100.0,2)*acc.cf_issue_refi_purchase as cf_issue_refi_purchase , round(pt.amount_cents/100.0,2)*acc.cf_net_admin_fee_take as cf_net_admin_fee_take , round(pt.amount_cents/100.0,2)*acc.cf_principal_refi_payoff as cf_principal_refi_payoff , round(pt.amount_cents/100.0,2)*acc.cf_principal_take as cf_principal_take , round(pt.amount_cents/100.0,2)*acc.cf_refunds_pay as cf_refunds_pay , round(pt.amount_cents/100.0,2)*acc.cf_unearned_cash_refi_payoff as cf_unearned_cash_refi_payoff , round(pt.amount_cents/100.0,2)*acc.cf_unearned_cash_take as cf_unearned_cash_take , round(pt.amount_cents/100.0,2)*acc.bank_cf_issue as bank_cf_issue , round(pt.amount_cents/100.0,2)*acc.bank_cf_issue_refi as bank_cf_issue_refi , round(pt.amount_cents/100.0,2)*acc.bank_cf_principal_transfer as bank_cf_principal_transfer , round(pt.amount_cents/100.0,2)*acc.cf_bank_fee_pay as cf_bank_fee_pay , round(pt.amount_cents/100.0,2)*acc.cf_bank_interest_pay as cf_bank_interest_pay , round(pt.amount_cents/100.0,2)*acc.principal_collected as principal_collected , round(pt.amount_cents/100.0,2)*acc.interest_collected as interest_collected , round(pt.amount_cents/100.0,2)*acc.nsf_fees_collected as nsf_fees_collected , round(pt.amount_cents/100.0,2)*acc.late_fees_collected as late_fees_collected , round(pt.amount_cents/100.0,2)*acc.unearned_cash_collected as unearned_cash_collected , round(pt.amount_cents/100.0,2)*acc.general_refund_collected as general_refund_collected , acc.is_credit = 1 as is_credit, case when lt.type = 'book_interest' then round(pt.amount_cents/100.0,2) else 0.00 end as interest_booked From avant_basic.payment_transactions pt Join avant_basic.loan_tasks lt on lt.id = pt.loan_task_id Left Join avant_basic.loan_tasks lt2 on lt2.id = lt.parent_loan_task_id Left Join avant_basic.account_date ad on ad.day = pt.eff_date Left Join dw_ref.financial_statement_indicators acc on acc.loan_task_type = lt.type   and pt.debit_account = acc.debit_account   and pt.credit_account = acc.credit_account Left Join (   Select id, method, loan_task_id, eff_date, created_at, installment_id, row_number() OVER (PARTITION BY loan_task_id ORDER BY created_at) as row_num   From avant_basic.payments   ) payments on lt.id = payments.loan_task_id and payments.row_num = 1 Left Join (   Select id, method, loan_task_id, eff_date, created_at, installment_id, row_number() OVER (PARTITION BY loan_task_id ORDER BY created_at) as row_num   From avant_basic.payments   ) payments2 on lt.parent_loan_task_id = payments2.loan_task_id and payments2.row_num = 1 Left Join (   Select l.id as loan_id, hsa.uuid as hsa_uuid, ara.uuid as ara_uuid, row_number() over (partition by l.id) as row_num   From avant_basic.loans l   Left Join avant_basic.customer_applications ca on ca.id = l.customer_application_id   Left Join avant_basic.hard_secured_attempts hsa on hsa.customer_application_uuid = ca.uuid   Left Join avant_basic.auto_refinance_attempts ara on ara.customer_application_uuid = ca.uuid   ) prod_type on prod_type.loan_id = lt.loan_id and prod_type.row_num = 1 Where pt.status != 'cancelled'\n";
        //{"type":"select","from":{"type":"join","joinType":"INNER","leftNode":{"type":"as","aliasName":"TAB1","value":{"type":"identifier","name":"TABLE1"}},"rightNode":{"type":"as","aliasName":"TAB2","value":{"type":"identifier","name":"TABLE2"}},"joinCondition":{"type":"operator","operator":"=","operands":{"type":"list","operandlist":[{"type":"identifier","name":"TAB1.X"},{"type":"identifier","name":"TAB2.Y"}]}}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":89793374}

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseHive(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }

    @Test
    public void testSimpleSubQuery() throws Exception {
        String command = "select acc_id, tmp.id, dt, tmp.tag from (select account_id as acc_id, hist.id, tag, dt, hist.submit_time from rstore.query_hists hist where hist.dt>0) tmp";

        //{"type":"select","from":{"type":"join","joinType":"INNER","leftNode":{"type":"as","aliasName":"TAB1","value":{"type":"identifier","name":"TABLE1"}},"rightNode":{"type":"as","aliasName":"TAB2","value":{"type":"identifier","name":"TABLE2"}},"joinCondition":{"type":"operator","operator":"=","operands":{"type":"list","operandlist":[{"type":"identifier","name":"TAB1.X"},{"type":"identifier","name":"TAB2.Y"}]}}},"columns":{"type":"list","operandlist":[{"type":"identifier","name":"A"},{"type":"identifier","name":"TAB.B"}]},"vid":89793374}

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseHive(command);
        //assertThat("correct number of queries is 1", cctx.getListQueryContext().size()==1);
    }


    @Test
    public void testSimpleNestedSubQuery() throws Exception {
        String command = "select account_id_id, id, dt2, tag from (select acc_id as account_id_id, tmp.id, dt as dt2, tmp.tag from (select account_id as acc_id, hist.id, tag, dt, hist.submit_time from rstore.query_hists hist where hist.dt>0) tmp) exp";

        String result = "{\"type\":\"select\",\"from\":{\"type\":\"select\",\"from\":{\"type\":\"select\",\"from\":{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS\"},\"columns\":{\"type\":\"list\",\"operandlist\":[{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ACCOUNT_ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.DT\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.SUBMIT_TIME\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.TAG\"}]},\"vid\":73971918},\"columns\":{\"type\":\"list\",\"operandlist\":[{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ACCOUNT_ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.DT\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.TAG\"}]},\"vid\":82411964},\"columns\":{\"type\":\"list\",\"operandlist\":[{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ACCOUNT_ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.DT\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.ID\"},{\"type\":\"identifier\",\"name\":\"RSTORE.QUERY_HISTS.TAG\"}]},\"vid\":64610793}\n";
        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseHive(command);
        //assertEquals(ast.toString(), result);
    }

    @Test
    public void testErrorNode() throws Exception {
        String command = "select a, b, c from rstore.query_hists hist where ";

        //SqlCommandTestHelper.parseHive(command);
        SqlCommandTestHelper.parseHive(command);
    }

}
