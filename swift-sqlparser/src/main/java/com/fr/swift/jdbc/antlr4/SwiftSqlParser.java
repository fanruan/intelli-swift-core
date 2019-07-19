// Generated from D:\workspace\swift-public/target/classes/SwiftSqlParser.g4 by ANTLR 4.5.3
package com.fr.swift.jdbc.antlr4;

import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SwiftSqlParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            SELECT = 1, DISTINCT = 2, AS = 3, FROM = 4, WHERE = 5, GROUP = 6, BY = 7, HAVING = 8,
            ORDER = 9, ASC = 10, DESC = 11, LIMIT = 12, INSERT = 13, INTO = 14, VALUES = 15, DELETE = 16,
            TRUNCATE = 17, CREATE = 18, TABLE = 19, NULL = 20, PARTITION = 21, BIT = 22, TINYINT = 23,
            SMALLINT = 24, INTEGER = 25, BIGINT = 26, FLOAT = 27, REAL = 28, DOUBLE = 29, NUMERIC = 30,
            DECIMAL = 31, CHAR = 32, VARCHAR = 33, LONGVARCHAR = 34, DATE = 35, TIME = 36, TIMESTAMP = 37,
            BOOLEAN = 38, DROP = 39, ALTER = 40, ADD = 41, COLUMN = 42, MAX = 43, MIN = 44, SUM = 45,
            AVG = 46, COUNT = 47, MID = 48, TODATE = 49, NOT = 50, IN = 51, BETWEEN = 52, AND = 53,
            OR = 54, LIKE = 55, IS = 56, LINE = 57, HASH = 58, RANGE = 59, MUL = 60, DIV = 61, MOD = 62,
            PLUS = 63, MINUS = 64, EQ = 65, GREATER = 66, LESS = 67, GEQ = 68, LEQ = 69, NEQ = 70,
            EXCLAMATION = 71, BIT_NOT = 72, BIT_OR = 73, BIT_AND = 74, BIT_XOR = 75, DOT = 76,
            L_PAR = 77, R_PAR = 78, COMMA = 79, SEMI = 80, AT = 81, SINGLE_QUOTE = 82, DOUBLE_QUOTE = 83,
            REVERSE_QUOTE = 84, COLON = 85, IDENTIFIER = 86, NUMERIC_LITERAL = 87, STRING_LITERAL = 88,
            DIGIT = 89, WS = 90;
    public static final int
            RULE_root = 0, RULE_sqls = 1, RULE_sql = 2, RULE_ddl = 3, RULE_dml = 4,
            RULE_createTable = 5, RULE_columnDefinitions = 6, RULE_columnDefinition = 7,
            RULE_dataType = 8, RULE_dropTable = 9, RULE_alterTable = 10, RULE_alterTableAddColumn = 11,
            RULE_alterTableDropColumn = 12, RULE_insert = 13, RULE_delete = 14, RULE_truncate = 15,
            RULE_select = 16, RULE_columns = 17, RULE_orderBy = 18, RULE_expr = 19,
            RULE_simpleExpr = 20, RULE_funcExpr = 21, RULE_funcName = 22, RULE_keywordBoolExpr = 23,
            RULE_boolExpr = 24, RULE_op = 25, RULE_boolOp = 26, RULE_logicOp = 27,
            RULE_name = 28, RULE_names = 29, RULE_value = 30, RULE_values = 31;
    public static final String[] ruleNames = {
            "root", "sqls", "sql", "ddl", "dml", "createTable", "columnDefinitions",
            "columnDefinition", "dataType", "dropTable", "alterTable", "alterTableAddColumn",
            "alterTableDropColumn", "insert", "delete", "truncate", "select", "columns",
            "orderBy", "expr", "simpleExpr", "funcExpr", "funcName", "keywordBoolExpr",
            "boolExpr", "op", "boolOp", "logicOp", "name", "names", "value", "values"
    };

    private static final String[] _LITERAL_NAMES = {
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            "'*'", "'/'", "'%'", "'+'", "'-'", "'='", "'>'", "'<'", "'>='", "'<='",
            "'!='", "'!'", "'~'", "'|'", "'&'", "'^'", "'.'", "'('", "')'", "','",
            "';'", "'@'", "'''", "'\"'", "'`'", "':'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, "SELECT", "DISTINCT", "AS", "FROM", "WHERE", "GROUP", "BY", "HAVING",
            "ORDER", "ASC", "DESC", "LIMIT", "INSERT", "INTO", "VALUES", "DELETE",
            "TRUNCATE", "CREATE", "TABLE", "NULL", "PARTITION", "BIT", "TINYINT",
            "SMALLINT", "INTEGER", "BIGINT", "FLOAT", "REAL", "DOUBLE", "NUMERIC",
            "DECIMAL", "CHAR", "VARCHAR", "LONGVARCHAR", "DATE", "TIME", "TIMESTAMP",
            "BOOLEAN", "DROP", "ALTER", "ADD", "COLUMN", "MAX", "MIN", "SUM", "AVG",
            "COUNT", "MID", "TODATE", "NOT", "IN", "BETWEEN", "AND", "OR", "LIKE",
            "IS", "LINE", "HASH", "RANGE", "MUL", "DIV", "MOD", "PLUS", "MINUS", "EQ",
            "GREATER", "LESS", "GEQ", "LEQ", "NEQ", "EXCLAMATION", "BIT_NOT", "BIT_OR",
            "BIT_AND", "BIT_XOR", "DOT", "L_PAR", "R_PAR", "COMMA", "SEMI", "AT",
            "SINGLE_QUOTE", "DOUBLE_QUOTE", "REVERSE_QUOTE", "COLON", "IDENTIFIER",
            "NUMERIC_LITERAL", "STRING_LITERAL", "DIGIT", "WS"
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
    public String getGrammarFileName() {
        return "SwiftSqlParser.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public SwiftSqlParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class RootContext extends ParserRuleContext {
        public SqlsContext sqls() {
            return getRuleContext(SqlsContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(SwiftSqlParser.EOF, 0);
        }

        public RootContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_root;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterRoot(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitRoot(this);
        }
    }

    public final RootContext root() throws RecognitionException {
        RootContext _localctx = new RootContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_root);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                sqls();
                setState(65);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class SqlsContext extends ParserRuleContext {
        public List<SqlContext> sql() {
            return getRuleContexts(SqlContext.class);
        }

        public SqlContext sql(int i) {
            return getRuleContext(SqlContext.class, i);
        }

        public List<TerminalNode> SEMI() {
            return getTokens(SwiftSqlParser.SEMI);
        }

        public TerminalNode SEMI(int i) {
            return getToken(SwiftSqlParser.SEMI, i);
        }

        public SqlsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sqls;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterSqls(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitSqls(this);
        }
    }

    public final SqlsContext sqls() throws RecognitionException {
        SqlsContext _localctx = new SqlsContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_sqls);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(67);
                sql();
                setState(72);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1 + 1) {
                        {
                            {
                                setState(68);
                                match(SEMI);
                                setState(69);
                                sql();
                            }
                        }
                    }
                    setState(74);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                }
                setState(76);
                _la = _input.LA(1);
                if (_la == SEMI) {
                    {
                        setState(75);
                        match(SEMI);
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class SqlContext extends ParserRuleContext {
        public DdlContext ddl() {
            return getRuleContext(DdlContext.class, 0);
        }

        public DmlContext dml() {
            return getRuleContext(DmlContext.class, 0);
        }

        public SqlContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sql;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterSql(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitSql(this);
        }
    }

    public final SqlContext sql() throws RecognitionException {
        SqlContext _localctx = new SqlContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_sql);
        try {
            setState(80);
            switch (_input.LA(1)) {
                case CREATE:
                case DROP:
                case ALTER:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(78);
                    ddl();
                }
                break;
                case SELECT:
                case INSERT:
                case DELETE:
                case TRUNCATE:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(79);
                    dml();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DdlContext extends ParserRuleContext {
        public CreateTableContext createTable() {
            return getRuleContext(CreateTableContext.class, 0);
        }

        public DropTableContext dropTable() {
            return getRuleContext(DropTableContext.class, 0);
        }

        public AlterTableContext alterTable() {
            return getRuleContext(AlterTableContext.class, 0);
        }

        public DdlContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_ddl;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterDdl(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitDdl(this);
        }
    }

    public final DdlContext ddl() throws RecognitionException {
        DdlContext _localctx = new DdlContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_ddl);
        try {
            setState(85);
            switch (_input.LA(1)) {
                case CREATE:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(82);
                    createTable();
                }
                break;
                case DROP:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(83);
                    dropTable();
                }
                break;
                case ALTER:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(84);
                    alterTable();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DmlContext extends ParserRuleContext {
        public InsertContext insert() {
            return getRuleContext(InsertContext.class, 0);
        }

        public DeleteContext delete() {
            return getRuleContext(DeleteContext.class, 0);
        }

        public SelectContext select() {
            return getRuleContext(SelectContext.class, 0);
        }

        public TruncateContext truncate() {
            return getRuleContext(TruncateContext.class, 0);
        }

        public DmlContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dml;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterDml(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitDml(this);
        }
    }

    public final DmlContext dml() throws RecognitionException {
        DmlContext _localctx = new DmlContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_dml);
        try {
            setState(91);
            switch (_input.LA(1)) {
                case INSERT:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(87);
                    insert();
                }
                break;
                case DELETE:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(88);
                    delete();
                }
                break;
                case SELECT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(89);
                    select();
                }
                break;
                case TRUNCATE:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(90);
                    truncate();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class CreateTableContext extends ParserRuleContext {
        public NameContext table;

        public TerminalNode CREATE() {
            return getToken(SwiftSqlParser.CREATE, 0);
        }

        public TerminalNode TABLE() {
            return getToken(SwiftSqlParser.TABLE, 0);
        }

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public ColumnDefinitionsContext columnDefinitions() {
            return getRuleContext(ColumnDefinitionsContext.class, 0);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CreateTableContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_createTable;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterCreateTable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitCreateTable(this);
        }
    }

    public final CreateTableContext createTable() throws RecognitionException {
        CreateTableContext _localctx = new CreateTableContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_createTable);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(93);
                match(CREATE);
                setState(94);
                match(TABLE);
                setState(95);
                ((CreateTableContext) _localctx).table = name();
                setState(96);
                match(L_PAR);
                setState(97);
                columnDefinitions();
                setState(98);
                match(R_PAR);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ColumnDefinitionsContext extends ParserRuleContext {
        public List<NameContext> name() {
            return getRuleContexts(NameContext.class);
        }

        public NameContext name(int i) {
            return getRuleContext(NameContext.class, i);
        }

        public List<ColumnDefinitionContext> columnDefinition() {
            return getRuleContexts(ColumnDefinitionContext.class);
        }

        public ColumnDefinitionContext columnDefinition(int i) {
            return getRuleContext(ColumnDefinitionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public ColumnDefinitionsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_columnDefinitions;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).enterColumnDefinitions(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).exitColumnDefinitions(this);
        }
    }

    public final ColumnDefinitionsContext columnDefinitions() throws RecognitionException {
        ColumnDefinitionsContext _localctx = new ColumnDefinitionsContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_columnDefinitions);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(100);
                name();
                setState(101);
                columnDefinition();
                setState(108);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(102);
                            match(COMMA);
                            setState(103);
                            name();
                            setState(104);
                            columnDefinition();
                        }
                    }
                    setState(110);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ColumnDefinitionContext extends ParserRuleContext {
        public Token length;

        public DataTypeContext dataType() {
            return getRuleContext(DataTypeContext.class, 0);
        }

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
        }

        public TerminalNode NUMERIC_LITERAL() {
            return getToken(SwiftSqlParser.NUMERIC_LITERAL, 0);
        }

        public ColumnDefinitionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_columnDefinition;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).enterColumnDefinition(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).exitColumnDefinition(this);
        }
    }

    public final ColumnDefinitionContext columnDefinition() throws RecognitionException {
        ColumnDefinitionContext _localctx = new ColumnDefinitionContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_columnDefinition);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(111);
                dataType();
                setState(115);
                _la = _input.LA(1);
                if (_la == L_PAR) {
                    {
                        setState(112);
                        match(L_PAR);
                        setState(113);
                        ((ColumnDefinitionContext) _localctx).length = match(NUMERIC_LITERAL);
                        setState(114);
                        match(R_PAR);
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DataTypeContext extends ParserRuleContext {
        public TerminalNode BIT() {
            return getToken(SwiftSqlParser.BIT, 0);
        }

        public TerminalNode TINYINT() {
            return getToken(SwiftSqlParser.TINYINT, 0);
        }

        public TerminalNode SMALLINT() {
            return getToken(SwiftSqlParser.SMALLINT, 0);
        }

        public TerminalNode INTEGER() {
            return getToken(SwiftSqlParser.INTEGER, 0);
        }

        public TerminalNode BIGINT() {
            return getToken(SwiftSqlParser.BIGINT, 0);
        }

        public TerminalNode FLOAT() {
            return getToken(SwiftSqlParser.FLOAT, 0);
        }

        public TerminalNode REAL() {
            return getToken(SwiftSqlParser.REAL, 0);
        }

        public TerminalNode DOUBLE() {
            return getToken(SwiftSqlParser.DOUBLE, 0);
        }

        public TerminalNode NUMERIC() {
            return getToken(SwiftSqlParser.NUMERIC, 0);
        }

        public TerminalNode DECIMAL() {
            return getToken(SwiftSqlParser.DECIMAL, 0);
        }

        public TerminalNode CHAR() {
            return getToken(SwiftSqlParser.CHAR, 0);
        }

        public TerminalNode VARCHAR() {
            return getToken(SwiftSqlParser.VARCHAR, 0);
        }

        public TerminalNode LONGVARCHAR() {
            return getToken(SwiftSqlParser.LONGVARCHAR, 0);
        }

        public TerminalNode DATE() {
            return getToken(SwiftSqlParser.DATE, 0);
        }

        public TerminalNode TIME() {
            return getToken(SwiftSqlParser.TIME, 0);
        }

        public TerminalNode TIMESTAMP() {
            return getToken(SwiftSqlParser.TIMESTAMP, 0);
        }

        public TerminalNode BOOLEAN() {
            return getToken(SwiftSqlParser.BOOLEAN, 0);
        }

        public DataTypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dataType;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterDataType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitDataType(this);
        }
    }

    public final DataTypeContext dataType() throws RecognitionException {
        DataTypeContext _localctx = new DataTypeContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_dataType);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(117);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BIT) | (1L << TINYINT) | (1L << SMALLINT) | (1L << INTEGER) | (1L << BIGINT) | (1L << FLOAT) | (1L << REAL) | (1L << DOUBLE) | (1L << NUMERIC) | (1L << DECIMAL) | (1L << CHAR) | (1L << VARCHAR) | (1L << LONGVARCHAR) | (1L << DATE) | (1L << TIME) | (1L << TIMESTAMP) | (1L << BOOLEAN))) != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DropTableContext extends ParserRuleContext {
        public NameContext table;

        public TerminalNode DROP() {
            return getToken(SwiftSqlParser.DROP, 0);
        }

        public TerminalNode TABLE() {
            return getToken(SwiftSqlParser.TABLE, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public DropTableContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dropTable;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterDropTable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitDropTable(this);
        }
    }

    public final DropTableContext dropTable() throws RecognitionException {
        DropTableContext _localctx = new DropTableContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_dropTable);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(119);
                match(DROP);
                setState(120);
                match(TABLE);
                setState(121);
                ((DropTableContext) _localctx).table = name();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class AlterTableContext extends ParserRuleContext {
        public AlterTableAddColumnContext alterTableAddColumn() {
            return getRuleContext(AlterTableAddColumnContext.class, 0);
        }

        public AlterTableDropColumnContext alterTableDropColumn() {
            return getRuleContext(AlterTableDropColumnContext.class, 0);
        }

        public AlterTableContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_alterTable;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterAlterTable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitAlterTable(this);
        }
    }

    public final AlterTableContext alterTable() throws RecognitionException {
        AlterTableContext _localctx = new AlterTableContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_alterTable);
        try {
            setState(125);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 7, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(123);
                    alterTableAddColumn();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(124);
                    alterTableDropColumn();
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class AlterTableAddColumnContext extends ParserRuleContext {
        public NameContext table;

        public TerminalNode ALTER() {
            return getToken(SwiftSqlParser.ALTER, 0);
        }

        public TerminalNode TABLE() {
            return getToken(SwiftSqlParser.TABLE, 0);
        }

        public TerminalNode ADD() {
            return getToken(SwiftSqlParser.ADD, 0);
        }

        public ColumnDefinitionsContext columnDefinitions() {
            return getRuleContext(ColumnDefinitionsContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public AlterTableAddColumnContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_alterTableAddColumn;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).enterAlterTableAddColumn(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).exitAlterTableAddColumn(this);
        }
    }

    public final AlterTableAddColumnContext alterTableAddColumn() throws RecognitionException {
        AlterTableAddColumnContext _localctx = new AlterTableAddColumnContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_alterTableAddColumn);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(127);
                match(ALTER);
                setState(128);
                match(TABLE);
                setState(129);
                ((AlterTableAddColumnContext) _localctx).table = name();
                setState(130);
                match(ADD);
                setState(131);
                columnDefinitions();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class AlterTableDropColumnContext extends ParserRuleContext {
        public NameContext table;
        public NamesContext columnNames;

        public TerminalNode ALTER() {
            return getToken(SwiftSqlParser.ALTER, 0);
        }

        public TerminalNode TABLE() {
            return getToken(SwiftSqlParser.TABLE, 0);
        }

        public TerminalNode DROP() {
            return getToken(SwiftSqlParser.DROP, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public NamesContext names() {
            return getRuleContext(NamesContext.class, 0);
        }

        public AlterTableDropColumnContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_alterTableDropColumn;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).enterAlterTableDropColumn(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).exitAlterTableDropColumn(this);
        }
    }

    public final AlterTableDropColumnContext alterTableDropColumn() throws RecognitionException {
        AlterTableDropColumnContext _localctx = new AlterTableDropColumnContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_alterTableDropColumn);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(133);
                match(ALTER);
                setState(134);
                match(TABLE);
                setState(135);
                ((AlterTableDropColumnContext) _localctx).table = name();
                setState(136);
                match(DROP);
                setState(137);
                ((AlterTableDropColumnContext) _localctx).columnNames = names();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class InsertContext extends ParserRuleContext {
        public NameContext table;
        public NamesContext columnNames;

        public TerminalNode INSERT() {
            return getToken(SwiftSqlParser.INSERT, 0);
        }

        public TerminalNode INTO() {
            return getToken(SwiftSqlParser.INTO, 0);
        }

        public TerminalNode VALUES() {
            return getToken(SwiftSqlParser.VALUES, 0);
        }

        public List<TerminalNode> L_PAR() {
            return getTokens(SwiftSqlParser.L_PAR);
        }

        public TerminalNode L_PAR(int i) {
            return getToken(SwiftSqlParser.L_PAR, i);
        }

        public List<ValuesContext> values() {
            return getRuleContexts(ValuesContext.class);
        }

        public ValuesContext values(int i) {
            return getRuleContext(ValuesContext.class, i);
        }

        public List<TerminalNode> R_PAR() {
            return getTokens(SwiftSqlParser.R_PAR);
        }

        public TerminalNode R_PAR(int i) {
            return getToken(SwiftSqlParser.R_PAR, i);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public NamesContext names() {
            return getRuleContext(NamesContext.class, 0);
        }

        public InsertContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_insert;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterInsert(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitInsert(this);
        }
    }

    public final InsertContext insert() throws RecognitionException {
        InsertContext _localctx = new InsertContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_insert);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(139);
                match(INSERT);
                setState(140);
                match(INTO);
                setState(141);
                ((InsertContext) _localctx).table = name();
                setState(146);
                _la = _input.LA(1);
                if (_la == L_PAR) {
                    {
                        setState(142);
                        match(L_PAR);
                        setState(143);
                        ((InsertContext) _localctx).columnNames = names();
                        setState(144);
                        match(R_PAR);
                    }
                }

                setState(148);
                match(VALUES);
                setState(149);
                match(L_PAR);
                setState(150);
                values();
                setState(151);
                match(R_PAR);
                setState(159);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1 + 1) {
                        {
                            {
                                setState(152);
                                match(COMMA);
                                setState(153);
                                match(L_PAR);
                                setState(154);
                                values();
                                setState(155);
                                match(R_PAR);
                            }
                        }
                    }
                    setState(161);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class DeleteContext extends ParserRuleContext {
        public NameContext table;
        public ExprContext where;

        public TerminalNode DELETE() {
            return getToken(SwiftSqlParser.DELETE, 0);
        }

        public TerminalNode FROM() {
            return getToken(SwiftSqlParser.FROM, 0);
        }

        public TerminalNode WHERE() {
            return getToken(SwiftSqlParser.WHERE, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public DeleteContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_delete;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterDelete(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitDelete(this);
        }
    }

    public final DeleteContext delete() throws RecognitionException {
        DeleteContext _localctx = new DeleteContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_delete);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(162);
                match(DELETE);
                setState(163);
                match(FROM);
                setState(164);
                ((DeleteContext) _localctx).table = name();
                setState(165);
                match(WHERE);
                setState(166);
                ((DeleteContext) _localctx).where = expr(0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class TruncateContext extends ParserRuleContext {
        public NameContext table;

        public TerminalNode TRUNCATE() {
            return getToken(SwiftSqlParser.TRUNCATE, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public TruncateContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_truncate;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterTruncate(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitTruncate(this);
        }
    }

    public final TruncateContext truncate() throws RecognitionException {
        TruncateContext _localctx = new TruncateContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_truncate);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(168);
                match(TRUNCATE);
                setState(169);
                ((TruncateContext) _localctx).table = name();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class SelectContext extends ParserRuleContext {
        public NameContext table;
        public SelectContext subQuery;
        public NameContext alias;
        public ExprContext where;
        public NamesContext groupBy;
        public ExprContext having;
        public Token limit;

        public TerminalNode SELECT() {
            return getToken(SwiftSqlParser.SELECT, 0);
        }

        public ColumnsContext columns() {
            return getRuleContext(ColumnsContext.class, 0);
        }

        public TerminalNode FROM() {
            return getToken(SwiftSqlParser.FROM, 0);
        }

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
        }

        public TerminalNode DISTINCT() {
            return getToken(SwiftSqlParser.DISTINCT, 0);
        }

        public List<NameContext> name() {
            return getRuleContexts(NameContext.class);
        }

        public NameContext name(int i) {
            return getRuleContext(NameContext.class, i);
        }

        public SelectContext select() {
            return getRuleContext(SelectContext.class, 0);
        }

        public TerminalNode WHERE() {
            return getToken(SwiftSqlParser.WHERE, 0);
        }

        public TerminalNode GROUP() {
            return getToken(SwiftSqlParser.GROUP, 0);
        }

        public List<TerminalNode> BY() {
            return getTokens(SwiftSqlParser.BY);
        }

        public TerminalNode BY(int i) {
            return getToken(SwiftSqlParser.BY, i);
        }

        public TerminalNode HAVING() {
            return getToken(SwiftSqlParser.HAVING, 0);
        }

        public TerminalNode ORDER() {
            return getToken(SwiftSqlParser.ORDER, 0);
        }

        public OrderByContext orderBy() {
            return getRuleContext(OrderByContext.class, 0);
        }

        public TerminalNode LIMIT() {
            return getToken(SwiftSqlParser.LIMIT, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public NamesContext names() {
            return getRuleContext(NamesContext.class, 0);
        }

        public TerminalNode NUMERIC_LITERAL() {
            return getToken(SwiftSqlParser.NUMERIC_LITERAL, 0);
        }

        public TerminalNode AS() {
            return getToken(SwiftSqlParser.AS, 0);
        }

        public SelectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_select;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterSelect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitSelect(this);
        }
    }

    public final SelectContext select() throws RecognitionException {
        SelectContext _localctx = new SelectContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_select);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(171);
                match(SELECT);
                setState(173);
                _la = _input.LA(1);
                if (_la == DISTINCT) {
                    {
                        setState(172);
                        match(DISTINCT);
                    }
                }

                setState(175);
                columns();
                setState(176);
                match(FROM);
                setState(182);
                switch (_input.LA(1)) {
                    case IDENTIFIER: {
                        setState(177);
                        ((SelectContext) _localctx).table = name();
                    }
                    break;
                    case L_PAR: {
                        setState(178);
                        match(L_PAR);
                        setState(179);
                        ((SelectContext) _localctx).subQuery = select();
                        setState(180);
                        match(R_PAR);
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(188);
                _la = _input.LA(1);
                if (_la == AS || _la == IDENTIFIER) {
                    {
                        setState(185);
                        _la = _input.LA(1);
                        if (_la == AS) {
                            {
                                setState(184);
                                match(AS);
                            }
                        }

                        setState(187);
                        ((SelectContext) _localctx).alias = name();
                    }
                }

                setState(192);
                _la = _input.LA(1);
                if (_la == WHERE) {
                    {
                        setState(190);
                        match(WHERE);
                        setState(191);
                        ((SelectContext) _localctx).where = expr(0);
                    }
                }

                setState(197);
                _la = _input.LA(1);
                if (_la == GROUP) {
                    {
                        setState(194);
                        match(GROUP);
                        setState(195);
                        match(BY);
                        setState(196);
                        ((SelectContext) _localctx).groupBy = names();
                    }
                }

                setState(201);
                _la = _input.LA(1);
                if (_la == HAVING) {
                    {
                        setState(199);
                        match(HAVING);
                        setState(200);
                        ((SelectContext) _localctx).having = expr(0);
                    }
                }

                setState(206);
                _la = _input.LA(1);
                if (_la == ORDER) {
                    {
                        setState(203);
                        match(ORDER);
                        setState(204);
                        match(BY);
                        setState(205);
                        orderBy();
                    }
                }

                setState(210);
                _la = _input.LA(1);
                if (_la == LIMIT) {
                    {
                        setState(208);
                        match(LIMIT);
                        setState(209);
                        ((SelectContext) _localctx).limit = match(NUMERIC_LITERAL);
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ColumnsContext extends ParserRuleContext {
        public NameContext alias;

        public List<SimpleExprContext> simpleExpr() {
            return getRuleContexts(SimpleExprContext.class);
        }

        public SimpleExprContext simpleExpr(int i) {
            return getRuleContext(SimpleExprContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public List<NameContext> name() {
            return getRuleContexts(NameContext.class);
        }

        public NameContext name(int i) {
            return getRuleContext(NameContext.class, i);
        }

        public List<TerminalNode> AS() {
            return getTokens(SwiftSqlParser.AS);
        }

        public TerminalNode AS(int i) {
            return getToken(SwiftSqlParser.AS, i);
        }

        public ColumnsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_columns;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterColumns(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitColumns(this);
        }
    }

    public final ColumnsContext columns() throws RecognitionException {
        ColumnsContext _localctx = new ColumnsContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_columns);
        int _la;
        try {
            int _alt;
            setState(233);
            switch (_input.LA(1)) {
                case MUL:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(212);
                    match(MUL);
                }
                break;
                case NULL:
                case MAX:
                case MIN:
                case SUM:
                case AVG:
                case COUNT:
                case MID:
                case TODATE:
                case IDENTIFIER:
                case NUMERIC_LITERAL:
                case STRING_LITERAL:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(213);
                    simpleExpr();
                    setState(218);
                    _la = _input.LA(1);
                    if (_la == AS || _la == IDENTIFIER) {
                        {
                            setState(215);
                            _la = _input.LA(1);
                            if (_la == AS) {
                                {
                                    setState(214);
                                    match(AS);
                                }
                            }

                            setState(217);
                            ((ColumnsContext) _localctx).alias = name();
                        }
                    }

                    setState(230);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 23, _ctx);
                    while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                        if (_alt == 1 + 1) {
                            {
                                {
                                    setState(220);
                                    match(COMMA);
                                    setState(221);
                                    simpleExpr();
                                    setState(226);
                                    _la = _input.LA(1);
                                    if (_la == AS || _la == IDENTIFIER) {
                                        {
                                            setState(223);
                                            _la = _input.LA(1);
                                            if (_la == AS) {
                                                {
                                                    setState(222);
                                                    match(AS);
                                                }
                                            }

                                            setState(225);
                                            ((ColumnsContext) _localctx).alias = name();
                                        }
                                    }

                                }
                            }
                        }
                        setState(232);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 23, _ctx);
                    }
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class OrderByContext extends ParserRuleContext {
        public List<NameContext> name() {
            return getRuleContexts(NameContext.class);
        }

        public NameContext name(int i) {
            return getRuleContext(NameContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public List<TerminalNode> ASC() {
            return getTokens(SwiftSqlParser.ASC);
        }

        public TerminalNode ASC(int i) {
            return getToken(SwiftSqlParser.ASC, i);
        }

        public List<TerminalNode> DESC() {
            return getTokens(SwiftSqlParser.DESC);
        }

        public TerminalNode DESC(int i) {
            return getToken(SwiftSqlParser.DESC, i);
        }

        public OrderByContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_orderBy;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterOrderBy(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitOrderBy(this);
        }
    }

    public final OrderByContext orderBy() throws RecognitionException {
        OrderByContext _localctx = new OrderByContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_orderBy);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(235);
                name();
                setState(237);
                _la = _input.LA(1);
                if (_la == ASC || _la == DESC) {
                    {
                        setState(236);
                        _la = _input.LA(1);
                        if (!(_la == ASC || _la == DESC)) {
                            _errHandler.recoverInline(this);
                        } else {
                            consume();
                        }
                    }
                }

                setState(246);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 27, _ctx);
                while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1 + 1) {
                        {
                            {
                                setState(239);
                                match(COMMA);
                                setState(240);
                                name();
                                setState(242);
                                _la = _input.LA(1);
                                if (_la == ASC || _la == DESC) {
                                    {
                                        setState(241);
                                        _la = _input.LA(1);
                                        if (!(_la == ASC || _la == DESC)) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            consume();
                                        }
                                    }
                                }

                            }
                        }
                    }
                    setState(248);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 27, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ExprContext extends ParserRuleContext {
        public List<SimpleExprContext> simpleExpr() {
            return getRuleContexts(SimpleExprContext.class);
        }

        public SimpleExprContext simpleExpr(int i) {
            return getRuleContext(SimpleExprContext.class, i);
        }

        public OpContext op() {
            return getRuleContext(OpContext.class, 0);
        }

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
        }

        public BoolExprContext boolExpr() {
            return getRuleContext(BoolExprContext.class, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public ExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitExpr(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        return expr(0);
    }

    private ExprContext expr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        ExprContext _localctx = new ExprContext(_ctx, _parentState);
        ExprContext _prevctx = _localctx;
        int _startState = 38;
        enterRecursionRule(_localctx, 38, RULE_expr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(268);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 28, _ctx)) {
                    case 1: {
                        setState(250);
                        simpleExpr();
                    }
                    break;
                    case 2: {
                        setState(251);
                        simpleExpr();
                        setState(252);
                        op();
                        setState(253);
                        simpleExpr();
                    }
                    break;
                    case 3: {
                        setState(255);
                        match(L_PAR);
                        setState(256);
                        simpleExpr();
                        setState(257);
                        op();
                        setState(258);
                        simpleExpr();
                        setState(259);
                        match(R_PAR);
                    }
                    break;
                    case 4: {
                        setState(261);
                        boolExpr(0);
                    }
                    break;
                    case 5: {
                        setState(262);
                        match(L_PAR);
                        setState(263);
                        expr(0);
                        setState(264);
                        op();
                        setState(265);
                        expr(0);
                        setState(266);
                        match(R_PAR);
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(276);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 29, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new ExprContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                setState(270);
                                if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                setState(271);
                                op();
                                setState(272);
                                expr(3);
                            }
                        }
                    }
                    setState(278);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 29, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public static class SimpleExprContext extends ParserRuleContext {
        public ValueContext value() {
            return getRuleContext(ValueContext.class, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public FuncExprContext funcExpr() {
            return getRuleContext(FuncExprContext.class, 0);
        }

        public SimpleExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_simpleExpr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterSimpleExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitSimpleExpr(this);
        }
    }

    public final SimpleExprContext simpleExpr() throws RecognitionException {
        SimpleExprContext _localctx = new SimpleExprContext(_ctx, getState());
        enterRule(_localctx, 40, RULE_simpleExpr);
        try {
            setState(282);
            switch (_input.LA(1)) {
                case NULL:
                case NUMERIC_LITERAL:
                case STRING_LITERAL:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(279);
                    value();
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(280);
                    name();
                }
                break;
                case MAX:
                case MIN:
                case SUM:
                case AVG:
                case COUNT:
                case MID:
                case TODATE:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(281);
                    funcExpr();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class FuncExprContext extends ParserRuleContext {
        public FuncNameContext funcName() {
            return getRuleContext(FuncNameContext.class, 0);
        }

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
        }

        public List<SimpleExprContext> simpleExpr() {
            return getRuleContexts(SimpleExprContext.class);
        }

        public SimpleExprContext simpleExpr(int i) {
            return getRuleContext(SimpleExprContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public FuncExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_funcExpr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterFuncExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitFuncExpr(this);
        }
    }

    public final FuncExprContext funcExpr() throws RecognitionException {
        FuncExprContext _localctx = new FuncExprContext(_ctx, getState());
        enterRule(_localctx, 42, RULE_funcExpr);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(284);
                funcName();
                setState(285);
                match(L_PAR);
                setState(294);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NULL) | (1L << MAX) | (1L << MIN) | (1L << SUM) | (1L << AVG) | (1L << COUNT) | (1L << MID) | (1L << TODATE))) != 0) || ((((_la - 86)) & ~0x3f) == 0 && ((1L << (_la - 86)) & ((1L << (IDENTIFIER - 86)) | (1L << (NUMERIC_LITERAL - 86)) | (1L << (STRING_LITERAL - 86)))) != 0)) {
                    {
                        setState(286);
                        simpleExpr();
                        setState(291);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 31, _ctx);
                        while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                            if (_alt == 1 + 1) {
                                {
                                    {
                                        setState(287);
                                        match(COMMA);
                                        setState(288);
                                        simpleExpr();
                                    }
                                }
                            }
                            setState(293);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 31, _ctx);
                        }
                    }
                }

                setState(296);
                match(R_PAR);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class FuncNameContext extends ParserRuleContext {
        public TerminalNode MAX() {
            return getToken(SwiftSqlParser.MAX, 0);
        }

        public TerminalNode MIN() {
            return getToken(SwiftSqlParser.MIN, 0);
        }

        public TerminalNode SUM() {
            return getToken(SwiftSqlParser.SUM, 0);
        }

        public TerminalNode AVG() {
            return getToken(SwiftSqlParser.AVG, 0);
        }

        public TerminalNode COUNT() {
            return getToken(SwiftSqlParser.COUNT, 0);
        }

        public TerminalNode MID() {
            return getToken(SwiftSqlParser.MID, 0);
        }

        public TerminalNode TODATE() {
            return getToken(SwiftSqlParser.TODATE, 0);
        }

        public FuncNameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_funcName;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterFuncName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitFuncName(this);
        }
    }

    public final FuncNameContext funcName() throws RecognitionException {
        FuncNameContext _localctx = new FuncNameContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_funcName);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(298);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MAX) | (1L << MIN) | (1L << SUM) | (1L << AVG) | (1L << COUNT) | (1L << MID) | (1L << TODATE))) != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class KeywordBoolExprContext extends ParserRuleContext {
        public SimpleExprContext simpleExpr() {
            return getRuleContext(SimpleExprContext.class, 0);
        }

        public TerminalNode IN() {
            return getToken(SwiftSqlParser.IN, 0);
        }

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public ValuesContext values() {
            return getRuleContext(ValuesContext.class, 0);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
        }

        public TerminalNode NOT() {
            return getToken(SwiftSqlParser.NOT, 0);
        }

        public TerminalNode BETWEEN() {
            return getToken(SwiftSqlParser.BETWEEN, 0);
        }

        public List<TerminalNode> NUMERIC_LITERAL() {
            return getTokens(SwiftSqlParser.NUMERIC_LITERAL);
        }

        public TerminalNode NUMERIC_LITERAL(int i) {
            return getToken(SwiftSqlParser.NUMERIC_LITERAL, i);
        }

        public TerminalNode AND() {
            return getToken(SwiftSqlParser.AND, 0);
        }

        public TerminalNode IS() {
            return getToken(SwiftSqlParser.IS, 0);
        }

        public TerminalNode NULL() {
            return getToken(SwiftSqlParser.NULL, 0);
        }

        public KeywordBoolExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_keywordBoolExpr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).enterKeywordBoolExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener)
                ((SwiftSqlParserListener) listener).exitKeywordBoolExpr(this);
        }
    }

    public final KeywordBoolExprContext keywordBoolExpr() throws RecognitionException {
        KeywordBoolExprContext _localctx = new KeywordBoolExprContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_keywordBoolExpr);
        int _la;
        try {
            setState(325);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 36, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(300);
                    simpleExpr();
                    setState(302);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(301);
                            match(NOT);
                        }
                    }

                    setState(304);
                    match(IN);
                    setState(305);
                    match(L_PAR);
                    setState(306);
                    values();
                    setState(307);
                    match(R_PAR);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(309);
                    simpleExpr();
                    setState(311);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(310);
                            match(NOT);
                        }
                    }

                    setState(313);
                    match(BETWEEN);
                    setState(314);
                    match(NUMERIC_LITERAL);
                    setState(315);
                    match(AND);
                    setState(316);
                    match(NUMERIC_LITERAL);
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(318);
                    simpleExpr();
                    setState(319);
                    match(IS);
                    setState(321);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(320);
                            match(NOT);
                        }
                    }

                    setState(323);
                    match(NULL);
                }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class BoolExprContext extends ParserRuleContext {
        public List<SimpleExprContext> simpleExpr() {
            return getRuleContexts(SimpleExprContext.class);
        }

        public SimpleExprContext simpleExpr(int i) {
            return getRuleContext(SimpleExprContext.class, i);
        }

        public BoolOpContext boolOp() {
            return getRuleContext(BoolOpContext.class, 0);
        }

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
        }

        public KeywordBoolExprContext keywordBoolExpr() {
            return getRuleContext(KeywordBoolExprContext.class, 0);
        }

        public TerminalNode NOT() {
            return getToken(SwiftSqlParser.NOT, 0);
        }

        public List<BoolExprContext> boolExpr() {
            return getRuleContexts(BoolExprContext.class);
        }

        public BoolExprContext boolExpr(int i) {
            return getRuleContext(BoolExprContext.class, i);
        }

        public LogicOpContext logicOp() {
            return getRuleContext(LogicOpContext.class, 0);
        }

        public BoolExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_boolExpr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterBoolExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitBoolExpr(this);
        }
    }

    public final BoolExprContext boolExpr() throws RecognitionException {
        return boolExpr(0);
    }

    private BoolExprContext boolExpr(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        BoolExprContext _localctx = new BoolExprContext(_ctx, _parentState);
        BoolExprContext _prevctx = _localctx;
        int _startState = 48;
        enterRecursionRule(_localctx, 48, RULE_boolExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(347);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 37, _ctx)) {
                    case 1: {
                        setState(328);
                        simpleExpr();
                        setState(329);
                        boolOp();
                        setState(330);
                        simpleExpr();
                    }
                    break;
                    case 2: {
                        setState(332);
                        match(L_PAR);
                        setState(333);
                        simpleExpr();
                        setState(334);
                        boolOp();
                        setState(335);
                        simpleExpr();
                        setState(336);
                        match(R_PAR);
                    }
                    break;
                    case 3: {
                        setState(338);
                        keywordBoolExpr();
                    }
                    break;
                    case 4: {
                        setState(339);
                        match(NOT);
                        setState(340);
                        boolExpr(3);
                    }
                    break;
                    case 5: {
                        setState(341);
                        match(L_PAR);
                        setState(342);
                        boolExpr(0);
                        setState(343);
                        logicOp();
                        setState(344);
                        boolExpr(0);
                        setState(345);
                        match(R_PAR);
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(355);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 38, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new BoolExprContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
                                setState(349);
                                if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                setState(350);
                                logicOp();
                                setState(351);
                                boolExpr(3);
                            }
                        }
                    }
                    setState(357);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 38, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public static class OpContext extends ParserRuleContext {
        public TerminalNode PLUS() {
            return getToken(SwiftSqlParser.PLUS, 0);
        }

        public TerminalNode MINUS() {
            return getToken(SwiftSqlParser.MINUS, 0);
        }

        public TerminalNode MUL() {
            return getToken(SwiftSqlParser.MUL, 0);
        }

        public TerminalNode DIV() {
            return getToken(SwiftSqlParser.DIV, 0);
        }

        public OpContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_op;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitOp(this);
        }
    }

    public final OpContext op() throws RecognitionException {
        OpContext _localctx = new OpContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_op);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(358);
                _la = _input.LA(1);
                if (!(((((_la - 60)) & ~0x3f) == 0 && ((1L << (_la - 60)) & ((1L << (MUL - 60)) | (1L << (DIV - 60)) | (1L << (PLUS - 60)) | (1L << (MINUS - 60)))) != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class BoolOpContext extends ParserRuleContext {
        public TerminalNode GREATER() {
            return getToken(SwiftSqlParser.GREATER, 0);
        }

        public TerminalNode GEQ() {
            return getToken(SwiftSqlParser.GEQ, 0);
        }

        public TerminalNode EQ() {
            return getToken(SwiftSqlParser.EQ, 0);
        }

        public TerminalNode NEQ() {
            return getToken(SwiftSqlParser.NEQ, 0);
        }

        public TerminalNode LEQ() {
            return getToken(SwiftSqlParser.LEQ, 0);
        }

        public TerminalNode LESS() {
            return getToken(SwiftSqlParser.LESS, 0);
        }

        public TerminalNode LIKE() {
            return getToken(SwiftSqlParser.LIKE, 0);
        }

        public BoolOpContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_boolOp;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterBoolOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitBoolOp(this);
        }
    }

    public final BoolOpContext boolOp() throws RecognitionException {
        BoolOpContext _localctx = new BoolOpContext(_ctx, getState());
        enterRule(_localctx, 52, RULE_boolOp);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(360);
                _la = _input.LA(1);
                if (!(((((_la - 55)) & ~0x3f) == 0 && ((1L << (_la - 55)) & ((1L << (LIKE - 55)) | (1L << (EQ - 55)) | (1L << (GREATER - 55)) | (1L << (LESS - 55)) | (1L << (GEQ - 55)) | (1L << (LEQ - 55)) | (1L << (NEQ - 55)))) != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class LogicOpContext extends ParserRuleContext {
        public TerminalNode AND() {
            return getToken(SwiftSqlParser.AND, 0);
        }

        public TerminalNode OR() {
            return getToken(SwiftSqlParser.OR, 0);
        }

        public LogicOpContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_logicOp;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterLogicOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitLogicOp(this);
        }
    }

    public final LogicOpContext logicOp() throws RecognitionException {
        LogicOpContext _localctx = new LogicOpContext(_ctx, getState());
        enterRule(_localctx, 54, RULE_logicOp);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(362);
                _la = _input.LA(1);
                if (!(_la == AND || _la == OR)) {
                    _errHandler.recoverInline(this);
                } else {
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class NameContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(SwiftSqlParser.IDENTIFIER, 0);
        }

        public NameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitName(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 56, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(364);
                match(IDENTIFIER);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class NamesContext extends ParserRuleContext {
        public List<NameContext> name() {
            return getRuleContexts(NameContext.class);
        }

        public NameContext name(int i) {
            return getRuleContext(NameContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public NamesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_names;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterNames(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitNames(this);
        }
    }

    public final NamesContext names() throws RecognitionException {
        NamesContext _localctx = new NamesContext(_ctx, getState());
        enterRule(_localctx, 58, RULE_names);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(366);
                name();
                setState(371);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 39, _ctx);
                while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1 + 1) {
                        {
                            {
                                setState(367);
                                match(COMMA);
                                setState(368);
                                name();
                            }
                        }
                    }
                    setState(373);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 39, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ValueContext extends ParserRuleContext {
        public TerminalNode NULL() {
            return getToken(SwiftSqlParser.NULL, 0);
        }

        public TerminalNode NUMERIC_LITERAL() {
            return getToken(SwiftSqlParser.NUMERIC_LITERAL, 0);
        }

        public TerminalNode STRING_LITERAL() {
            return getToken(SwiftSqlParser.STRING_LITERAL, 0);
        }

        public ValueContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_value;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterValue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitValue(this);
        }
    }

    public final ValueContext value() throws RecognitionException {
        ValueContext _localctx = new ValueContext(_ctx, getState());
        enterRule(_localctx, 60, RULE_value);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(374);
                _la = _input.LA(1);
                if (!(_la == NULL || _la == NUMERIC_LITERAL || _la == STRING_LITERAL)) {
                    _errHandler.recoverInline(this);
                } else {
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ValuesContext extends ParserRuleContext {
        public List<ValueContext> value() {
            return getRuleContexts(ValueContext.class);
        }

        public ValueContext value(int i) {
            return getRuleContext(ValueContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public ValuesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_values;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterValues(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitValues(this);
        }
    }

    public final ValuesContext values() throws RecognitionException {
        ValuesContext _localctx = new ValuesContext(_ctx, getState());
        enterRule(_localctx, 62, RULE_values);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(376);
                value();
                setState(381);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 40, _ctx);
                while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1 + 1) {
                        {
                            {
                                setState(377);
                                match(COMMA);
                                setState(378);
                                value();
                            }
                        }
                    }
                    setState(383);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 40, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 19:
                return expr_sempred((ExprContext) _localctx, predIndex);
            case 24:
                return boolExpr_sempred((BoolExprContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean expr_sempred(ExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 2);
        }
        return true;
    }

    private boolean boolExpr_sempred(BoolExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 1:
                return precpred(_ctx, 2);
        }
        return true;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\\\u0183\4\2\t\2\4" +
                    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t" +
                    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!" +
                    "\t!\3\2\3\2\3\2\3\3\3\3\3\3\7\3I\n\3\f\3\16\3L\13\3\3\3\5\3O\n\3\3\4\3" +
                    "\4\5\4S\n\4\3\5\3\5\3\5\5\5X\n\5\3\6\3\6\3\6\3\6\5\6^\n\6\3\7\3\7\3\7" +
                    "\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\7\bm\n\b\f\b\16\bp\13\b\3\t\3" +
                    "\t\3\t\3\t\5\tv\n\t\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\5\f\u0080\n\f" +
                    "\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17" +
                    "\3\17\3\17\3\17\3\17\5\17\u0095\n\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17" +
                    "\3\17\3\17\7\17\u00a0\n\17\f\17\16\17\u00a3\13\17\3\20\3\20\3\20\3\20" +
                    "\3\20\3\20\3\21\3\21\3\21\3\22\3\22\5\22\u00b0\n\22\3\22\3\22\3\22\3\22" +
                    "\3\22\3\22\3\22\5\22\u00b9\n\22\3\22\5\22\u00bc\n\22\3\22\5\22\u00bf\n" +
                    "\22\3\22\3\22\5\22\u00c3\n\22\3\22\3\22\3\22\5\22\u00c8\n\22\3\22\3\22" +
                    "\5\22\u00cc\n\22\3\22\3\22\3\22\5\22\u00d1\n\22\3\22\3\22\5\22\u00d5\n" +
                    "\22\3\23\3\23\3\23\5\23\u00da\n\23\3\23\5\23\u00dd\n\23\3\23\3\23\3\23" +
                    "\5\23\u00e2\n\23\3\23\5\23\u00e5\n\23\7\23\u00e7\n\23\f\23\16\23\u00ea" +
                    "\13\23\5\23\u00ec\n\23\3\24\3\24\5\24\u00f0\n\24\3\24\3\24\3\24\5\24\u00f5" +
                    "\n\24\7\24\u00f7\n\24\f\24\16\24\u00fa\13\24\3\25\3\25\3\25\3\25\3\25" +
                    "\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25" +
                    "\5\25\u010f\n\25\3\25\3\25\3\25\3\25\7\25\u0115\n\25\f\25\16\25\u0118" +
                    "\13\25\3\26\3\26\3\26\5\26\u011d\n\26\3\27\3\27\3\27\3\27\3\27\7\27\u0124" +
                    "\n\27\f\27\16\27\u0127\13\27\5\27\u0129\n\27\3\27\3\27\3\30\3\30\3\31" +
                    "\3\31\5\31\u0131\n\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u013a\n" +
                    "\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u0144\n\31\3\31\3\31" +
                    "\5\31\u0148\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32" +
                    "\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u015e\n\32\3\32\3\32" +
                    "\3\32\3\32\7\32\u0164\n\32\f\32\16\32\u0167\13\32\3\33\3\33\3\34\3\34" +
                    "\3\35\3\35\3\36\3\36\3\37\3\37\3\37\7\37\u0174\n\37\f\37\16\37\u0177\13" +
                    "\37\3 \3 \3!\3!\3!\7!\u017e\n!\f!\16!\u0181\13!\3!\tJ\u00a1\u00e8\u00f8" +
                    "\u0125\u0175\u017f\4(\62\"\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$" +
                    "&(*,.\60\62\64\668:<>@\2\t\3\2\30(\3\2\f\r\3\2-\63\4\2>?AB\4\299CH\3\2" +
                    "\678\4\2\26\26YZ\u0196\2B\3\2\2\2\4E\3\2\2\2\6R\3\2\2\2\bW\3\2\2\2\n]" +
                    "\3\2\2\2\f_\3\2\2\2\16f\3\2\2\2\20q\3\2\2\2\22w\3\2\2\2\24y\3\2\2\2\26" +
                    "\177\3\2\2\2\30\u0081\3\2\2\2\32\u0087\3\2\2\2\34\u008d\3\2\2\2\36\u00a4" +
                    "\3\2\2\2 \u00aa\3\2\2\2\"\u00ad\3\2\2\2$\u00eb\3\2\2\2&\u00ed\3\2\2\2" +
                    "(\u010e\3\2\2\2*\u011c\3\2\2\2,\u011e\3\2\2\2.\u012c\3\2\2\2\60\u0147" +
                    "\3\2\2\2\62\u015d\3\2\2\2\64\u0168\3\2\2\2\66\u016a\3\2\2\28\u016c\3\2" +
                    "\2\2:\u016e\3\2\2\2<\u0170\3\2\2\2>\u0178\3\2\2\2@\u017a\3\2\2\2BC\5\4" +
                    "\3\2CD\7\2\2\3D\3\3\2\2\2EJ\5\6\4\2FG\7R\2\2GI\5\6\4\2HF\3\2\2\2IL\3\2" +
                    "\2\2JK\3\2\2\2JH\3\2\2\2KN\3\2\2\2LJ\3\2\2\2MO\7R\2\2NM\3\2\2\2NO\3\2" +
                    "\2\2O\5\3\2\2\2PS\5\b\5\2QS\5\n\6\2RP\3\2\2\2RQ\3\2\2\2S\7\3\2\2\2TX\5" +
                    "\f\7\2UX\5\24\13\2VX\5\26\f\2WT\3\2\2\2WU\3\2\2\2WV\3\2\2\2X\t\3\2\2\2" +
                    "Y^\5\34\17\2Z^\5\36\20\2[^\5\"\22\2\\^\5 \21\2]Y\3\2\2\2]Z\3\2\2\2][\3" +
                    "\2\2\2]\\\3\2\2\2^\13\3\2\2\2_`\7\24\2\2`a\7\25\2\2ab\5:\36\2bc\7O\2\2" +
                    "cd\5\16\b\2de\7P\2\2e\r\3\2\2\2fg\5:\36\2gn\5\20\t\2hi\7Q\2\2ij\5:\36" +
                    "\2jk\5\20\t\2km\3\2\2\2lh\3\2\2\2mp\3\2\2\2nl\3\2\2\2no\3\2\2\2o\17\3" +
                    "\2\2\2pn\3\2\2\2qu\5\22\n\2rs\7O\2\2st\7Y\2\2tv\7P\2\2ur\3\2\2\2uv\3\2" +
                    "\2\2v\21\3\2\2\2wx\t\2\2\2x\23\3\2\2\2yz\7)\2\2z{\7\25\2\2{|\5:\36\2|" +
                    "\25\3\2\2\2}\u0080\5\30\r\2~\u0080\5\32\16\2\177}\3\2\2\2\177~\3\2\2\2" +
                    "\u0080\27\3\2\2\2\u0081\u0082\7*\2\2\u0082\u0083\7\25\2\2\u0083\u0084" +
                    "\5:\36\2\u0084\u0085\7+\2\2\u0085\u0086\5\16\b\2\u0086\31\3\2\2\2\u0087" +
                    "\u0088\7*\2\2\u0088\u0089\7\25\2\2\u0089\u008a\5:\36\2\u008a\u008b\7)" +
                    "\2\2\u008b\u008c\5<\37\2\u008c\33\3\2\2\2\u008d\u008e\7\17\2\2\u008e\u008f" +
                    "\7\20\2\2\u008f\u0094\5:\36\2\u0090\u0091\7O\2\2\u0091\u0092\5<\37\2\u0092" +
                    "\u0093\7P\2\2\u0093\u0095\3\2\2\2\u0094\u0090\3\2\2\2\u0094\u0095\3\2" +
                    "\2\2\u0095\u0096\3\2\2\2\u0096\u0097\7\21\2\2\u0097\u0098\7O\2\2\u0098" +
                    "\u0099\5@!\2\u0099\u00a1\7P\2\2\u009a\u009b\7Q\2\2\u009b\u009c\7O\2\2" +
                    "\u009c\u009d\5@!\2\u009d\u009e\7P\2\2\u009e\u00a0\3\2\2\2\u009f\u009a" +
                    "\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a1\u009f\3\2\2\2\u00a2" +
                    "\35\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a5\7\22\2\2\u00a5\u00a6\7\6\2" +
                    "\2\u00a6\u00a7\5:\36\2\u00a7\u00a8\7\7\2\2\u00a8\u00a9\5(\25\2\u00a9\37" +
                    "\3\2\2\2\u00aa\u00ab\7\23\2\2\u00ab\u00ac\5:\36\2\u00ac!\3\2\2\2\u00ad" +
                    "\u00af\7\3\2\2\u00ae\u00b0\7\4\2\2\u00af\u00ae\3\2\2\2\u00af\u00b0\3\2" +
                    "\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\5$\23\2\u00b2\u00b8\7\6\2\2\u00b3" +
                    "\u00b9\5:\36\2\u00b4\u00b5\7O\2\2\u00b5\u00b6\5\"\22\2\u00b6\u00b7\7P" +
                    "\2\2\u00b7\u00b9\3\2\2\2\u00b8\u00b3\3\2\2\2\u00b8\u00b4\3\2\2\2\u00b9" +
                    "\u00be\3\2\2\2\u00ba\u00bc\7\5\2\2\u00bb\u00ba\3\2\2\2\u00bb\u00bc\3\2" +
                    "\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00bf\5:\36\2\u00be\u00bb\3\2\2\2\u00be" +
                    "\u00bf\3\2\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00c1\7\7\2\2\u00c1\u00c3\5(" +
                    "\25\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c7\3\2\2\2\u00c4" +
                    "\u00c5\7\b\2\2\u00c5\u00c6\7\t\2\2\u00c6\u00c8\5<\37\2\u00c7\u00c4\3\2" +
                    "\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00cb\3\2\2\2\u00c9\u00ca\7\n\2\2\u00ca" +
                    "\u00cc\5(\25\2\u00cb\u00c9\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00d0\3\2" +
                    "\2\2\u00cd\u00ce\7\13\2\2\u00ce\u00cf\7\t\2\2\u00cf\u00d1\5&\24\2\u00d0" +
                    "\u00cd\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d4\3\2\2\2\u00d2\u00d3\7\16" +
                    "\2\2\u00d3\u00d5\7Y\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5" +
                    "#\3\2\2\2\u00d6\u00ec\7>\2\2\u00d7\u00dc\5*\26\2\u00d8\u00da\7\5\2\2\u00d9" +
                    "\u00d8\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00dd\5:" +
                    "\36\2\u00dc\u00d9\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00e8\3\2\2\2\u00de" +
                    "\u00df\7Q\2\2\u00df\u00e4\5*\26\2\u00e0\u00e2\7\5\2\2\u00e1\u00e0\3\2" +
                    "\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e5\5:\36\2\u00e4" +
                    "\u00e1\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e7\3\2\2\2\u00e6\u00de\3\2" +
                    "\2\2\u00e7\u00ea\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9" +
                    "\u00ec\3\2\2\2\u00ea\u00e8\3\2\2\2\u00eb\u00d6\3\2\2\2\u00eb\u00d7\3\2" +
                    "\2\2\u00ec%\3\2\2\2\u00ed\u00ef\5:\36\2\u00ee\u00f0\t\3\2\2\u00ef\u00ee" +
                    "\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f8\3\2\2\2\u00f1\u00f2\7Q\2\2\u00f2" +
                    "\u00f4\5:\36\2\u00f3\u00f5\t\3\2\2\u00f4\u00f3\3\2\2\2\u00f4\u00f5\3\2" +
                    "\2\2\u00f5\u00f7\3\2\2\2\u00f6\u00f1\3\2\2\2\u00f7\u00fa\3\2\2\2\u00f8" +
                    "\u00f9\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f9\'\3\2\2\2\u00fa\u00f8\3\2\2\2" +
                    "\u00fb\u00fc\b\25\1\2\u00fc\u010f\5*\26\2\u00fd\u00fe\5*\26\2\u00fe\u00ff" +
                    "\5\64\33\2\u00ff\u0100\5*\26\2\u0100\u010f\3\2\2\2\u0101\u0102\7O\2\2" +
                    "\u0102\u0103\5*\26\2\u0103\u0104\5\64\33\2\u0104\u0105\5*\26\2\u0105\u0106" +
                    "\7P\2\2\u0106\u010f\3\2\2\2\u0107\u010f\5\62\32\2\u0108\u0109\7O\2\2\u0109" +
                    "\u010a\5(\25\2\u010a\u010b\5\64\33\2\u010b\u010c\5(\25\2\u010c\u010d\7" +
                    "P\2\2\u010d\u010f\3\2\2\2\u010e\u00fb\3\2\2\2\u010e\u00fd\3\2\2\2\u010e" +
                    "\u0101\3\2\2\2\u010e\u0107\3\2\2\2\u010e\u0108\3\2\2\2\u010f\u0116\3\2" +
                    "\2\2\u0110\u0111\f\4\2\2\u0111\u0112\5\64\33\2\u0112\u0113\5(\25\5\u0113" +
                    "\u0115\3\2\2\2\u0114\u0110\3\2\2\2\u0115\u0118\3\2\2\2\u0116\u0114\3\2" +
                    "\2\2\u0116\u0117\3\2\2\2\u0117)\3\2\2\2\u0118\u0116\3\2\2\2\u0119\u011d" +
                    "\5> \2\u011a\u011d\5:\36\2\u011b\u011d\5,\27\2\u011c\u0119\3\2\2\2\u011c" +
                    "\u011a\3\2\2\2\u011c\u011b\3\2\2\2\u011d+\3\2\2\2\u011e\u011f\5.\30\2" +
                    "\u011f\u0128\7O\2\2\u0120\u0125\5*\26\2\u0121\u0122\7Q\2\2\u0122\u0124" +
                    "\5*\26\2\u0123\u0121\3\2\2\2\u0124\u0127\3\2\2\2\u0125\u0126\3\2\2\2\u0125" +
                    "\u0123\3\2\2\2\u0126\u0129\3\2\2\2\u0127\u0125\3\2\2\2\u0128\u0120\3\2" +
                    "\2\2\u0128\u0129\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012b\7P\2\2\u012b" +
                    "-\3\2\2\2\u012c\u012d\t\4\2\2\u012d/\3\2\2\2\u012e\u0130\5*\26\2\u012f" +
                    "\u0131\7\64\2\2\u0130\u012f\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u0132\3" +
                    "\2\2\2\u0132\u0133\7\65\2\2\u0133\u0134\7O\2\2\u0134\u0135\5@!\2\u0135" +
                    "\u0136\7P\2\2\u0136\u0148\3\2\2\2\u0137\u0139\5*\26\2\u0138\u013a\7\64" +
                    "\2\2\u0139\u0138\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u013b\3\2\2\2\u013b" +
                    "\u013c\7\66\2\2\u013c\u013d\7Y\2\2\u013d\u013e\7\67\2\2\u013e\u013f\7" +
                    "Y\2\2\u013f\u0148\3\2\2\2\u0140\u0141\5*\26\2\u0141\u0143\7:\2\2\u0142" +
                    "\u0144\7\64\2\2\u0143\u0142\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\3" +
                    "\2\2\2\u0145\u0146\7\26\2\2\u0146\u0148\3\2\2\2\u0147\u012e\3\2\2\2\u0147" +
                    "\u0137\3\2\2\2\u0147\u0140\3\2\2\2\u0148\61\3\2\2\2\u0149\u014a\b\32\1" +
                    "\2\u014a\u014b\5*\26\2\u014b\u014c\5\66\34\2\u014c\u014d\5*\26\2\u014d" +
                    "\u015e\3\2\2\2\u014e\u014f\7O\2\2\u014f\u0150\5*\26\2\u0150\u0151\5\66" +
                    "\34\2\u0151\u0152\5*\26\2\u0152\u0153\7P\2\2\u0153\u015e\3\2\2\2\u0154" +
                    "\u015e\5\60\31\2\u0155\u0156\7\64\2\2\u0156\u015e\5\62\32\5\u0157\u0158" +
                    "\7O\2\2\u0158\u0159\5\62\32\2\u0159\u015a\58\35\2\u015a\u015b\5\62\32" +
                    "\2\u015b\u015c\7P\2\2\u015c\u015e\3\2\2\2\u015d\u0149\3\2\2\2\u015d\u014e" +
                    "\3\2\2\2\u015d\u0154\3\2\2\2\u015d\u0155\3\2\2\2\u015d\u0157\3\2\2\2\u015e" +
                    "\u0165\3\2\2\2\u015f\u0160\f\4\2\2\u0160\u0161\58\35\2\u0161\u0162\5\62" +
                    "\32\5\u0162\u0164\3\2\2\2\u0163\u015f\3\2\2\2\u0164\u0167\3\2\2\2\u0165" +
                    "\u0163\3\2\2\2\u0165\u0166\3\2\2\2\u0166\63\3\2\2\2\u0167\u0165\3\2\2" +
                    "\2\u0168\u0169\t\5\2\2\u0169\65\3\2\2\2\u016a\u016b\t\6\2\2\u016b\67\3" +
                    "\2\2\2\u016c\u016d\t\7\2\2\u016d9\3\2\2\2\u016e\u016f\7X\2\2\u016f;\3" +
                    "\2\2\2\u0170\u0175\5:\36\2\u0171\u0172\7Q\2\2\u0172\u0174\5:\36\2\u0173" +
                    "\u0171\3\2\2\2\u0174\u0177\3\2\2\2\u0175\u0176\3\2\2\2\u0175\u0173\3\2" +
                    "\2\2\u0176=\3\2\2\2\u0177\u0175\3\2\2\2\u0178\u0179\t\b\2\2\u0179?\3\2" +
                    "\2\2\u017a\u017f\5> \2\u017b\u017c\7Q\2\2\u017c\u017e\5> \2\u017d\u017b" +
                    "\3\2\2\2\u017e\u0181\3\2\2\2\u017f\u0180\3\2\2\2\u017f\u017d\3\2\2\2\u0180" +
                    "A\3\2\2\2\u0181\u017f\3\2\2\2+JNRW]nu\177\u0094\u00a1\u00af\u00b8\u00bb" +
                    "\u00be\u00c2\u00c7\u00cb\u00d0\u00d4\u00d9\u00dc\u00e1\u00e4\u00e8\u00eb" +
                    "\u00ef\u00f4\u00f8\u010e\u0116\u011c\u0125\u0128\u0130\u0139\u0143\u0147" +
                    "\u015d\u0165\u0175\u017f";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}