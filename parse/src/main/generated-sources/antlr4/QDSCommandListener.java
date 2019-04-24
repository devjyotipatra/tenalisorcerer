// Generated from antlr4/QDSCommand.g4 by ANTLR 4.7
package antlr4;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link QDSCommandParser}.
 */
public interface QDSCommandListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link QDSCommandParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(QDSCommandParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDSCommandParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(QDSCommandParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link QDSCommandParser#sql_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSql_stmt(QDSCommandParser.Sql_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link QDSCommandParser#sql_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSql_stmt(QDSCommandParser.Sql_stmtContext ctx);
}