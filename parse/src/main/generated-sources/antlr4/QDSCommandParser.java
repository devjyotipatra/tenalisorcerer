// Generated from antlr4/QDSCommand.g4 by ANTLR 4.7
package antlr4;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QDSCommandParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Q_SET=1, Q_ADD_JAR=2, Q_USE=3, Q_CREATE_FUNCTION=4, Q_INSERT_INTO=5, Q_INSERT_OVERWRITE=6, 
		Q_SELECT=7, Q_DROP_TABLE=8, Q_DROP_VIEW=9, Q_ALTER_TABLE=10, Q_CREATE_TABLE=11, 
		Q_CREATE_DATABASE=12, Q_CREATE_EXTERNAL_TABLE=13, Q_CREATE_TEMP_TABLE=14, 
		Q_CREATE_VIEW=15, Q_CTE=16, TEXT=17, Q_SEMI=18, SIMPLE_COMMENT=19, BRACKETED_COMMENT=20, 
		SPACES=21;
	public static final int
		RULE_parse = 0, RULE_sql_stmt = 1;
	public static final String[] ruleNames = {
		"parse", "sql_stmt"
	};

	private static final String[] _LITERAL_NAMES = {
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "Q_SET", "Q_ADD_JAR", "Q_USE", "Q_CREATE_FUNCTION", "Q_INSERT_INTO", 
		"Q_INSERT_OVERWRITE", "Q_SELECT", "Q_DROP_TABLE", "Q_DROP_VIEW", "Q_ALTER_TABLE", 
		"Q_CREATE_TABLE", "Q_CREATE_DATABASE", "Q_CREATE_EXTERNAL_TABLE", "Q_CREATE_TEMP_TABLE", 
		"Q_CREATE_VIEW", "Q_CTE", "TEXT", "Q_SEMI", "SIMPLE_COMMENT", "BRACKETED_COMMENT", 
		"SPACES"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "QDSCommand.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public QDSCommandParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ParseContext extends ParserRuleContext {
		public Sql_stmtContext sql_stmt() {
			return getRuleContext(Sql_stmtContext.class,0);
		}
		public TerminalNode EOF() { return getToken(QDSCommandParser.EOF, 0); }
		public List<TerminalNode> SPACES() { return getTokens(QDSCommandParser.SPACES); }
		public TerminalNode SPACES(int i) {
			return getToken(QDSCommandParser.SPACES, i);
		}
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDSCommandListener ) ((QDSCommandListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDSCommandListener ) ((QDSCommandListener)listener).exitParse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QDSCommandVisitor ) return ((QDSCommandVisitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(7);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SPACES) {
				{
				{
				setState(4);
				match(SPACES);
				}
				}
				setState(9);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(10);
			sql_stmt();
			setState(11);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Sql_stmtContext extends ParserRuleContext {
		public Token op;
		public TerminalNode Q_SET() { return getToken(QDSCommandParser.Q_SET, 0); }
		public TerminalNode Q_ADD_JAR() { return getToken(QDSCommandParser.Q_ADD_JAR, 0); }
		public TerminalNode Q_USE() { return getToken(QDSCommandParser.Q_USE, 0); }
		public TerminalNode Q_CTE() { return getToken(QDSCommandParser.Q_CTE, 0); }
		public TerminalNode Q_CREATE_FUNCTION() { return getToken(QDSCommandParser.Q_CREATE_FUNCTION, 0); }
		public TerminalNode Q_INSERT_INTO() { return getToken(QDSCommandParser.Q_INSERT_INTO, 0); }
		public TerminalNode Q_INSERT_OVERWRITE() { return getToken(QDSCommandParser.Q_INSERT_OVERWRITE, 0); }
		public TerminalNode Q_SELECT() { return getToken(QDSCommandParser.Q_SELECT, 0); }
		public TerminalNode Q_DROP_TABLE() { return getToken(QDSCommandParser.Q_DROP_TABLE, 0); }
		public TerminalNode Q_DROP_VIEW() { return getToken(QDSCommandParser.Q_DROP_VIEW, 0); }
		public TerminalNode Q_ALTER_TABLE() { return getToken(QDSCommandParser.Q_ALTER_TABLE, 0); }
		public TerminalNode Q_CREATE_TABLE() { return getToken(QDSCommandParser.Q_CREATE_TABLE, 0); }
		public TerminalNode Q_CREATE_EXTERNAL_TABLE() { return getToken(QDSCommandParser.Q_CREATE_EXTERNAL_TABLE, 0); }
		public TerminalNode Q_CREATE_VIEW() { return getToken(QDSCommandParser.Q_CREATE_VIEW, 0); }
		public TerminalNode Q_CREATE_TEMP_TABLE() { return getToken(QDSCommandParser.Q_CREATE_TEMP_TABLE, 0); }
		public TerminalNode Q_CREATE_DATABASE() { return getToken(QDSCommandParser.Q_CREATE_DATABASE, 0); }
		public Sql_stmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sql_stmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof QDSCommandListener ) ((QDSCommandListener)listener).enterSql_stmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof QDSCommandListener ) ((QDSCommandListener)listener).exitSql_stmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof QDSCommandVisitor ) return ((QDSCommandVisitor<? extends T>)visitor).visitSql_stmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Sql_stmtContext sql_stmt() throws RecognitionException {
		Sql_stmtContext _localctx = new Sql_stmtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_sql_stmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(13);
			((Sql_stmtContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Q_SET) | (1L << Q_ADD_JAR) | (1L << Q_USE) | (1L << Q_CREATE_FUNCTION) | (1L << Q_INSERT_INTO) | (1L << Q_INSERT_OVERWRITE) | (1L << Q_SELECT) | (1L << Q_DROP_TABLE) | (1L << Q_DROP_VIEW) | (1L << Q_ALTER_TABLE) | (1L << Q_CREATE_TABLE) | (1L << Q_CREATE_DATABASE) | (1L << Q_CREATE_EXTERNAL_TABLE) | (1L << Q_CREATE_TEMP_TABLE) | (1L << Q_CREATE_VIEW) | (1L << Q_CTE))) != 0)) ) {
				((Sql_stmtContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\27\22\4\2\t\2\4\3"+
		"\t\3\3\2\7\2\b\n\2\f\2\16\2\13\13\2\3\2\3\2\3\2\3\3\3\3\3\3\2\2\4\2\4"+
		"\2\3\3\2\3\22\2\20\2\t\3\2\2\2\4\17\3\2\2\2\6\b\7\27\2\2\7\6\3\2\2\2\b"+
		"\13\3\2\2\2\t\7\3\2\2\2\t\n\3\2\2\2\n\f\3\2\2\2\13\t\3\2\2\2\f\r\5\4\3"+
		"\2\r\16\7\2\2\3\16\3\3\2\2\2\17\20\t\2\2\2\20\5\3\2\2\2\3\t";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}