package com.qubole.tenali.partial

import com.qubole.tenali.partial.parser.SQLParser
import org.specs2.mutable._

object Queries {
  lazy val q00 = """
select
  l_returnflag,
  l_linestatus,
  sum(l_quantity) as sum_qty,
  sum(l_extendedprice) as sum_base_price,
  sum(l_extendedprice * (1 - l_discount)) as sum_disc_price,
  sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge,
  avg(l_quantity) as avg_qty,
  avg(l_extendedprice) as avg_price,
  avg(l_discount) as avg_disc,
  count(*) as count_order
  from __ANY__
  """


  lazy val q01 = """
select
  lineitem.l_returnflag as llr,
  l_linestatus,
  sum(l_quantity) as sum_qty,
  sum(l_extendedprice) as sum_base_price,
  sum(l_extendedprice * (1 - l_discount)) as sum_disc_price,
  sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge,
  avg(l_quantity) as avg_qty,
  avg(l_extendedprice) as avg_price,
  avg(l_discount) as avg_disc,
  count(*) as count_order
from
  __ANY__
where
  __ANY__"""

  lazy val q1 = """
select
  l_returnflag,
  l_linestatus,
  sum(l_quantity) as sum_qty,
  sum(l_extendedprice) as sum_base_price,
  sum(l_extendedprice * (1 - l_discount)) as sum_disc_price,
  sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge,
  avg(l_quantity) as avg_qty,
  avg(l_extendedprice) as avg_price,
  avg(l_discount) as avg_disc,
  count(*) as count_order
from
  lineitem
where
  l_shipdate <= date '1998-12-01' - interval '5' day
group by
  __ANY__"""

  lazy val q2 = """
select
  s_acctbal,
  s_name,
  n_name,
  p_partkey,
  p_mfgr,
  s_address,
  s_phone,
  s_comment
from
  part,
  (select __ANY__) s,
  supplier,
  partsupp,
  nation,
  region
where
  p_partkey = ps_partkey
  and s_suppkey = ps_suppkey
  and p_size = 10
  and p_type like '%foo'
  and s_nationkey = n_nationkey
  and n_regionkey = r_regionkey
  and r_name = 'somename'
  and ps_supplycost = (
    select
      min(ps_supplycost)
    from
      partsupp,
      supplier,
      nation,
      region
    where
      p_partkey = ps_partkey
      and s_suppkey = ps_suppkey
      and s_nationkey = n_nationkey
      and n_regionkey = r_regionkey
      and r_name = 'somename'
  )
order by
  s_acctbal desc,
  n_name,
  s_name,
  p_partkey
limit 100;
"""

  lazy val q3 = """
select
  l_orderkey,
  sum(l_extendedprice * (1 - l_discount)) as revenue,
  __ANY__
from
  customer,
  __ANY__
where
  c_mktsegment = 'somesegment'
  and c_custkey = o_custkey
  and l_orderkey = o_orderkey
  and o_orderdate < date '1999-01-01'
  and l_shipdate > date '1999-01-01'
group by
  l_orderkey,
  o_orderdate,
  o_shippriority
order by
  __ANY__
"""

  lazy val q4 = """
select
  o_orderpriority,
  count(*) as order_count
from
  orders
  left join
  (
     select
       *
     from
       __ANY__
  ) s
  on __ANY__
where
  o_orderdate >= date '1999-01-01'
group by
  o_orderpriority
order by
  o_orderpriority;
"""

  lazy val q5 = """
select
  n_name,
  sum(l_extendedprice * (1 - l_discount)) as revenue
from
  customers right join __ANY__
on
  c_custkey = o_custkey
  and l_orderkey = o_orderkey
  and l_suppkey = s_suppkey
"""

  lazy val q6 = """
select
  sum(l_extendedprice * l_discount) as revenue
from
  lineitem
where
  __ANY__
"""

  lazy val q7 = """
select
  supp_nation,
  cust_nation,
  l_year,
  sum(volume) as revenue
from
  (
    select
      n1.n_name as supp_nation,
      n2.n_name as cust_nation,
      extract(year from l_shipdate) as l_year,
      l_extendedprice * (1 - l_discount) as volume
    from
      supplier,
      lineitem
    where
      s_suppkey = l_suppkey
      and (
        (n1.n_name = 'a' and n2.n_name = 'b')
        or (n1.n_name = 'b' and n2.n_name = 'a')
      )
      and l_shipdate between date '1995-01-01' and date '1996-12-31'
  ) as shipping
  join
  (select __ANY__ from __ANY__) s
"""
}


class SQLParserSpec extends Specification {

  lazy val parser = new SQLParser

  "SQLParser" should {

    "parse query00" in {
      val r = parser.parse(Queries.q00)
      println(r)
      r should beSome
    }


    "parse query01" in {
      val r = parser.parse(Queries.q01)
      println(r)
      r should beSome
    }

     "parse query1" in {
       val r = parser.parse(Queries.q1)
       println(r)
       r should beSome
     }

     "parse query2" in {
       val r = parser.parse(Queries.q2)
       println(r)
       r should beSome
     }

     "parse query3" in {
       val r = parser.parse(Queries.q3)
       println(r)
       r should beSome
     }

    "parse query4" in {
       val r = parser.parse(Queries.q4)
       println(r)
       r should beSome
     }

     "parse query5" in {
       val r = parser.parse(Queries.q5)
       println(r)
       r should beSome
     }

     "parse query6" in {
       val r = parser.parse(Queries.q6)
       println(r)
       r should beSome
     }

     "parse query7" in {
       val r = parser.parse(Queries.q7)
       println(r)
       r should beSome
     }
  }
}
