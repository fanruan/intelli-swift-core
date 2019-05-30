// Generated from SwiftSqlParser.g4 by ANTLR 4.5.3
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
            SELECT = 1, DISTINCT = 2, FROM = 3, WHERE = 4, GROUP = 5, BY = 6, HAVING = 7, ORDER = 8,
            ASC = 9, DESC = 10, LIMIT = 11, INSERT = 12, INTO = 13, VALUES = 14, DELETE = 15, CREATE = 16,
            TABLE = 17, NULL = 18, PARTITION = 19, BIT = 20, TINYINT = 21, SMALLINT = 22, INTEGER = 23,
            BIGINT = 24, FLOAT = 25, REAL = 26, DOUBLE = 27, NUMERIC = 28, DECIMAL = 29, CHAR = 30,
            VARCHAR = 31, LONGVARCHAR = 32, DATE = 33, TIME = 34, TIMESTAMP = 35, BOOLEAN = 36,
            DROP = 37, ALTER = 38, ADD = 39, COLUMN = 40, MAX = 41, MIN = 42, SUM = 43, AVG = 44,
            COUNT = 45, MID = 46, TODATE = 47, NOT = 48, IN = 49, BETWEEN = 50, AND = 51, OR = 52,
            LIKE = 53, IS = 54, LINE = 55, HASH = 56, RANGE = 57, MUL = 58, DIV = 59, MOD = 60, PLUS = 61,
            MINUS = 62, EQ = 63, GREATER = 64, LESS = 65, GEQ = 66, LEQ = 67, NEQ = 68, EXCLAMATION = 69,
            BIT_NOT = 70, BIT_OR = 71, BIT_AND = 72, BIT_XOR = 73, DOT = 74, L_PAR = 75, R_PAR = 76,
            COMMA = 77, SEMI = 78, AT = 79, SINGLE_QUOTE = 80, DOUBLE_QUOTE = 81, REVERSE_QUOTE = 82,
            COLON = 83, IDENTIFIER = 84, NUMERIC_LITERAL = 85, STRING_LITERAL = 86, DIGIT = 87,
            WS = 88;
    public static final int
            RULE_root = 0, RULE_sqls = 1, RULE_sql = 2, RULE_ddl = 3, RULE_dml = 4,
            RULE_createTable = 5, RULE_columnDefinitions = 6, RULE_dataType = 7, RULE_function = 8,
            RULE_columnDefinition = 9, RULE_dropTable = 10, RULE_alterTable = 11,
            RULE_alterTableAddColumn = 12, RULE_alterTableDropColumn = 13, RULE_insert = 14,
            RULE_values = 15, RULE_delete = 16, RULE_select = 17, RULE_columnOrders = 18,
            RULE_names = 19, RULE_expr = 20, RULE_keywordBoolExpr = 21, RULE_inExpr = 22,
            RULE_betweenExpr = 23, RULE_simpleExpr = 24, RULE_funcCall = 25, RULE_literal = 26,
            RULE_unaryBoolOp = 27, RULE_binaryOp = 28, RULE_binaryBoolOp = 29, RULE_name = 30;
    public static final String[] ruleNames = {
            "root", "sqls", "sql", "ddl", "dml", "createTable", "columnDefinitions",
            "dataType", "function", "columnDefinition", "dropTable", "alterTable",
            "alterTableAddColumn", "alterTableDropColumn", "insert", "values", "delete",
            "select", "columnOrders", "names", "expr", "keywordBoolExpr", "inExpr",
            "betweenExpr", "simpleExpr", "funcCall", "literal", "unaryBoolOp", "binaryOp",
            "binaryBoolOp", "name"
    };

    private static final String[] _LITERAL_NAMES = {
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, "'*'", "'/'",
            "'%'", "'+'", "'-'", "'='", "'>'", "'<'", "'>='", "'<='", "'!='", "'!'",
            "'~'", "'|'", "'&'", "'^'", "'.'", "'('", "')'", "','", "';'", "'@'",
            "'''", "'\"'", "'`'", "':'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, "SELECT", "DISTINCT", "FROM", "WHERE", "GROUP", "BY", "HAVING",
            "ORDER", "ASC", "DESC", "LIMIT", "INSERT", "INTO", "VALUES", "DELETE",
            "CREATE", "TABLE", "NULL", "PARTITION", "BIT", "TINYINT", "SMALLINT",
            "INTEGER", "BIGINT", "FLOAT", "REAL", "DOUBLE", "NUMERIC", "DECIMAL",
            "CHAR", "VARCHAR", "LONGVARCHAR", "DATE", "TIME", "TIMESTAMP", "BOOLEAN",
            "DROP", "ALTER", "ADD", "COLUMN", "MAX", "MIN", "SUM", "AVG", "COUNT",
            "MID", "TODATE", "NOT", "IN", "BETWEEN", "AND", "OR", "LIKE", "IS", "LINE",
            "HASH", "RANGE", "MUL", "DIV", "MOD", "PLUS", "MINUS", "EQ", "GREATER",
            "LESS", "GEQ", "LEQ", "NEQ", "EXCLAMATION", "BIT_NOT", "BIT_OR", "BIT_AND",
            "BIT_XOR", "DOT", "L_PAR", "R_PAR", "COMMA", "SEMI", "AT", "SINGLE_QUOTE",
            "DOUBLE_QUOTE", "REVERSE_QUOTE", "COLON", "IDENTIFIER", "NUMERIC_LITERAL",
            "STRING_LITERAL", "DIGIT", "WS"
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
                setState(62);
                sqls();
                setState(63);
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
            setState(80);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 3, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(65);
                    sql();
                    setState(67);
                    _la = _input.LA(1);
                    if (_la == SEMI) {
                        {
                            setState(66);
                            match(SEMI);
                        }
                    }

                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(69);
                    sql();
                    setState(74);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                    while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                        if (_alt == 1) {
                            {
                                {
                                    setState(70);
                                    match(SEMI);
                                    setState(71);
                                    sql();
                                }
                            }
                        }
                        setState(76);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                    }
                    setState(78);
                    _la = _input.LA(1);
                    if (_la == SEMI) {
                        {
                            setState(77);
                            match(SEMI);
                        }
                    }

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
            setState(84);
            switch (_input.LA(1)) {
                case CREATE:
                case DROP:
                case ALTER:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(82);
                    ddl();
                }
                break;
                case SELECT:
                case INSERT:
                case DELETE:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(83);
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
            setState(89);
            switch (_input.LA(1)) {
                case CREATE:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(86);
                    createTable();
                }
                break;
                case DROP:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(87);
                    dropTable();
                }
                break;
                case ALTER:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(88);
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
            setState(94);
            switch (_input.LA(1)) {
                case INSERT:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(91);
                    insert();
                }
                break;
                case DELETE:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(92);
                    delete();
                }
                break;
                case SELECT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(93);
                    select();
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
        public NameContext tableName;

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
                setState(96);
                match(CREATE);
                setState(97);
                match(TABLE);
                setState(98);
                ((CreateTableContext) _localctx).tableName = name();
                setState(99);
                match(L_PAR);
                setState(100);
                columnDefinitions();
                setState(101);
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
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(103);
                name();
                setState(104);
                columnDefinition();
                setState(111);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
                while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1 + 1) {
                        {
                            {
                                setState(105);
                                match(COMMA);
                                setState(106);
                                name();
                                setState(107);
                                columnDefinition();
                            }
                        }
                    }
                    setState(113);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 7, _ctx);
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
        enterRule(_localctx, 14, RULE_dataType);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(114);
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

    public static class FunctionContext extends ParserRuleContext {
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

        public FunctionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_function;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterFunction(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitFunction(this);
        }
    }

    public final FunctionContext function() throws RecognitionException {
        FunctionContext _localctx = new FunctionContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_function);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(116);
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
        enterRule(_localctx, 18, RULE_columnDefinition);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(118);
                dataType();
                setState(122);
                _la = _input.LA(1);
                if (_la == L_PAR) {
                    {
                        setState(119);
                        match(L_PAR);
                        setState(120);
                        ((ColumnDefinitionContext) _localctx).length = match(NUMERIC_LITERAL);
                        setState(121);
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

    public static class DropTableContext extends ParserRuleContext {
        public NameContext tableName;

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
        enterRule(_localctx, 20, RULE_dropTable);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(124);
                match(DROP);
                setState(125);
                match(TABLE);
                setState(126);
                ((DropTableContext) _localctx).tableName = name();
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
        enterRule(_localctx, 22, RULE_alterTable);
        try {
            setState(130);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 9, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(128);
                    alterTableAddColumn();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(129);
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
        public NameContext tableName;

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
        enterRule(_localctx, 24, RULE_alterTableAddColumn);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(132);
                match(ALTER);
                setState(133);
                match(TABLE);
                setState(134);
                ((AlterTableAddColumnContext) _localctx).tableName = name();
                setState(135);
                match(ADD);
                setState(136);
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
        public NameContext tableName;
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
        enterRule(_localctx, 26, RULE_alterTableDropColumn);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(138);
                match(ALTER);
                setState(139);
                match(TABLE);
                setState(140);
                ((AlterTableDropColumnContext) _localctx).tableName = name();
                setState(141);
                match(DROP);
                setState(142);
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
        public NameContext tableName;
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

        public ValuesContext values() {
            return getRuleContext(ValuesContext.class, 0);
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
        enterRule(_localctx, 28, RULE_insert);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(144);
                match(INSERT);
                setState(145);
                match(INTO);
                setState(146);
                ((InsertContext) _localctx).tableName = name();
                setState(151);
                _la = _input.LA(1);
                if (_la == L_PAR) {
                    {
                        setState(147);
                        match(L_PAR);
                        setState(148);
                        ((InsertContext) _localctx).columnNames = names();
                        setState(149);
                        match(R_PAR);
                    }
                }

                setState(153);
                match(VALUES);
                setState(154);
                match(L_PAR);
                setState(155);
                values();
                setState(156);
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

    public static class ValuesContext extends ParserRuleContext {
        public List<LiteralContext> literal() {
            return getRuleContexts(LiteralContext.class);
        }

        public LiteralContext literal(int i) {
            return getRuleContext(LiteralContext.class, i);
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
        enterRule(_localctx, 30, RULE_values);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(158);
                literal();
                setState(163);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(159);
                            match(COMMA);
                            setState(160);
                            literal();
                        }
                    }
                    setState(165);
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

    public static class DeleteContext extends ParserRuleContext {
        public NameContext tableName;
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
        enterRule(_localctx, 32, RULE_delete);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(166);
                match(DELETE);
                setState(167);
                match(FROM);
                setState(168);
                ((DeleteContext) _localctx).tableName = name();
                setState(169);
                match(WHERE);
                setState(170);
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

    public static class SelectContext extends ParserRuleContext {
        public NameContext tableName;
        public ExprContext where;
        public NamesContext columnNames;
        public ExprContext having;
        public Token limit;

        public TerminalNode SELECT() {
            return getToken(SwiftSqlParser.SELECT, 0);
        }

        public TerminalNode FROM() {
            return getToken(SwiftSqlParser.FROM, 0);
        }

        public List<NameContext> name() {
            return getRuleContexts(NameContext.class);
        }

        public NameContext name(int i) {
            return getRuleContext(NameContext.class, i);
        }

        public TerminalNode MUL() {
            return getToken(SwiftSqlParser.MUL, 0);
        }

        public TerminalNode DISTINCT() {
            return getToken(SwiftSqlParser.DISTINCT, 0);
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

        public ColumnOrdersContext columnOrders() {
            return getRuleContext(ColumnOrdersContext.class, 0);
        }

        public TerminalNode LIMIT() {
            return getToken(SwiftSqlParser.LIMIT, 0);
        }

        public List<FuncCallContext> funcCall() {
            return getRuleContexts(FuncCallContext.class);
        }

        public FuncCallContext funcCall(int i) {
            return getRuleContext(FuncCallContext.class, i);
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

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
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
        enterRule(_localctx, 34, RULE_select);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(172);
                match(SELECT);
                setState(174);
                _la = _input.LA(1);
                if (_la == DISTINCT) {
                    {
                        setState(173);
                        match(DISTINCT);
                    }
                }

                setState(189);
                switch (_input.LA(1)) {
                    case MUL: {
                        setState(176);
                        match(MUL);
                    }
                    break;
                    case MAX:
                    case MIN:
                    case SUM:
                    case AVG:
                    case COUNT:
                    case MID:
                    case TODATE:
                    case L_PAR:
                    case IDENTIFIER: {
                        setState(179);
                        switch (_input.LA(1)) {
                            case L_PAR:
                            case IDENTIFIER: {
                                setState(177);
                                name();
                            }
                            break;
                            case MAX:
                            case MIN:
                            case SUM:
                            case AVG:
                            case COUNT:
                            case MID:
                            case TODATE: {
                                setState(178);
                                funcCall();
                            }
                            break;
                            default:
                                throw new NoViableAltException(this);
                        }
                        setState(186);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (((((_la - 41)) & ~0x3f) == 0 && ((1L << (_la - 41)) & ((1L << (MAX - 41)) | (1L << (MIN - 41)) | (1L << (SUM - 41)) | (1L << (AVG - 41)) | (1L << (COUNT - 41)) | (1L << (MID - 41)) | (1L << (TODATE - 41)) | (1L << (COMMA - 41)))) != 0)) {
                            {
                                setState(184);
                                switch (_input.LA(1)) {
                                    case COMMA: {
                                        setState(181);
                                        match(COMMA);
                                        setState(182);
                                        name();
                                    }
                                    break;
                                    case MAX:
                                    case MIN:
                                    case SUM:
                                    case AVG:
                                    case COUNT:
                                    case MID:
                                    case TODATE: {
                                        setState(183);
                                        funcCall();
                                    }
                                    break;
                                    default:
                                        throw new NoViableAltException(this);
                                }
                            }
                            setState(188);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                    break;
                    default:
                        throw new NoViableAltException(this);
                }
                setState(191);
                match(FROM);
                setState(192);
                ((SelectContext) _localctx).tableName = name();
                setState(195);
                _la = _input.LA(1);
                if (_la == WHERE) {
                    {
                        setState(193);
                        match(WHERE);
                        setState(194);
                        ((SelectContext) _localctx).where = expr(0);
                    }
                }

                setState(200);
                _la = _input.LA(1);
                if (_la == GROUP) {
                    {
                        setState(197);
                        match(GROUP);
                        setState(198);
                        match(BY);
                        setState(199);
                        ((SelectContext) _localctx).columnNames = names();
                    }
                }

                setState(204);
                _la = _input.LA(1);
                if (_la == HAVING) {
                    {
                        setState(202);
                        match(HAVING);
                        setState(203);
                        ((SelectContext) _localctx).having = expr(0);
                    }
                }

                setState(209);
                _la = _input.LA(1);
                if (_la == ORDER) {
                    {
                        setState(206);
                        match(ORDER);
                        setState(207);
                        match(BY);
                        setState(208);
                        columnOrders();
                    }
                }

                setState(213);
                _la = _input.LA(1);
                if (_la == LIMIT) {
                    {
                        setState(211);
                        match(LIMIT);
                        setState(212);
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

    public static class ColumnOrdersContext extends ParserRuleContext {
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

        public ColumnOrdersContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_columnOrders;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterColumnOrders(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitColumnOrders(this);
        }
    }

    public final ColumnOrdersContext columnOrders() throws RecognitionException {
        ColumnOrdersContext _localctx = new ColumnOrdersContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_columnOrders);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(215);
                name();
                setState(217);
                _la = _input.LA(1);
                if (_la == ASC || _la == DESC) {
                    {
                        setState(216);
                        _la = _input.LA(1);
                        if (!(_la == ASC || _la == DESC)) {
                            _errHandler.recoverInline(this);
                        } else {
                            consume();
                        }
                    }
                }

                setState(226);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(219);
                            match(COMMA);
                            setState(220);
                            name();
                            setState(222);
                            _la = _input.LA(1);
                            if (_la == ASC || _la == DESC) {
                                {
                                    setState(221);
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
                    setState(228);
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
        enterRule(_localctx, 38, RULE_names);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(229);
                name();
                setState(234);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(230);
                            match(COMMA);
                            setState(231);
                            name();
                        }
                    }
                    setState(236);
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

    public static class ExprContext extends ParserRuleContext {
        public List<SimpleExprContext> simpleExpr() {
            return getRuleContexts(SimpleExprContext.class);
        }

        public SimpleExprContext simpleExpr(int i) {
            return getRuleContext(SimpleExprContext.class, i);
        }

        public BinaryOpContext binaryOp() {
            return getRuleContext(BinaryOpContext.class, 0);
        }

        public UnaryBoolOpContext unaryBoolOp() {
            return getRuleContext(UnaryBoolOpContext.class, 0);
        }

        public List<ExprContext> expr() {
            return getRuleContexts(ExprContext.class);
        }

        public ExprContext expr(int i) {
            return getRuleContext(ExprContext.class, i);
        }

        public FuncCallContext funcCall() {
            return getRuleContext(FuncCallContext.class, 0);
        }

        public KeywordBoolExprContext keywordBoolExpr() {
            return getRuleContext(KeywordBoolExprContext.class, 0);
        }

        public BinaryBoolOpContext binaryBoolOp() {
            return getRuleContext(BinaryBoolOpContext.class, 0);
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
        int _startState = 40;
        enterRecursionRule(_localctx, 40, RULE_expr, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(248);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 26, _ctx)) {
                    case 1: {
                        setState(238);
                        simpleExpr();
                    }
                    break;
                    case 2: {
                        setState(239);
                        simpleExpr();
                        setState(240);
                        binaryOp();
                        setState(241);
                        simpleExpr();
                    }
                    break;
                    case 3: {
                        setState(243);
                        unaryBoolOp();
                        setState(244);
                        expr(3);
                    }
                    break;
                    case 4: {
                        setState(246);
                        funcCall();
                    }
                    break;
                    case 5: {
                        setState(247);
                        keywordBoolExpr();
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(259);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 28, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new ExprContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                setState(250);
                                if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                                setState(252);
                                _la = _input.LA(1);
                                if (_la == NOT) {
                                    {
                                        setState(251);
                                        unaryBoolOp();
                                    }
                                }

                                setState(254);
                                binaryBoolOp();
                                setState(255);
                                expr(5);
                            }
                        }
                    }
                    setState(261);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 28, _ctx);
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

    public static class KeywordBoolExprContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(SwiftSqlParser.IDENTIFIER, 0);
        }

        public InExprContext inExpr() {
            return getRuleContext(InExprContext.class, 0);
        }

        public TerminalNode NOT() {
            return getToken(SwiftSqlParser.NOT, 0);
        }

        public BetweenExprContext betweenExpr() {
            return getRuleContext(BetweenExprContext.class, 0);
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
        enterRule(_localctx, 42, RULE_keywordBoolExpr);
        int _la;
        try {
            setState(278);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 32, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(262);
                    match(IDENTIFIER);
                    setState(264);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(263);
                            match(NOT);
                        }
                    }

                    setState(266);
                    inExpr();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(267);
                    match(IDENTIFIER);
                    setState(269);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(268);
                            match(NOT);
                        }
                    }

                    setState(271);
                    betweenExpr();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(272);
                    match(IDENTIFIER);
                    setState(273);
                    match(IS);
                    setState(275);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(274);
                            match(NOT);
                        }
                    }

                    setState(277);
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

    public static class InExprContext extends ParserRuleContext {
        public TerminalNode IN() {
            return getToken(SwiftSqlParser.IN, 0);
        }

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public List<LiteralContext> literal() {
            return getRuleContexts(LiteralContext.class);
        }

        public LiteralContext literal(int i) {
            return getRuleContext(LiteralContext.class, i);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public InExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_inExpr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterInExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitInExpr(this);
        }
    }

    public final InExprContext inExpr() throws RecognitionException {
        InExprContext _localctx = new InExprContext(_ctx, getState());
        enterRule(_localctx, 44, RULE_inExpr);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(280);
                match(IN);
                setState(281);
                match(L_PAR);
                setState(282);
                literal();
                setState(287);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(283);
                            match(COMMA);
                            setState(284);
                            literal();
                        }
                    }
                    setState(289);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(290);
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

    public static class BetweenExprContext extends ParserRuleContext {
        public TerminalNode BETWEEN() {
            return getToken(SwiftSqlParser.BETWEEN, 0);
        }

        public List<LiteralContext> literal() {
            return getRuleContexts(LiteralContext.class);
        }

        public LiteralContext literal(int i) {
            return getRuleContext(LiteralContext.class, i);
        }

        public TerminalNode AND() {
            return getToken(SwiftSqlParser.AND, 0);
        }

        public BetweenExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_betweenExpr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterBetweenExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitBetweenExpr(this);
        }
    }

    public final BetweenExprContext betweenExpr() throws RecognitionException {
        BetweenExprContext _localctx = new BetweenExprContext(_ctx, getState());
        enterRule(_localctx, 46, RULE_betweenExpr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(292);
                match(BETWEEN);
                setState(293);
                literal();
                setState(294);
                match(AND);
                setState(295);
                literal();
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

    public static class SimpleExprContext extends ParserRuleContext {
        public LiteralContext literal() {
            return getRuleContext(LiteralContext.class, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(SwiftSqlParser.IDENTIFIER, 0);
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
        enterRule(_localctx, 48, RULE_simpleExpr);
        try {
            setState(299);
            switch (_input.LA(1)) {
                case NULL:
                case NUMERIC_LITERAL:
                case STRING_LITERAL:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(297);
                    literal();
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(298);
                    match(IDENTIFIER);
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

    public static class FuncCallContext extends ParserRuleContext {
        public FunctionContext function() {
            return getRuleContext(FunctionContext.class, 0);
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

        public TerminalNode DISTINCT() {
            return getToken(SwiftSqlParser.DISTINCT, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(SwiftSqlParser.COMMA);
        }

        public TerminalNode COMMA(int i) {
            return getToken(SwiftSqlParser.COMMA, i);
        }

        public FuncCallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_funcCall;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterFuncCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitFuncCall(this);
        }
    }

    public final FuncCallContext funcCall() throws RecognitionException {
        FuncCallContext _localctx = new FuncCallContext(_ctx, getState());
        enterRule(_localctx, 50, RULE_funcCall);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(301);
                function();
                setState(302);
                match(L_PAR);
                setState(314);
                _la = _input.LA(1);
                if (_la == DISTINCT || _la == NULL || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & ((1L << (IDENTIFIER - 84)) | (1L << (NUMERIC_LITERAL - 84)) | (1L << (STRING_LITERAL - 84)))) != 0)) {
                    {
                        setState(304);
                        _la = _input.LA(1);
                        if (_la == DISTINCT) {
                            {
                                setState(303);
                                match(DISTINCT);
                            }
                        }

                        setState(306);
                        simpleExpr();
                        setState(311);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(307);
                                    match(COMMA);
                                    setState(308);
                                    simpleExpr();
                                }
                            }
                            setState(313);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(316);
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

    public static class LiteralContext extends ParserRuleContext {
        public TerminalNode NULL() {
            return getToken(SwiftSqlParser.NULL, 0);
        }

        public TerminalNode NUMERIC_LITERAL() {
            return getToken(SwiftSqlParser.NUMERIC_LITERAL, 0);
        }

        public TerminalNode STRING_LITERAL() {
            return getToken(SwiftSqlParser.STRING_LITERAL, 0);
        }

        public LiteralContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_literal;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterLiteral(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitLiteral(this);
        }
    }

    public final LiteralContext literal() throws RecognitionException {
        LiteralContext _localctx = new LiteralContext(_ctx, getState());
        enterRule(_localctx, 52, RULE_literal);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(318);
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

    public static class UnaryBoolOpContext extends ParserRuleContext {
        public TerminalNode NOT() {
            return getToken(SwiftSqlParser.NOT, 0);
        }

        public UnaryBoolOpContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_unaryBoolOp;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterUnaryBoolOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitUnaryBoolOp(this);
        }
    }

    public final UnaryBoolOpContext unaryBoolOp() throws RecognitionException {
        UnaryBoolOpContext _localctx = new UnaryBoolOpContext(_ctx, getState());
        enterRule(_localctx, 54, RULE_unaryBoolOp);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(320);
                match(NOT);
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

    public static class BinaryOpContext extends ParserRuleContext {
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

        public BinaryOpContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_binaryOp;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterBinaryOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitBinaryOp(this);
        }
    }

    public final BinaryOpContext binaryOp() throws RecognitionException {
        BinaryOpContext _localctx = new BinaryOpContext(_ctx, getState());
        enterRule(_localctx, 56, RULE_binaryOp);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(322);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << PLUS) | (1L << MINUS))) != 0))) {
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

    public static class BinaryBoolOpContext extends ParserRuleContext {
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

        public TerminalNode AND() {
            return getToken(SwiftSqlParser.AND, 0);
        }

        public TerminalNode OR() {
            return getToken(SwiftSqlParser.OR, 0);
        }

        public TerminalNode LIKE() {
            return getToken(SwiftSqlParser.LIKE, 0);
        }

        public BinaryBoolOpContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_binaryBoolOp;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).enterBinaryBoolOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SwiftSqlParserListener) ((SwiftSqlParserListener) listener).exitBinaryBoolOp(this);
        }
    }

    public final BinaryBoolOpContext binaryBoolOp() throws RecognitionException {
        BinaryBoolOpContext _localctx = new BinaryBoolOpContext(_ctx, getState());
        enterRule(_localctx, 58, RULE_binaryBoolOp);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(324);
                _la = _input.LA(1);
                if (!(((((_la - 51)) & ~0x3f) == 0 && ((1L << (_la - 51)) & ((1L << (AND - 51)) | (1L << (OR - 51)) | (1L << (LIKE - 51)) | (1L << (EQ - 51)) | (1L << (GREATER - 51)) | (1L << (LESS - 51)) | (1L << (GEQ - 51)) | (1L << (LEQ - 51)) | (1L << (NEQ - 51)))) != 0))) {
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

        public TerminalNode L_PAR() {
            return getToken(SwiftSqlParser.L_PAR, 0);
        }

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public TerminalNode R_PAR() {
            return getToken(SwiftSqlParser.R_PAR, 0);
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
        enterRule(_localctx, 60, RULE_name);
        try {
            setState(331);
            switch (_input.LA(1)) {
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(326);
                    match(IDENTIFIER);
                }
                break;
                case L_PAR:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(327);
                    match(L_PAR);
                    setState(328);
                    name();
                    setState(329);
                    match(R_PAR);
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

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 20:
                return expr_sempred((ExprContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean expr_sempred(ExprContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 4);
        }
        return true;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3Z\u0150\4\2\t\2\4" +
                    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t" +
                    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \3\2" +
                    "\3\2\3\2\3\3\3\3\5\3F\n\3\3\3\3\3\3\3\7\3K\n\3\f\3\16\3N\13\3\3\3\5\3" +
                    "Q\n\3\5\3S\n\3\3\4\3\4\5\4W\n\4\3\5\3\5\3\5\5\5\\\n\5\3\6\3\6\3\6\5\6" +
                    "a\n\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\7\bp\n\b\f\b" +
                    "\16\bs\13\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\5\13}\n\13\3\f\3\f\3\f" +
                    "\3\f\3\r\3\r\5\r\u0085\n\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17" +
                    "\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u009a\n\20\3\20" +
                    "\3\20\3\20\3\20\3\20\3\21\3\21\3\21\7\21\u00a4\n\21\f\21\16\21\u00a7\13" +
                    "\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\5\23\u00b1\n\23\3\23\3\23" +
                    "\3\23\5\23\u00b6\n\23\3\23\3\23\3\23\7\23\u00bb\n\23\f\23\16\23\u00be" +
                    "\13\23\5\23\u00c0\n\23\3\23\3\23\3\23\3\23\5\23\u00c6\n\23\3\23\3\23\3" +
                    "\23\5\23\u00cb\n\23\3\23\3\23\5\23\u00cf\n\23\3\23\3\23\3\23\5\23\u00d4" +
                    "\n\23\3\23\3\23\5\23\u00d8\n\23\3\24\3\24\5\24\u00dc\n\24\3\24\3\24\3" +
                    "\24\5\24\u00e1\n\24\7\24\u00e3\n\24\f\24\16\24\u00e6\13\24\3\25\3\25\3" +
                    "\25\7\25\u00eb\n\25\f\25\16\25\u00ee\13\25\3\26\3\26\3\26\3\26\3\26\3" +
                    "\26\3\26\3\26\3\26\3\26\3\26\5\26\u00fb\n\26\3\26\3\26\5\26\u00ff\n\26" +
                    "\3\26\3\26\3\26\7\26\u0104\n\26\f\26\16\26\u0107\13\26\3\27\3\27\5\27" +
                    "\u010b\n\27\3\27\3\27\3\27\5\27\u0110\n\27\3\27\3\27\3\27\3\27\5\27\u0116" +
                    "\n\27\3\27\5\27\u0119\n\27\3\30\3\30\3\30\3\30\3\30\7\30\u0120\n\30\f" +
                    "\30\16\30\u0123\13\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32\5\32" +
                    "\u012e\n\32\3\33\3\33\3\33\5\33\u0133\n\33\3\33\3\33\3\33\7\33\u0138\n" +
                    "\33\f\33\16\33\u013b\13\33\5\33\u013d\n\33\3\33\3\33\3\34\3\34\3\35\3" +
                    "\35\3\36\3\36\3\37\3\37\3 \3 \3 \3 \3 \5 \u014e\n \3 \3q\3*!\2\4\6\b\n" +
                    "\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>\2\b\3\2\26&\3\2" +
                    "+\61\3\2\13\f\4\2\24\24WX\4\2<=?@\4\2\65\67AF\u015d\2@\3\2\2\2\4R\3\2" +
                    "\2\2\6V\3\2\2\2\b[\3\2\2\2\n`\3\2\2\2\fb\3\2\2\2\16i\3\2\2\2\20t\3\2\2" +
                    "\2\22v\3\2\2\2\24x\3\2\2\2\26~\3\2\2\2\30\u0084\3\2\2\2\32\u0086\3\2\2" +
                    "\2\34\u008c\3\2\2\2\36\u0092\3\2\2\2 \u00a0\3\2\2\2\"\u00a8\3\2\2\2$\u00ae" +
                    "\3\2\2\2&\u00d9\3\2\2\2(\u00e7\3\2\2\2*\u00fa\3\2\2\2,\u0118\3\2\2\2." +
                    "\u011a\3\2\2\2\60\u0126\3\2\2\2\62\u012d\3\2\2\2\64\u012f\3\2\2\2\66\u0140" +
                    "\3\2\2\28\u0142\3\2\2\2:\u0144\3\2\2\2<\u0146\3\2\2\2>\u014d\3\2\2\2@" +
                    "A\5\4\3\2AB\7\2\2\3B\3\3\2\2\2CE\5\6\4\2DF\7P\2\2ED\3\2\2\2EF\3\2\2\2" +
                    "FS\3\2\2\2GL\5\6\4\2HI\7P\2\2IK\5\6\4\2JH\3\2\2\2KN\3\2\2\2LJ\3\2\2\2" +
                    "LM\3\2\2\2MP\3\2\2\2NL\3\2\2\2OQ\7P\2\2PO\3\2\2\2PQ\3\2\2\2QS\3\2\2\2" +
                    "RC\3\2\2\2RG\3\2\2\2S\5\3\2\2\2TW\5\b\5\2UW\5\n\6\2VT\3\2\2\2VU\3\2\2" +
                    "\2W\7\3\2\2\2X\\\5\f\7\2Y\\\5\26\f\2Z\\\5\30\r\2[X\3\2\2\2[Y\3\2\2\2[" +
                    "Z\3\2\2\2\\\t\3\2\2\2]a\5\36\20\2^a\5\"\22\2_a\5$\23\2`]\3\2\2\2`^\3\2" +
                    "\2\2`_\3\2\2\2a\13\3\2\2\2bc\7\22\2\2cd\7\23\2\2de\5> \2ef\7M\2\2fg\5" +
                    "\16\b\2gh\7N\2\2h\r\3\2\2\2ij\5> \2jq\5\24\13\2kl\7O\2\2lm\5> \2mn\5\24" +
                    "\13\2np\3\2\2\2ok\3\2\2\2ps\3\2\2\2qr\3\2\2\2qo\3\2\2\2r\17\3\2\2\2sq" +
                    "\3\2\2\2tu\t\2\2\2u\21\3\2\2\2vw\t\3\2\2w\23\3\2\2\2x|\5\20\t\2yz\7M\2" +
                    "\2z{\7W\2\2{}\7N\2\2|y\3\2\2\2|}\3\2\2\2}\25\3\2\2\2~\177\7\'\2\2\177" +
                    "\u0080\7\23\2\2\u0080\u0081\5> \2\u0081\27\3\2\2\2\u0082\u0085\5\32\16" +
                    "\2\u0083\u0085\5\34\17\2\u0084\u0082\3\2\2\2\u0084\u0083\3\2\2\2\u0085" +
                    "\31\3\2\2\2\u0086\u0087\7(\2\2\u0087\u0088\7\23\2\2\u0088\u0089\5> \2" +
                    "\u0089\u008a\7)\2\2\u008a\u008b\5\16\b\2\u008b\33\3\2\2\2\u008c\u008d" +
                    "\7(\2\2\u008d\u008e\7\23\2\2\u008e\u008f\5> \2\u008f\u0090\7\'\2\2\u0090" +
                    "\u0091\5(\25\2\u0091\35\3\2\2\2\u0092\u0093\7\16\2\2\u0093\u0094\7\17" +
                    "\2\2\u0094\u0099\5> \2\u0095\u0096\7M\2\2\u0096\u0097\5(\25\2\u0097\u0098" +
                    "\7N\2\2\u0098\u009a\3\2\2\2\u0099\u0095\3\2\2\2\u0099\u009a\3\2\2\2\u009a" +
                    "\u009b\3\2\2\2\u009b\u009c\7\20\2\2\u009c\u009d\7M\2\2\u009d\u009e\5 " +
                    "\21\2\u009e\u009f\7N\2\2\u009f\37\3\2\2\2\u00a0\u00a5\5\66\34\2\u00a1" +
                    "\u00a2\7O\2\2\u00a2\u00a4\5\66\34\2\u00a3\u00a1\3\2\2\2\u00a4\u00a7\3" +
                    "\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6!\3\2\2\2\u00a7\u00a5" +
                    "\3\2\2\2\u00a8\u00a9\7\21\2\2\u00a9\u00aa\7\5\2\2\u00aa\u00ab\5> \2\u00ab" +
                    "\u00ac\7\6\2\2\u00ac\u00ad\5*\26\2\u00ad#\3\2\2\2\u00ae\u00b0\7\3\2\2" +
                    "\u00af\u00b1\7\4\2\2\u00b0\u00af\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00bf" +
                    "\3\2\2\2\u00b2\u00c0\7<\2\2\u00b3\u00b6\5> \2\u00b4\u00b6\5\64\33\2\u00b5" +
                    "\u00b3\3\2\2\2\u00b5\u00b4\3\2\2\2\u00b6\u00bc\3\2\2\2\u00b7\u00b8\7O" +
                    "\2\2\u00b8\u00bb\5> \2\u00b9\u00bb\5\64\33\2\u00ba\u00b7\3\2\2\2\u00ba" +
                    "\u00b9\3\2\2\2\u00bb\u00be\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2" +
                    "\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00bf\u00b2\3\2\2\2\u00bf" +
                    "\u00b5\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1\u00c2\7\5\2\2\u00c2\u00c5\5>" +
                    " \2\u00c3\u00c4\7\6\2\2\u00c4\u00c6\5*\26\2\u00c5\u00c3\3\2\2\2\u00c5" +
                    "\u00c6\3\2\2\2\u00c6\u00ca\3\2\2\2\u00c7\u00c8\7\7\2\2\u00c8\u00c9\7\b" +
                    "\2\2\u00c9\u00cb\5(\25\2\u00ca\u00c7\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb" +
                    "\u00ce\3\2\2\2\u00cc\u00cd\7\t\2\2\u00cd\u00cf\5*\26\2\u00ce\u00cc\3\2" +
                    "\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d3\3\2\2\2\u00d0\u00d1\7\n\2\2\u00d1" +
                    "\u00d2\7\b\2\2\u00d2\u00d4\5&\24\2\u00d3\u00d0\3\2\2\2\u00d3\u00d4\3\2" +
                    "\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d6\7\r\2\2\u00d6\u00d8\7W\2\2\u00d7" +
                    "\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8%\3\2\2\2\u00d9\u00db\5> \2\u00da" +
                    "\u00dc\t\4\2\2\u00db\u00da\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc\u00e4\3\2" +
                    "\2\2\u00dd\u00de\7O\2\2\u00de\u00e0\5> \2\u00df\u00e1\t\4\2\2\u00e0\u00df" +
                    "\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e3\3\2\2\2\u00e2\u00dd\3\2\2\2\u00e3" +
                    "\u00e6\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\'\3\2\2\2" +
                    "\u00e6\u00e4\3\2\2\2\u00e7\u00ec\5> \2\u00e8\u00e9\7O\2\2\u00e9\u00eb" +
                    "\5> \2\u00ea\u00e8\3\2\2\2\u00eb\u00ee\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ec" +
                    "\u00ed\3\2\2\2\u00ed)\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ef\u00f0\b\26\1\2" +
                    "\u00f0\u00fb\5\62\32\2\u00f1\u00f2\5\62\32\2\u00f2\u00f3\5:\36\2\u00f3" +
                    "\u00f4\5\62\32\2\u00f4\u00fb\3\2\2\2\u00f5\u00f6\58\35\2\u00f6\u00f7\5" +
                    "*\26\5\u00f7\u00fb\3\2\2\2\u00f8\u00fb\5\64\33\2\u00f9\u00fb\5,\27\2\u00fa" +
                    "\u00ef\3\2\2\2\u00fa\u00f1\3\2\2\2\u00fa\u00f5\3\2\2\2\u00fa\u00f8\3\2" +
                    "\2\2\u00fa\u00f9\3\2\2\2\u00fb\u0105\3\2\2\2\u00fc\u00fe\f\6\2\2\u00fd" +
                    "\u00ff\58\35\2\u00fe\u00fd\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0100\3\2" +
                    "\2\2\u0100\u0101\5<\37\2\u0101\u0102\5*\26\7\u0102\u0104\3\2\2\2\u0103" +
                    "\u00fc\3\2\2\2\u0104\u0107\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2" +
                    "\2\2\u0106+\3\2\2\2\u0107\u0105\3\2\2\2\u0108\u010a\7V\2\2\u0109\u010b" +
                    "\7\62\2\2\u010a\u0109\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010c\3\2\2\2" +
                    "\u010c\u0119\5.\30\2\u010d\u010f\7V\2\2\u010e\u0110\7\62\2\2\u010f\u010e" +
                    "\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0119\5\60\31\2" +
                    "\u0112\u0113\7V\2\2\u0113\u0115\78\2\2\u0114\u0116\7\62\2\2\u0115\u0114" +
                    "\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0119\7\24\2\2" +
                    "\u0118\u0108\3\2\2\2\u0118\u010d\3\2\2\2\u0118\u0112\3\2\2\2\u0119-\3" +
                    "\2\2\2\u011a\u011b\7\63\2\2\u011b\u011c\7M\2\2\u011c\u0121\5\66\34\2\u011d" +
                    "\u011e\7O\2\2\u011e\u0120\5\66\34\2\u011f\u011d\3\2\2\2\u0120\u0123\3" +
                    "\2\2\2\u0121\u011f\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0124\3\2\2\2\u0123" +
                    "\u0121\3\2\2\2\u0124\u0125\7N\2\2\u0125/\3\2\2\2\u0126\u0127\7\64\2\2" +
                    "\u0127\u0128\5\66\34\2\u0128\u0129\7\65\2\2\u0129\u012a\5\66\34\2\u012a" +
                    "\61\3\2\2\2\u012b\u012e\5\66\34\2\u012c\u012e\7V\2\2\u012d\u012b\3\2\2" +
                    "\2\u012d\u012c\3\2\2\2\u012e\63\3\2\2\2\u012f\u0130\5\22\n\2\u0130\u013c" +
                    "\7M\2\2\u0131\u0133\7\4\2\2\u0132\u0131\3\2\2\2\u0132\u0133\3\2\2\2\u0133" +
                    "\u0134\3\2\2\2\u0134\u0139\5\62\32\2\u0135\u0136\7O\2\2\u0136\u0138\5" +
                    "\62\32\2\u0137\u0135\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137\3\2\2\2\u0139" +
                    "\u013a\3\2\2\2\u013a\u013d\3\2\2\2\u013b\u0139\3\2\2\2\u013c\u0132\3\2" +
                    "\2\2\u013c\u013d\3\2\2\2\u013d\u013e\3\2\2\2\u013e\u013f\7N\2\2\u013f" +
                    "\65\3\2\2\2\u0140\u0141\t\5\2\2\u0141\67\3\2\2\2\u0142\u0143\7\62\2\2" +
                    "\u01439\3\2\2\2\u0144\u0145\t\6\2\2\u0145;\3\2\2\2\u0146\u0147\t\7\2\2" +
                    "\u0147=\3\2\2\2\u0148\u014e\7V\2\2\u0149\u014a\7M\2\2\u014a\u014b\5> " +
                    "\2\u014b\u014c\7N\2\2\u014c\u014e\3\2\2\2\u014d\u0148\3\2\2\2\u014d\u0149" +
                    "\3\2\2\2\u014e?\3\2\2\2)ELPRV[`q|\u0084\u0099\u00a5\u00b0\u00b5\u00ba" +
                    "\u00bc\u00bf\u00c5\u00ca\u00ce\u00d3\u00d7\u00db\u00e0\u00e4\u00ec\u00fa" +
                    "\u00fe\u0105\u010a\u010f\u0115\u0118\u0121\u012d\u0132\u0139\u013c\u014d";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}