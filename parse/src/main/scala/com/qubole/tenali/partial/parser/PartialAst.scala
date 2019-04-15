package com.qubole.tenali.partial.parser

import com.qubole.tenali.parse.util.PrettyPrinters

trait Node extends PrettyPrinters {
// emit sql repr of node
  def sql: String
}

case class SelectStmt(projections: Seq[SqlProj],
                      relations: Option[Seq[SqlRelation]],
                      filter: Option[SqlExpr],
                      groupBy: Option[SqlGroupBy],
                      orderBy: Option[SqlOrderBy],
                      limit: Option[Int]) extends Node {
  def sql =
    Seq(Some("select"),
      Some(projections.map(_.sql).mkString(", ")),
      relations.map(x => "from " + x.map(_.sql).mkString(", ")),
      filter.map(x => "where " + x.sql),
      groupBy.map(_.sql),
      orderBy.map(_.sql),
      limit.map(x => "limit " + x.toString)).flatten.mkString(" ")
}

trait SqlProj extends Node
case class ExprProj(expr: SqlExpr, alias: Option[String]) extends SqlProj {
  def sql = Seq(Some(expr.sql), alias).flatten.mkString(" as ")
}

case class StarProj() extends SqlProj {
  def sql = "*"
}

trait SqlExpr extends Node {
  //def getType: DataType = UnknownType
  def isLiteral: Boolean = false

  // is the r-value of this expression a literal?
  def isRValueLiteral: Boolean = isLiteral

  // (col, true if aggregate context false otherwise)
  // only gathers fields within this context (
  // wont traverse into subselects )
  def gatherFields: Seq[(FieldIdent, Boolean)]
}

trait Binop extends SqlExpr {
  val lhs: SqlExpr
  val rhs: SqlExpr

  val opStr: String

  override def isLiteral = lhs.isLiteral && rhs.isLiteral
  def gatherFields = lhs.gatherFields ++ rhs.gatherFields

  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr): Binop

  def sql = Seq("(" + lhs.sql + ")", opStr, "(" + rhs.sql + ")") mkString " "
}

case class Or(lhs: SqlExpr, rhs: SqlExpr) extends Binop {
  val opStr = "or"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}
case class And(lhs: SqlExpr, rhs: SqlExpr) extends Binop {
  val opStr = "and"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}

trait EqualityLike extends Binop
case class Eq(lhs: SqlExpr, rhs: SqlExpr) extends EqualityLike {
  val opStr = "="
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}
case class Neq(lhs: SqlExpr, rhs: SqlExpr) extends EqualityLike {
  val opStr = "<>"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}

//wang Ge/Le/Gt/Lt
trait InequalityLike extends Binop
case class Ge(lhs: SqlExpr, rhs: SqlExpr) extends InequalityLike {
  val opStr = ">="
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}
case class Gt(lhs: SqlExpr, rhs: SqlExpr) extends InequalityLike {
  val opStr = ">"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}
case class Le(lhs: SqlExpr, rhs: SqlExpr) extends InequalityLike {
  val opStr = "<="
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}
case class Lt(lhs: SqlExpr, rhs: SqlExpr) extends InequalityLike {
  val opStr = "<"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}

case class In(elem: SqlExpr, set: Seq[SqlExpr], negate: Boolean) extends SqlExpr {
  override def isLiteral =
    elem.isLiteral && set.filter(e => !e.isLiteral).isEmpty
  def gatherFields =
    elem.gatherFields ++ set.flatMap(_.gatherFields)
  def sql = Seq(elem.sql, "in", "(", set.map(_.sql).mkString(", "), ")") mkString " "
}
case class Like(lhs: SqlExpr, rhs: SqlExpr, negate: Boolean) extends Binop {
  val opStr = if (negate) "not like" else "like"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}

case class Plus(lhs: SqlExpr, rhs: SqlExpr) extends Binop {
  val opStr = "+"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}
case class Minus(lhs: SqlExpr, rhs: SqlExpr) extends Binop {
  val opStr = "-"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}

case class Mult(lhs: SqlExpr, rhs: SqlExpr) extends Binop {
  val opStr = "*"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}
case class Div(lhs: SqlExpr, rhs: SqlExpr) extends Binop {
  val opStr = "/"
  def copyWithChildren(lhs: SqlExpr, rhs: SqlExpr) = copy(lhs = lhs, rhs = rhs)
}

trait Unop extends SqlExpr {
  val expr: SqlExpr
  val opStr: String
  override def isLiteral = expr.isLiteral
  def gatherFields = expr.gatherFields
  def sql = Seq(opStr, "(", expr.sql, ")") mkString " "
}

case class Not(expr: SqlExpr) extends Unop {
  val opStr = "not"
}
case class Exists(select: Subselect) extends SqlExpr {
  def gatherFields = Seq.empty
  def sql = Seq("exists", "(", select.sql, ")") mkString " "
}

case class FieldIdent(qualifier: Option[String], name: String) extends SqlExpr {
  def gatherFields = Seq((this, false))
  def sql = Seq(qualifier, Some(name)).flatten.mkString(".")
}

case class Subselect(subquery: SelectStmt) extends SqlExpr {
  def gatherFields = Seq.empty
  def sql = "(" + subquery.sql + ")"
}

trait SqlAgg extends SqlExpr
case class CountStar() extends SqlAgg {
  def gatherFields = Seq.empty
  def sql = "count(*)"
}
case class CountExpr(expr: SqlExpr, distinct: Boolean) extends SqlAgg {
  def gatherFields = expr.gatherFields.map(_.copy(_2 = true))
  def sql = Seq(Some("count("), if (distinct) Some("distinct ") else None, Some(expr.sql), Some(")")).flatten.mkString("")
}
case class Sum(expr: SqlExpr, distinct: Boolean) extends SqlAgg {
  def gatherFields = expr.gatherFields.map(_.copy(_2 = true))
  def sql = Seq(Some("sum("), if (distinct) Some("distinct ") else None, Some(expr.sql), Some(")")).flatten.mkString("")
}
case class Avg(expr: SqlExpr, distinct: Boolean) extends SqlAgg {
  def gatherFields = expr.gatherFields.map(_.copy(_2 = true))
  def sql = Seq(Some("avg("), if (distinct) Some("distinct ") else None, Some(expr.sql), Some(")")).flatten.mkString("")
}
case class Min(expr: SqlExpr) extends SqlAgg {
  def gatherFields = expr.gatherFields.map(_.copy(_2 = true))
  def sql = "min(" + expr.sql + ")"
}
case class Max(expr: SqlExpr) extends SqlAgg {
  def gatherFields = expr.gatherFields.map(_.copy(_2 = true))
  def sql = "max(" + expr.sql + ")"
}
case class GroupConcat(expr: SqlExpr, sep: String) extends SqlAgg {
  def gatherFields = expr.gatherFields.map(_.copy(_2 = true))
  def sql = Seq("group_concat(", Seq(expr.sql, _q(sep)).mkString(", "), ")").mkString("")
}
case class AggCall(name: String, args: Seq[SqlExpr]) extends SqlAgg {
  def gatherFields = args.flatMap(_.gatherFields)
  def sql = Seq(name, "(", args.map(_.sql).mkString(", "), ")").mkString("")
}

trait SqlFunction extends SqlExpr {
  val name: String
  val args: Seq[SqlExpr]
  override def isLiteral = args.foldLeft(true)(_ && _.isLiteral)
  def gatherFields = args.flatMap(_.gatherFields)
  def sql = Seq(name, "(", args.map(_.sql) mkString ", ", ")") mkString ""
}

case class FunctionCall(name: String, args: Seq[SqlExpr]) extends SqlFunction

sealed abstract trait ExtractType
case object YEAR extends ExtractType
case object MONTH extends ExtractType
case object DAY extends ExtractType

case class Extract(expr: SqlExpr, what: ExtractType) extends SqlFunction {
  val name = "extract"
  val args = Seq(expr)
}

case class Substring(expr: SqlExpr, from: Int, length: Option[Int]) extends SqlFunction {
  val name = "substring"
  val args = Seq(expr)
}

case class CaseExprCase(cond: SqlExpr, expr: SqlExpr) extends Node {
  def sql = Seq("when", cond.sql, "then", expr.sql) mkString " "
}
case class CaseExpr(expr: SqlExpr, cases: Seq[CaseExprCase], default: Option[SqlExpr]) extends SqlExpr {
  override def isLiteral =
    expr.isLiteral &&
      cases.filter(x => !x.cond.isLiteral || !x.expr.isLiteral).isEmpty &&
      default.map(_.isLiteral).getOrElse(true)
  override def isRValueLiteral =
    cases.filterNot(_.expr.isRValueLiteral).isEmpty &&
      default.map(_.isRValueLiteral).getOrElse(true)
  def gatherFields =
    expr.gatherFields ++
      cases.flatMap(x => x.cond.gatherFields ++ x.expr.gatherFields) ++
      default.map(_.gatherFields).getOrElse(Seq.empty)
  def sql = Seq(Some("case"), Some(expr.sql), Some(cases.map(_.sql) mkString " "), default.map(d => "else " + d.sql), Some("end")).flatten.mkString(" ")
}
case class CaseWhenExpr(cases: Seq[CaseExprCase], default: Option[SqlExpr]) extends SqlExpr {
  override def isLiteral =
    cases.filter(x => !x.cond.isLiteral || !x.expr.isLiteral).isEmpty &&
      default.map(_.isLiteral).getOrElse(true)
  override def isRValueLiteral =
    cases.filterNot(_.expr.isRValueLiteral).isEmpty &&
      default.map(_.isRValueLiteral).getOrElse(true)
  def gatherFields =
    cases.flatMap(x => x.cond.gatherFields ++ x.expr.gatherFields) ++
      default.map(_.gatherFields).getOrElse(Seq.empty)
  def sql = Seq(Some("case"), Some(cases.map(_.sql) mkString " "), default.map(d => "else " + d.sql), Some("end")).flatten.mkString(" ")
}

case class UnaryPlus(expr: SqlExpr) extends Unop {
  val opStr = "+"
}
case class UnaryMinus(expr: SqlExpr) extends Unop {
  val opStr = "-"
}

trait LiteralExpr extends SqlExpr {
  override def isLiteral = true
  def gatherFields = Seq.empty
}
case class IntLiteral(v: Long) extends LiteralExpr {
  def sql = v.toString
}
case class FloatLiteral(v: Double) extends LiteralExpr {
  def sql = v.toString
}
case class StringLiteral(v: String) extends LiteralExpr {
  def sql = "\"" + v.toString + "\""
}
case class NullLiteral() extends LiteralExpr {
  def sql = "null"
}
case class DateLiteral(d: String) extends LiteralExpr {
  def sql = Seq("date", "\"" + d + "\"") mkString " "
}
case class IntervalLiteral(e: String, unit: ExtractType) extends LiteralExpr {
  def sql = Seq("interval", "\"" + e + "\"", unit.toString) mkString " "
}

trait SqlRelation extends Node
case class TableRelationAST(name: String, alias: Option[String]) extends SqlRelation {
  def sql = Seq(Some(name), alias).flatten.mkString(" ")
}
case class SubqueryRelationAST(subquery: SelectStmt, alias: String) extends SqlRelation {
  def sql = Seq("(", subquery.sql, ")", "as", alias) mkString " "
}

sealed abstract trait JoinType {
  def sql: String
}
case object LeftJoin extends JoinType {
  def sql = "left join"
}
case object RightJoin extends JoinType {
  def sql = "right join"
}
case object InnerJoin extends JoinType {
  def sql = "join"
}

case class JoinRelation(left: SqlRelation, right: SqlRelation, tpe: JoinType, clause: SqlExpr) extends SqlRelation {
  def sql = Seq(left.sql, tpe.sql, right.sql, "on", "(", clause.sql, ")") mkString " "
}

sealed abstract trait OrderType
case object ASC extends OrderType
case object DESC extends OrderType

case class SqlGroupBy(keys: Seq[SqlExpr], having: Option[SqlExpr]) extends Node {
  def sql = Seq(Some("group by"), Some(keys.map(_.sql).mkString(", ")), having.map(e => "having " + e.sql)).flatten.mkString(" ")
}
case class SqlOrderBy(keys: Seq[(SqlExpr, OrderType)]) extends Node {
  def sql = Seq("order by", keys map (x => x._1.sql + " " + x._2.toString) mkString ", ") mkString " "
}
