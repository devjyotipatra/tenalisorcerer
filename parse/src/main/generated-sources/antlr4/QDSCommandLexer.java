// Generated from antlr4/QDSCommand.g4 by ANTLR 4.7
package antlr4;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class QDSCommandLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Q_SET=1, Q_ADD_JAR=2, Q_USE=3, Q_CREATE_FUNCTION=4, Q_INSERT_INTO=5, Q_INSERT_OVERWRITE=6, 
		Q_SELECT=7, Q_DROP_TABLE=8, Q_DROP_VIEW=9, Q_ALTER_TABLE=10, Q_CREATE_TABLE=11, 
		Q_CREATE_EXTERNAL_TABLE=12, Q_CREATE_VIEW=13, Q_CTE=14, TEXT=15, Q_SEMI=16, 
		SIMPLE_COMMENT=17, BRACKETED_COMMENT=18, SPACES=19;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"Q_SET", "Q_ADD_JAR", "Q_USE", "Q_CREATE_FUNCTION", "Q_INSERT_INTO", "Q_INSERT_OVERWRITE", 
		"Q_SELECT", "Q_DROP_TABLE", "Q_DROP_VIEW", "Q_ALTER_TABLE", "Q_CREATE_TABLE", 
		"Q_CREATE_EXTERNAL_TABLE", "Q_CREATE_VIEW", "Q_CTE", "TEXT", "Q_SEMI", 
		"SIMPLE_COMMENT", "BRACKETED_COMMENT", "SPACES", "A", "B", "C", "D", "E", 
		"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", 
		"T", "U", "V", "W", "X", "Y", "Z"
	};

	private static final String[] _LITERAL_NAMES = {
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "Q_SET", "Q_ADD_JAR", "Q_USE", "Q_CREATE_FUNCTION", "Q_INSERT_INTO", 
		"Q_INSERT_OVERWRITE", "Q_SELECT", "Q_DROP_TABLE", "Q_DROP_VIEW", "Q_ALTER_TABLE", 
		"Q_CREATE_TABLE", "Q_CREATE_EXTERNAL_TABLE", "Q_CREATE_VIEW", "Q_CTE", 
		"TEXT", "Q_SEMI", "SIMPLE_COMMENT", "BRACKETED_COMMENT", "SPACES"
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


	public QDSCommandLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "QDSCommand.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\25\u01a0\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\3\2\3\2\3\2\3\2\3\2\6\2c\n\2\r\2\16\2d\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\6\3p\n\3\r\3\16\3q\3\4\3\4\3\4\3\4\3\4\6\4y\n"+
		"\4\r\4\16\4z\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\6\5\u008f\n\5\r\5\16\5\u0090\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\6\6\u00a0\n\6\r\6\16\6\u00a1\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\6\7\u00b6\n"+
		"\7\r\7\16\7\u00b7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\6\b\u00c2\n\b\r\b\16"+
		"\b\u00c3\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\6\t\u00d2\n\t"+
		"\r\t\16\t\u00d3\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\6\n\u00e1"+
		"\n\n\r\n\16\n\u00e2\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\6\13\u00f1\n\13\r\13\16\13\u00f2\3\13\6\13\u00f6\n\13\r\13"+
		"\16\13\u00f7\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\6"+
		"\f\u0108\n\f\r\f\16\f\u0109\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\6\r\u0123\n\r\r\r\16"+
		"\r\u0124\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\6\16\u0134\n\16\r\16\16\16\u0135\3\17\3\17\3\17\3\17\3\17\3\17\6\17"+
		"\u013e\n\17\r\17\16\17\u013f\3\20\3\20\3\21\6\21\u0145\n\21\r\21\16\21"+
		"\u0146\3\22\3\22\3\22\3\22\7\22\u014d\n\22\f\22\16\22\u0150\13\22\3\22"+
		"\5\22\u0153\n\22\3\22\5\22\u0156\n\22\3\22\3\22\3\23\3\23\3\23\3\23\7"+
		"\23\u015e\n\23\f\23\16\23\u0161\13\23\3\23\3\23\3\23\3\23\3\23\3\24\6"+
		"\24\u0169\n\24\r\24\16\24\u016a\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30"+
		"\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37"+
		"\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3"+
		"*\3+\3+\3,\3,\3-\3-\3.\3.\3\u015f\2/\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\2+\2-\2/"+
		"\2\61\2\63\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2O\2Q\2S\2U\2W\2"+
		"Y\2[\2\3\2 \3\2==\4\2\"\"==\4\2\f\f\17\17\5\2\13\r\17\17\"\"\4\2CCcc\4"+
		"\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLl"+
		"l\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2"+
		"UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u019a"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\3]\3\2\2\2\5f\3\2\2\2\7s\3\2\2\2\t|\3\2\2"+
		"\2\13\u0092\3\2\2\2\r\u00a3\3\2\2\2\17\u00b9\3\2\2\2\21\u00c5\3\2\2\2"+
		"\23\u00d5\3\2\2\2\25\u00e4\3\2\2\2\27\u00f9\3\2\2\2\31\u010b\3\2\2\2\33"+
		"\u0126\3\2\2\2\35\u0137\3\2\2\2\37\u0141\3\2\2\2!\u0144\3\2\2\2#\u0148"+
		"\3\2\2\2%\u0159\3\2\2\2\'\u0168\3\2\2\2)\u016c\3\2\2\2+\u016e\3\2\2\2"+
		"-\u0170\3\2\2\2/\u0172\3\2\2\2\61\u0174\3\2\2\2\63\u0176\3\2\2\2\65\u0178"+
		"\3\2\2\2\67\u017a\3\2\2\29\u017c\3\2\2\2;\u017e\3\2\2\2=\u0180\3\2\2\2"+
		"?\u0182\3\2\2\2A\u0184\3\2\2\2C\u0186\3\2\2\2E\u0188\3\2\2\2G\u018a\3"+
		"\2\2\2I\u018c\3\2\2\2K\u018e\3\2\2\2M\u0190\3\2\2\2O\u0192\3\2\2\2Q\u0194"+
		"\3\2\2\2S\u0196\3\2\2\2U\u0198\3\2\2\2W\u019a\3\2\2\2Y\u019c\3\2\2\2["+
		"\u019e\3\2\2\2]^\5M\'\2^_\5\61\31\2_`\5O(\2`b\5\'\24\2ac\5\37\20\2ba\3"+
		"\2\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2\2e\4\3\2\2\2fg\5)\25\2gh\5/\30\2hi"+
		"\5/\30\2ij\5\'\24\2jk\5;\36\2kl\5)\25\2lm\5K&\2mo\5\'\24\2np\5\37\20\2"+
		"on\3\2\2\2pq\3\2\2\2qo\3\2\2\2qr\3\2\2\2r\6\3\2\2\2st\5Q)\2tu\5M\'\2u"+
		"v\5\61\31\2vx\5\'\24\2wy\5\37\20\2xw\3\2\2\2yz\3\2\2\2zx\3\2\2\2z{\3\2"+
		"\2\2{\b\3\2\2\2|}\5-\27\2}~\5K&\2~\177\5\61\31\2\177\u0080\5)\25\2\u0080"+
		"\u0081\5O(\2\u0081\u0082\5\61\31\2\u0082\u0083\5\'\24\2\u0083\u0084\5"+
		"O(\2\u0084\u0085\5\61\31\2\u0085\u0086\5A!\2\u0086\u0087\5G$\2\u0087\u0088"+
		"\5E#\2\u0088\u0089\5K&\2\u0089\u008a\5)\25\2\u008a\u008b\5K&\2\u008b\u008c"+
		"\5Y-\2\u008c\u008e\5\'\24\2\u008d\u008f\5\37\20\2\u008e\u008d\3\2\2\2"+
		"\u008f\u0090\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\n\3"+
		"\2\2\2\u0092\u0093\59\35\2\u0093\u0094\5C\"\2\u0094\u0095\5M\'\2\u0095"+
		"\u0096\5\61\31\2\u0096\u0097\5K&\2\u0097\u0098\5O(\2\u0098\u0099\5\'\24"+
		"\2\u0099\u009a\59\35\2\u009a\u009b\5C\"\2\u009b\u009c\5O(\2\u009c\u009d"+
		"\5E#\2\u009d\u009f\5\'\24\2\u009e\u00a0\5\37\20\2\u009f\u009e\3\2\2\2"+
		"\u00a0\u00a1\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\f\3"+
		"\2\2\2\u00a3\u00a4\59\35\2\u00a4\u00a5\5C\"\2\u00a5\u00a6\5M\'\2\u00a6"+
		"\u00a7\5\61\31\2\u00a7\u00a8\5K&\2\u00a8\u00a9\5O(\2\u00a9\u00aa\5\'\24"+
		"\2\u00aa\u00ab\5E#\2\u00ab\u00ac\5S*\2\u00ac\u00ad\5\61\31\2\u00ad\u00ae"+
		"\5K&\2\u00ae\u00af\5U+\2\u00af\u00b0\5K&\2\u00b0\u00b1\59\35\2\u00b1\u00b2"+
		"\5O(\2\u00b2\u00b3\5\61\31\2\u00b3\u00b5\5\'\24\2\u00b4\u00b6\5\37\20"+
		"\2\u00b5\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8"+
		"\3\2\2\2\u00b8\16\3\2\2\2\u00b9\u00ba\5M\'\2\u00ba\u00bb\5\61\31\2\u00bb"+
		"\u00bc\5? \2\u00bc\u00bd\5\61\31\2\u00bd\u00be\5-\27\2\u00be\u00bf\5O"+
		"(\2\u00bf\u00c1\5\'\24\2\u00c0\u00c2\5\37\20\2\u00c1\u00c0\3\2\2\2\u00c2"+
		"\u00c3\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\20\3\2\2"+
		"\2\u00c5\u00c6\5/\30\2\u00c6\u00c7\5K&\2\u00c7\u00c8\5E#\2\u00c8\u00c9"+
		"\5G$\2\u00c9\u00ca\5\'\24\2\u00ca\u00cb\5O(\2\u00cb\u00cc\5)\25\2\u00cc"+
		"\u00cd\5+\26\2\u00cd\u00ce\5? \2\u00ce\u00cf\5\61\31\2\u00cf\u00d1\5\'"+
		"\24\2\u00d0\u00d2\5\37\20\2\u00d1\u00d0\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3"+
		"\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\22\3\2\2\2\u00d5\u00d6\5/\30"+
		"\2\u00d6\u00d7\5K&\2\u00d7\u00d8\5E#\2\u00d8\u00d9\5G$\2\u00d9\u00da\5"+
		"\'\24\2\u00da\u00db\5S*\2\u00db\u00dc\59\35\2\u00dc\u00dd\5\61\31\2\u00dd"+
		"\u00de\5U+\2\u00de\u00e0\5\'\24\2\u00df\u00e1\5\37\20\2\u00e0\u00df\3"+
		"\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3"+
		"\24\3\2\2\2\u00e4\u00e5\5)\25\2\u00e5\u00e6\5? \2\u00e6\u00e7\5O(\2\u00e7"+
		"\u00e8\5\61\31\2\u00e8\u00e9\5K&\2\u00e9\u00ea\5\'\24\2\u00ea\u00eb\5"+
		"O(\2\u00eb\u00ec\5)\25\2\u00ec\u00ed\5+\26\2\u00ed\u00ee\5? \2\u00ee\u00f0"+
		"\5\61\31\2\u00ef\u00f1\5\'\24\2\u00f0\u00ef\3\2\2\2\u00f1\u00f2\3\2\2"+
		"\2\u00f2\u00f0\3\2\2\2\u00f2\u00f3\3\2\2\2\u00f3\u00f5\3\2\2\2\u00f4\u00f6"+
		"\5\37\20\2\u00f5\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f5\3\2\2\2"+
		"\u00f7\u00f8\3\2\2\2\u00f8\26\3\2\2\2\u00f9\u00fa\5-\27\2\u00fa\u00fb"+
		"\5K&\2\u00fb\u00fc\5\61\31\2\u00fc\u00fd\5)\25\2\u00fd\u00fe\5O(\2\u00fe"+
		"\u00ff\5\61\31\2\u00ff\u0100\5\'\24\2\u0100\u0101\5O(\2\u0101\u0102\5"+
		")\25\2\u0102\u0103\5+\26\2\u0103\u0104\5? \2\u0104\u0105\5\61\31\2\u0105"+
		"\u0107\5\'\24\2\u0106\u0108\5\37\20\2\u0107\u0106\3\2\2\2\u0108\u0109"+
		"\3\2\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a\30\3\2\2\2\u010b"+
		"\u010c\5-\27\2\u010c\u010d\5K&\2\u010d\u010e\5\61\31\2\u010e\u010f\5)"+
		"\25\2\u010f\u0110\5O(\2\u0110\u0111\5\61\31\2\u0111\u0112\5\'\24\2\u0112"+
		"\u0113\5\61\31\2\u0113\u0114\5W,\2\u0114\u0115\5O(\2\u0115\u0116\5\61"+
		"\31\2\u0116\u0117\5K&\2\u0117\u0118\5C\"\2\u0118\u0119\5)\25\2\u0119\u011a"+
		"\5? \2\u011a\u011b\5\'\24\2\u011b\u011c\5O(\2\u011c\u011d\5)\25\2\u011d"+
		"\u011e\5+\26\2\u011e\u011f\5? \2\u011f\u0120\5\61\31\2\u0120\u0122\5\'"+
		"\24\2\u0121\u0123\5\37\20\2\u0122\u0121\3\2\2\2\u0123\u0124\3\2\2\2\u0124"+
		"\u0122\3\2\2\2\u0124\u0125\3\2\2\2\u0125\32\3\2\2\2\u0126\u0127\5-\27"+
		"\2\u0127\u0128\5K&\2\u0128\u0129\5\61\31\2\u0129\u012a\5)\25\2\u012a\u012b"+
		"\5O(\2\u012b\u012c\5\61\31\2\u012c\u012d\5\'\24\2\u012d\u012e\5S*\2\u012e"+
		"\u012f\59\35\2\u012f\u0130\5\61\31\2\u0130\u0131\5U+\2\u0131\u0133\5\'"+
		"\24\2\u0132\u0134\5\37\20\2\u0133\u0132\3\2\2\2\u0134\u0135\3\2\2\2\u0135"+
		"\u0133\3\2\2\2\u0135\u0136\3\2\2\2\u0136\34\3\2\2\2\u0137\u0138\5U+\2"+
		"\u0138\u0139\59\35\2\u0139\u013a\5O(\2\u013a\u013b\5\67\34\2\u013b\u013d"+
		"\5\'\24\2\u013c\u013e\5\37\20\2\u013d\u013c\3\2\2\2\u013e\u013f\3\2\2"+
		"\2\u013f\u013d\3\2\2\2\u013f\u0140\3\2\2\2\u0140\36\3\2\2\2\u0141\u0142"+
		"\n\2\2\2\u0142 \3\2\2\2\u0143\u0145\t\3\2\2\u0144\u0143\3\2\2\2\u0145"+
		"\u0146\3\2\2\2\u0146\u0144\3\2\2\2\u0146\u0147\3\2\2\2\u0147\"\3\2\2\2"+
		"\u0148\u0149\7/\2\2\u0149\u014a\7/\2\2\u014a\u014e\3\2\2\2\u014b\u014d"+
		"\n\4\2\2\u014c\u014b\3\2\2\2\u014d\u0150\3\2\2\2\u014e\u014c\3\2\2\2\u014e"+
		"\u014f\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2\2\2\u0151\u0153\7\17"+
		"\2\2\u0152\u0151\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0155\3\2\2\2\u0154"+
		"\u0156\7\f\2\2\u0155\u0154\3\2\2\2\u0155\u0156\3\2\2\2\u0156\u0157\3\2"+
		"\2\2\u0157\u0158\b\22\2\2\u0158$\3\2\2\2\u0159\u015a\7\61\2\2\u015a\u015b"+
		"\7,\2\2\u015b\u015f\3\2\2\2\u015c\u015e\13\2\2\2\u015d\u015c\3\2\2\2\u015e"+
		"\u0161\3\2\2\2\u015f\u0160\3\2\2\2\u015f\u015d\3\2\2\2\u0160\u0162\3\2"+
		"\2\2\u0161\u015f\3\2\2\2\u0162\u0163\7,\2\2\u0163\u0164\7\61\2\2\u0164"+
		"\u0165\3\2\2\2\u0165\u0166\b\23\2\2\u0166&\3\2\2\2\u0167\u0169\t\5\2\2"+
		"\u0168\u0167\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u0168\3\2\2\2\u016a\u016b"+
		"\3\2\2\2\u016b(\3\2\2\2\u016c\u016d\t\6\2\2\u016d*\3\2\2\2\u016e\u016f"+
		"\t\7\2\2\u016f,\3\2\2\2\u0170\u0171\t\b\2\2\u0171.\3\2\2\2\u0172\u0173"+
		"\t\t\2\2\u0173\60\3\2\2\2\u0174\u0175\t\n\2\2\u0175\62\3\2\2\2\u0176\u0177"+
		"\t\13\2\2\u0177\64\3\2\2\2\u0178\u0179\t\f\2\2\u0179\66\3\2\2\2\u017a"+
		"\u017b\t\r\2\2\u017b8\3\2\2\2\u017c\u017d\t\16\2\2\u017d:\3\2\2\2\u017e"+
		"\u017f\t\17\2\2\u017f<\3\2\2\2\u0180\u0181\t\20\2\2\u0181>\3\2\2\2\u0182"+
		"\u0183\t\21\2\2\u0183@\3\2\2\2\u0184\u0185\t\22\2\2\u0185B\3\2\2\2\u0186"+
		"\u0187\t\23\2\2\u0187D\3\2\2\2\u0188\u0189\t\24\2\2\u0189F\3\2\2\2\u018a"+
		"\u018b\t\25\2\2\u018bH\3\2\2\2\u018c\u018d\t\26\2\2\u018dJ\3\2\2\2\u018e"+
		"\u018f\t\27\2\2\u018fL\3\2\2\2\u0190\u0191\t\30\2\2\u0191N\3\2\2\2\u0192"+
		"\u0193\t\31\2\2\u0193P\3\2\2\2\u0194\u0195\t\32\2\2\u0195R\3\2\2\2\u0196"+
		"\u0197\t\33\2\2\u0197T\3\2\2\2\u0198\u0199\t\34\2\2\u0199V\3\2\2\2\u019a"+
		"\u019b\t\35\2\2\u019bX\3\2\2\2\u019c\u019d\t\36\2\2\u019dZ\3\2\2\2\u019e"+
		"\u019f\t\37\2\2\u019f\\\3\2\2\2\30\2dqz\u0090\u00a1\u00b7\u00c3\u00d3"+
		"\u00e2\u00f2\u00f7\u0109\u0124\u0135\u013f\u0146\u014e\u0152\u0155\u015f"+
		"\u016a\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}