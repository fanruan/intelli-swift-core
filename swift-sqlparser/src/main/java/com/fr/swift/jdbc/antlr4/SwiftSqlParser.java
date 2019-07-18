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
            RULE_createTable = 5, RULE_columnDefinitions = 6, RULE_columnDefinition = 7,
            RULE_dataType = 8, RULE_dropTable = 9, RULE_alterTable = 10, RULE_alterTableAddColumn = 11,
            RULE_alterTableDropColumn = 12, RULE_insert = 13, RULE_delete = 14, RULE_select = 15,
            RULE_columns = 16, RULE_orderBy = 17, RULE_expr = 18, RULE_simpleExpr = 19,
            RULE_funcExpr = 20, RULE_funcName = 21, RULE_keywordBoolExpr = 22, RULE_boolExpr = 23,
            RULE_op = 24, RULE_boolOp = 25, RULE_logicOp = 26, RULE_name = 27, RULE_names = 28,
            RULE_value = 29, RULE_values = 30;
    public static final String[] ruleNames = {
            "root", "sqls", "sql", "ddl", "dml", "createTable", "columnDefinitions",
            "columnDefinition", "dataType", "dropTable", "alterTable", "alterTableAddColumn",
            "alterTableDropColumn", "insert", "delete", "select", "columns", "orderBy",
            "expr", "simpleExpr", "funcExpr", "funcName", "keywordBoolExpr", "boolExpr",
            "op", "boolOp", "logicOp", "name", "names", "value", "values"
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
            enterOuterAlt(_localctx, 1);
            {
                setState(65);
                sql();
                setState(70);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(66);
                                match(SEMI);
                                setState(67);
                                sql();
                            }
                        }
                    }
                    setState(72);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                }
                setState(74);
                _la = _input.LA(1);
                if (_la == SEMI) {
                    {
                        setState(73);
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
            setState(78);
            switch (_input.LA(1)) {
                case CREATE:
                case DROP:
                case ALTER:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(76);
                    ddl();
                }
                break;
                case SELECT:
                case INSERT:
                case DELETE:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(77);
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
            setState(83);
            switch (_input.LA(1)) {
                case CREATE:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(80);
                    createTable();
                }
                break;
                case DROP:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(81);
                    dropTable();
                }
                break;
                case ALTER:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(82);
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
            setState(88);
            switch (_input.LA(1)) {
                case INSERT:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(85);
                    insert();
                }
                break;
                case DELETE:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(86);
                    delete();
                }
                break;
                case SELECT:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(87);
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
                setState(90);
                match(CREATE);
                setState(91);
                match(TABLE);
                setState(92);
                ((CreateTableContext) _localctx).table = name();
                setState(93);
                match(L_PAR);
                setState(94);
                columnDefinitions();
                setState(95);
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
                setState(97);
                name();
                setState(98);
                columnDefinition();
                setState(105);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                while (_alt != 1 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1 + 1) {
                        {
                            {
                                setState(99);
                                match(COMMA);
                                setState(100);
                                name();
                                setState(101);
                                columnDefinition();
                            }
                        }
                    }
                    setState(107);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
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
                setState(108);
                dataType();
                setState(112);
                _la = _input.LA(1);
                if (_la == L_PAR) {
                    {
                        setState(109);
                        match(L_PAR);
                        setState(110);
                        ((ColumnDefinitionContext) _localctx).length = match(NUMERIC_LITERAL);
                        setState(111);
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
                setState(116);
                match(DROP);
                setState(117);
                match(TABLE);
                setState(118);
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
            setState(122);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 7, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(120);
                    alterTableAddColumn();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(121);
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
                setState(124);
                match(ALTER);
                setState(125);
                match(TABLE);
                setState(126);
                ((AlterTableAddColumnContext) _localctx).table = name();
                setState(127);
                match(ADD);
                setState(128);
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
                setState(130);
                match(ALTER);
                setState(131);
                match(TABLE);
                setState(132);
                ((AlterTableDropColumnContext) _localctx).table = name();
                setState(133);
                match(DROP);
                setState(134);
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
        enterRule(_localctx, 26, RULE_insert);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(136);
                match(INSERT);
                setState(137);
                match(INTO);
                setState(138);
                ((InsertContext) _localctx).table = name();
                setState(143);
                _la = _input.LA(1);
                if (_la == L_PAR) {
                    {
                        setState(139);
                        match(L_PAR);
                        setState(140);
                        ((InsertContext) _localctx).columnNames = names();
                        setState(141);
                        match(R_PAR);
                    }
                }

                setState(145);
                match(VALUES);
                setState(146);
                match(L_PAR);
                setState(147);
                values();
                setState(148);
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
                setState(150);
                match(DELETE);
                setState(151);
                match(FROM);
                setState(152);
                ((DeleteContext) _localctx).table = name();
                setState(153);
                match(WHERE);
                setState(154);
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
        public NameContext table;
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

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
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
        enterRule(_localctx, 30, RULE_select);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(156);
                match(SELECT);
                setState(158);
                _la = _input.LA(1);
                if (_la == DISTINCT) {
                    {
                        setState(157);
                        match(DISTINCT);
                    }
                }

                setState(160);
                columns();
                setState(161);
                match(FROM);
                setState(162);
                ((SelectContext) _localctx).table = name();
                setState(165);
                _la = _input.LA(1);
                if (_la == WHERE) {
                    {
                        setState(163);
                        match(WHERE);
                        setState(164);
                        ((SelectContext) _localctx).where = expr(0);
                    }
                }

                setState(170);
                _la = _input.LA(1);
                if (_la == GROUP) {
                    {
                        setState(167);
                        match(GROUP);
                        setState(168);
                        match(BY);
                        setState(169);
                        ((SelectContext) _localctx).groupBy = names();
                    }
                }

                setState(174);
                _la = _input.LA(1);
                if (_la == HAVING) {
                    {
                        setState(172);
                        match(HAVING);
                        setState(173);
                        ((SelectContext) _localctx).having = expr(0);
                    }
                }

                setState(179);
                _la = _input.LA(1);
                if (_la == ORDER) {
                    {
                        setState(176);
                        match(ORDER);
                        setState(177);
                        match(BY);
                        setState(178);
                        orderBy();
                    }
                }

                setState(183);
                _la = _input.LA(1);
                if (_la == LIMIT) {
                    {
                        setState(181);
                        match(LIMIT);
                        setState(182);
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
        enterRule(_localctx, 32, RULE_columns);
        int _la;
        try {
            setState(194);
            switch (_input.LA(1)) {
                case MUL:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(185);
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
                    setState(186);
                    simpleExpr();
                    setState(191);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                    while (_la == COMMA) {
                        {
                            {
                                setState(187);
                                match(COMMA);
                                setState(188);
                                simpleExpr();
                            }
                        }
                        setState(193);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
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
        enterRule(_localctx, 34, RULE_orderBy);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(196);
                name();
                setState(198);
                _la = _input.LA(1);
                if (_la == ASC || _la == DESC) {
                    {
                        setState(197);
                        _la = _input.LA(1);
                        if (!(_la == ASC || _la == DESC)) {
                            _errHandler.recoverInline(this);
                        } else {
                            consume();
                        }
                    }
                }

                setState(207);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(200);
                            match(COMMA);
                            setState(201);
                            name();
                            setState(203);
                            _la = _input.LA(1);
                            if (_la == ASC || _la == DESC) {
                                {
                                    setState(202);
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
                    setState(209);
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
        int _startState = 36;
        enterRecursionRule(_localctx, 36, RULE_expr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(229);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 20, _ctx)) {
                    case 1: {
                        setState(211);
                        simpleExpr();
                    }
                    break;
                    case 2: {
                        setState(212);
                        simpleExpr();
                        setState(213);
                        op();
                        setState(214);
                        simpleExpr();
                    }
                    break;
                    case 3: {
                        setState(216);
                        match(L_PAR);
                        setState(217);
                        simpleExpr();
                        setState(218);
                        op();
                        setState(219);
                        simpleExpr();
                        setState(220);
                        match(R_PAR);
                    }
                    break;
                    case 4: {
                        setState(222);
                        boolExpr(0);
                    }
                    break;
                    case 5: {
                        setState(223);
                        match(L_PAR);
                        setState(224);
                        expr(0);
                        setState(225);
                        op();
                        setState(226);
                        expr(0);
                        setState(227);
                        match(R_PAR);
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(237);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 21, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new ExprContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_expr);
                                setState(231);
                                if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                setState(232);
                                op();
                                setState(233);
                                expr(3);
                            }
                        }
                    }
                    setState(239);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 21, _ctx);
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
        enterRule(_localctx, 38, RULE_simpleExpr);
        try {
            setState(243);
            switch (_input.LA(1)) {
                case NULL:
                case NUMERIC_LITERAL:
                case STRING_LITERAL:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(240);
                    value();
                }
                break;
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(241);
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
                    setState(242);
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
        enterRule(_localctx, 40, RULE_funcExpr);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(245);
                funcName();
                setState(246);
                match(L_PAR);
                setState(255);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NULL) | (1L << MAX) | (1L << MIN) | (1L << SUM) | (1L << AVG) | (1L << COUNT) | (1L << MID) | (1L << TODATE))) != 0) || ((((_la - 84)) & ~0x3f) == 0 && ((1L << (_la - 84)) & ((1L << (IDENTIFIER - 84)) | (1L << (NUMERIC_LITERAL - 84)) | (1L << (STRING_LITERAL - 84)))) != 0)) {
                    {
                        setState(247);
                        simpleExpr();
                        setState(252);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == COMMA) {
                            {
                                {
                                    setState(248);
                                    match(COMMA);
                                    setState(249);
                                    simpleExpr();
                                }
                            }
                            setState(254);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

                setState(257);
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
        enterRule(_localctx, 42, RULE_funcName);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(259);
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
        enterRule(_localctx, 44, RULE_keywordBoolExpr);
        int _la;
        try {
            setState(286);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 28, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(261);
                    simpleExpr();
                    setState(263);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(262);
                            match(NOT);
                        }
                    }

                    setState(265);
                    match(IN);
                    setState(266);
                    match(L_PAR);
                    setState(267);
                    values();
                    setState(268);
                    match(R_PAR);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(270);
                    simpleExpr();
                    setState(272);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(271);
                            match(NOT);
                        }
                    }

                    setState(274);
                    match(BETWEEN);
                    setState(275);
                    match(NUMERIC_LITERAL);
                    setState(276);
                    match(AND);
                    setState(277);
                    match(NUMERIC_LITERAL);
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(279);
                    simpleExpr();
                    setState(280);
                    match(IS);
                    setState(282);
                    _la = _input.LA(1);
                    if (_la == NOT) {
                        {
                            setState(281);
                            match(NOT);
                        }
                    }

                    setState(284);
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
        int _startState = 46;
        enterRecursionRule(_localctx, 46, RULE_boolExpr, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(308);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 29, _ctx)) {
                    case 1: {
                        setState(289);
                        simpleExpr();
                        setState(290);
                        boolOp();
                        setState(291);
                        simpleExpr();
                    }
                    break;
                    case 2: {
                        setState(293);
                        match(L_PAR);
                        setState(294);
                        simpleExpr();
                        setState(295);
                        boolOp();
                        setState(296);
                        simpleExpr();
                        setState(297);
                        match(R_PAR);
                    }
                    break;
                    case 3: {
                        setState(299);
                        keywordBoolExpr();
                    }
                    break;
                    case 4: {
                        setState(300);
                        match(NOT);
                        setState(301);
                        boolExpr(3);
                    }
                    break;
                    case 5: {
                        setState(302);
                        match(L_PAR);
                        setState(303);
                        boolExpr(0);
                        setState(304);
                        logicOp();
                        setState(305);
                        boolExpr(0);
                        setState(306);
                        match(R_PAR);
                    }
                    break;
                }
                _ctx.stop = _input.LT(-1);
                setState(316);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 30, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new BoolExprContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_boolExpr);
                                setState(310);
                                if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                setState(311);
                                logicOp();
                                setState(312);
                                boolExpr(3);
                            }
                        }
                    }
                    setState(318);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 30, _ctx);
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
        enterRule(_localctx, 48, RULE_op);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(319);
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
        enterRule(_localctx, 50, RULE_boolOp);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(321);
                _la = _input.LA(1);
                if (!(((((_la - 53)) & ~0x3f) == 0 && ((1L << (_la - 53)) & ((1L << (LIKE - 53)) | (1L << (EQ - 53)) | (1L << (GREATER - 53)) | (1L << (LESS - 53)) | (1L << (GEQ - 53)) | (1L << (LEQ - 53)) | (1L << (NEQ - 53)))) != 0))) {
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
        enterRule(_localctx, 52, RULE_logicOp);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(323);
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
        enterRule(_localctx, 54, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(325);
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
        enterRule(_localctx, 56, RULE_names);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(327);
                name();
                setState(332);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(328);
                            match(COMMA);
                            setState(329);
                            name();
                        }
                    }
                    setState(334);
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
        enterRule(_localctx, 58, RULE_value);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(335);
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
        enterRule(_localctx, 60, RULE_values);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(337);
                value();
                setState(342);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == COMMA) {
                    {
                        {
                            setState(338);
                            match(COMMA);
                            setState(339);
                            value();
                        }
                    }
                    setState(344);
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

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 18:
                return expr_sempred((ExprContext) _localctx, predIndex);
            case 23:
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
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3Z\u015c\4\2\t\2\4" +
                    "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t" +
                    "\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22" +
                    "\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31" +
                    "\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \3\2" +
                    "\3\2\3\2\3\3\3\3\3\3\7\3G\n\3\f\3\16\3J\13\3\3\3\5\3M\n\3\3\4\3\4\5\4" +
                    "Q\n\4\3\5\3\5\3\5\5\5V\n\5\3\6\3\6\3\6\5\6[\n\6\3\7\3\7\3\7\3\7\3\7\3" +
                    "\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\7\bj\n\b\f\b\16\bm\13\b\3\t\3\t\3\t\3\t" +
                    "\5\ts\n\t\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\5\f}\n\f\3\r\3\r\3\r\3\r" +
                    "\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3" +
                    "\17\5\17\u0092\n\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20" +
                    "\3\20\3\21\3\21\5\21\u00a1\n\21\3\21\3\21\3\21\3\21\3\21\5\21\u00a8\n" +
                    "\21\3\21\3\21\3\21\5\21\u00ad\n\21\3\21\3\21\5\21\u00b1\n\21\3\21\3\21" +
                    "\3\21\5\21\u00b6\n\21\3\21\3\21\5\21\u00ba\n\21\3\22\3\22\3\22\3\22\7" +
                    "\22\u00c0\n\22\f\22\16\22\u00c3\13\22\5\22\u00c5\n\22\3\23\3\23\5\23\u00c9" +
                    "\n\23\3\23\3\23\3\23\5\23\u00ce\n\23\7\23\u00d0\n\23\f\23\16\23\u00d3" +
                    "\13\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24" +
                    "\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u00e8\n\24\3\24\3\24\3\24\3\24\7\24" +
                    "\u00ee\n\24\f\24\16\24\u00f1\13\24\3\25\3\25\3\25\5\25\u00f6\n\25\3\26" +
                    "\3\26\3\26\3\26\3\26\7\26\u00fd\n\26\f\26\16\26\u0100\13\26\5\26\u0102" +
                    "\n\26\3\26\3\26\3\27\3\27\3\30\3\30\5\30\u010a\n\30\3\30\3\30\3\30\3\30" +
                    "\3\30\3\30\3\30\5\30\u0113\n\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30" +
                    "\5\30\u011d\n\30\3\30\3\30\5\30\u0121\n\30\3\31\3\31\3\31\3\31\3\31\3" +
                    "\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3" +
                    "\31\5\31\u0137\n\31\3\31\3\31\3\31\3\31\7\31\u013d\n\31\f\31\16\31\u0140" +
                    "\13\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\36\7\36\u014d" +
                    "\n\36\f\36\16\36\u0150\13\36\3\37\3\37\3 \3 \3 \7 \u0157\n \f \16 \u015a" +
                    "\13 \3 \3k\4&\60!\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62" +
                    "\64\668:<>\2\t\3\2\26&\3\2\13\f\3\2+\61\4\2<=?@\4\2\67\67AF\3\2\65\66" +
                    "\4\2\24\24WX\u0167\2@\3\2\2\2\4C\3\2\2\2\6P\3\2\2\2\bU\3\2\2\2\nZ\3\2" +
                    "\2\2\f\\\3\2\2\2\16c\3\2\2\2\20n\3\2\2\2\22t\3\2\2\2\24v\3\2\2\2\26|\3" +
                    "\2\2\2\30~\3\2\2\2\32\u0084\3\2\2\2\34\u008a\3\2\2\2\36\u0098\3\2\2\2" +
                    " \u009e\3\2\2\2\"\u00c4\3\2\2\2$\u00c6\3\2\2\2&\u00e7\3\2\2\2(\u00f5\3" +
                    "\2\2\2*\u00f7\3\2\2\2,\u0105\3\2\2\2.\u0120\3\2\2\2\60\u0136\3\2\2\2\62" +
                    "\u0141\3\2\2\2\64\u0143\3\2\2\2\66\u0145\3\2\2\28\u0147\3\2\2\2:\u0149" +
                    "\3\2\2\2<\u0151\3\2\2\2>\u0153\3\2\2\2@A\5\4\3\2AB\7\2\2\3B\3\3\2\2\2" +
                    "CH\5\6\4\2DE\7P\2\2EG\5\6\4\2FD\3\2\2\2GJ\3\2\2\2HF\3\2\2\2HI\3\2\2\2" +
                    "IL\3\2\2\2JH\3\2\2\2KM\7P\2\2LK\3\2\2\2LM\3\2\2\2M\5\3\2\2\2NQ\5\b\5\2" +
                    "OQ\5\n\6\2PN\3\2\2\2PO\3\2\2\2Q\7\3\2\2\2RV\5\f\7\2SV\5\24\13\2TV\5\26" +
                    "\f\2UR\3\2\2\2US\3\2\2\2UT\3\2\2\2V\t\3\2\2\2W[\5\34\17\2X[\5\36\20\2" +
                    "Y[\5 \21\2ZW\3\2\2\2ZX\3\2\2\2ZY\3\2\2\2[\13\3\2\2\2\\]\7\22\2\2]^\7\23" +
                    "\2\2^_\58\35\2_`\7M\2\2`a\5\16\b\2ab\7N\2\2b\r\3\2\2\2cd\58\35\2dk\5\20" +
                    "\t\2ef\7O\2\2fg\58\35\2gh\5\20\t\2hj\3\2\2\2ie\3\2\2\2jm\3\2\2\2kl\3\2" +
                    "\2\2ki\3\2\2\2l\17\3\2\2\2mk\3\2\2\2nr\5\22\n\2op\7M\2\2pq\7W\2\2qs\7" +
                    "N\2\2ro\3\2\2\2rs\3\2\2\2s\21\3\2\2\2tu\t\2\2\2u\23\3\2\2\2vw\7\'\2\2" +
                    "wx\7\23\2\2xy\58\35\2y\25\3\2\2\2z}\5\30\r\2{}\5\32\16\2|z\3\2\2\2|{\3" +
                    "\2\2\2}\27\3\2\2\2~\177\7(\2\2\177\u0080\7\23\2\2\u0080\u0081\58\35\2" +
                    "\u0081\u0082\7)\2\2\u0082\u0083\5\16\b\2\u0083\31\3\2\2\2\u0084\u0085" +
                    "\7(\2\2\u0085\u0086\7\23\2\2\u0086\u0087\58\35\2\u0087\u0088\7\'\2\2\u0088" +
                    "\u0089\5:\36\2\u0089\33\3\2\2\2\u008a\u008b\7\16\2\2\u008b\u008c\7\17" +
                    "\2\2\u008c\u0091\58\35\2\u008d\u008e\7M\2\2\u008e\u008f\5:\36\2\u008f" +
                    "\u0090\7N\2\2\u0090\u0092\3\2\2\2\u0091\u008d\3\2\2\2\u0091\u0092\3\2" +
                    "\2\2\u0092\u0093\3\2\2\2\u0093\u0094\7\20\2\2\u0094\u0095\7M\2\2\u0095" +
                    "\u0096\5> \2\u0096\u0097\7N\2\2\u0097\35\3\2\2\2\u0098\u0099\7\21\2\2" +
                    "\u0099\u009a\7\5\2\2\u009a\u009b\58\35\2\u009b\u009c\7\6\2\2\u009c\u009d" +
                    "\5&\24\2\u009d\37\3\2\2\2\u009e\u00a0\7\3\2\2\u009f\u00a1\7\4\2\2\u00a0" +
                    "\u009f\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\5\"" +
                    "\22\2\u00a3\u00a4\7\5\2\2\u00a4\u00a7\58\35\2\u00a5\u00a6\7\6\2\2\u00a6" +
                    "\u00a8\5&\24\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00ac\3\2" +
                    "\2\2\u00a9\u00aa\7\7\2\2\u00aa\u00ab\7\b\2\2\u00ab\u00ad\5:\36\2\u00ac" +
                    "\u00a9\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00af\7\t" +
                    "\2\2\u00af\u00b1\5&\24\2\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1" +
                    "\u00b5\3\2\2\2\u00b2\u00b3\7\n\2\2\u00b3\u00b4\7\b\2\2\u00b4\u00b6\5$" +
                    "\23\2\u00b5\u00b2\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b9\3\2\2\2\u00b7" +
                    "\u00b8\7\r\2\2\u00b8\u00ba\7W\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2" +
                    "\2\2\u00ba!\3\2\2\2\u00bb\u00c5\7<\2\2\u00bc\u00c1\5(\25\2\u00bd\u00be" +
                    "\7O\2\2\u00be\u00c0\5(\25\2\u00bf\u00bd\3\2\2\2\u00c0\u00c3\3\2\2\2\u00c1" +
                    "\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3\u00c1\3\2" +
                    "\2\2\u00c4\u00bb\3\2\2\2\u00c4\u00bc\3\2\2\2\u00c5#\3\2\2\2\u00c6\u00c8" +
                    "\58\35\2\u00c7\u00c9\t\3\2\2\u00c8\u00c7\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9" +
                    "\u00d1\3\2\2\2\u00ca\u00cb\7O\2\2\u00cb\u00cd\58\35\2\u00cc\u00ce\t\3" +
                    "\2\2\u00cd\u00cc\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00d0\3\2\2\2\u00cf" +
                    "\u00ca\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2" +
                    "\2\2\u00d2%\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d4\u00d5\b\24\1\2\u00d5\u00e8" +
                    "\5(\25\2\u00d6\u00d7\5(\25\2\u00d7\u00d8\5\62\32\2\u00d8\u00d9\5(\25\2" +
                    "\u00d9\u00e8\3\2\2\2\u00da\u00db\7M\2\2\u00db\u00dc\5(\25\2\u00dc\u00dd" +
                    "\5\62\32\2\u00dd\u00de\5(\25\2\u00de\u00df\7N\2\2\u00df\u00e8\3\2\2\2" +
                    "\u00e0\u00e8\5\60\31\2\u00e1\u00e2\7M\2\2\u00e2\u00e3\5&\24\2\u00e3\u00e4" +
                    "\5\62\32\2\u00e4\u00e5\5&\24\2\u00e5\u00e6\7N\2\2\u00e6\u00e8\3\2\2\2" +
                    "\u00e7\u00d4\3\2\2\2\u00e7\u00d6\3\2\2\2\u00e7\u00da\3\2\2\2\u00e7\u00e0" +
                    "\3\2\2\2\u00e7\u00e1\3\2\2\2\u00e8\u00ef\3\2\2\2\u00e9\u00ea\f\4\2\2\u00ea" +
                    "\u00eb\5\62\32\2\u00eb\u00ec\5&\24\5\u00ec\u00ee\3\2\2\2\u00ed\u00e9\3" +
                    "\2\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0" +
                    "\'\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2\u00f6\5<\37\2\u00f3\u00f6\58\35\2" +
                    "\u00f4\u00f6\5*\26\2\u00f5\u00f2\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5\u00f4" +
                    "\3\2\2\2\u00f6)\3\2\2\2\u00f7\u00f8\5,\27\2\u00f8\u0101\7M\2\2\u00f9\u00fe" +
                    "\5(\25\2\u00fa\u00fb\7O\2\2\u00fb\u00fd\5(\25\2\u00fc\u00fa\3\2\2\2\u00fd" +
                    "\u0100\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0102\3\2" +
                    "\2\2\u0100\u00fe\3\2\2\2\u0101\u00f9\3\2\2\2\u0101\u0102\3\2\2\2\u0102" +
                    "\u0103\3\2\2\2\u0103\u0104\7N\2\2\u0104+\3\2\2\2\u0105\u0106\t\4\2\2\u0106" +
                    "-\3\2\2\2\u0107\u0109\5(\25\2\u0108\u010a\7\62\2\2\u0109\u0108\3\2\2\2" +
                    "\u0109\u010a\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010c\7\63\2\2\u010c\u010d" +
                    "\7M\2\2\u010d\u010e\5> \2\u010e\u010f\7N\2\2\u010f\u0121\3\2\2\2\u0110" +
                    "\u0112\5(\25\2\u0111\u0113\7\62\2\2\u0112\u0111\3\2\2\2\u0112\u0113\3" +
                    "\2\2\2\u0113\u0114\3\2\2\2\u0114\u0115\7\64\2\2\u0115\u0116\7W\2\2\u0116" +
                    "\u0117\7\65\2\2\u0117\u0118\7W\2\2\u0118\u0121\3\2\2\2\u0119\u011a\5(" +
                    "\25\2\u011a\u011c\78\2\2\u011b\u011d\7\62\2\2\u011c\u011b\3\2\2\2\u011c" +
                    "\u011d\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u011f\7\24\2\2\u011f\u0121\3" +
                    "\2\2\2\u0120\u0107\3\2\2\2\u0120\u0110\3\2\2\2\u0120\u0119\3\2\2\2\u0121" +
                    "/\3\2\2\2\u0122\u0123\b\31\1\2\u0123\u0124\5(\25\2\u0124\u0125\5\64\33" +
                    "\2\u0125\u0126\5(\25\2\u0126\u0137\3\2\2\2\u0127\u0128\7M\2\2\u0128\u0129" +
                    "\5(\25\2\u0129\u012a\5\64\33\2\u012a\u012b\5(\25\2\u012b\u012c\7N\2\2" +
                    "\u012c\u0137\3\2\2\2\u012d\u0137\5.\30\2\u012e\u012f\7\62\2\2\u012f\u0137" +
                    "\5\60\31\5\u0130\u0131\7M\2\2\u0131\u0132\5\60\31\2\u0132\u0133\5\66\34" +
                    "\2\u0133\u0134\5\60\31\2\u0134\u0135\7N\2\2\u0135\u0137\3\2\2\2\u0136" +
                    "\u0122\3\2\2\2\u0136\u0127\3\2\2\2\u0136\u012d\3\2\2\2\u0136\u012e\3\2" +
                    "\2\2\u0136\u0130\3\2\2\2\u0137\u013e\3\2\2\2\u0138\u0139\f\4\2\2\u0139" +
                    "\u013a\5\66\34\2\u013a\u013b\5\60\31\5\u013b\u013d\3\2\2\2\u013c\u0138" +
                    "\3\2\2\2\u013d\u0140\3\2\2\2\u013e\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f" +
                    "\61\3\2\2\2\u0140\u013e\3\2\2\2\u0141\u0142\t\5\2\2\u0142\63\3\2\2\2\u0143" +
                    "\u0144\t\6\2\2\u0144\65\3\2\2\2\u0145\u0146\t\7\2\2\u0146\67\3\2\2\2\u0147" +
                    "\u0148\7V\2\2\u01489\3\2\2\2\u0149\u014e\58\35\2\u014a\u014b\7O\2\2\u014b" +
                    "\u014d\58\35\2\u014c\u014a\3\2\2\2\u014d\u0150\3\2\2\2\u014e\u014c\3\2" +
                    "\2\2\u014e\u014f\3\2\2\2\u014f;\3\2\2\2\u0150\u014e\3\2\2\2\u0151\u0152" +
                    "\t\b\2\2\u0152=\3\2\2\2\u0153\u0158\5<\37\2\u0154\u0155\7O\2\2\u0155\u0157" +
                    "\5<\37\2\u0156\u0154\3\2\2\2\u0157\u015a\3\2\2\2\u0158\u0156\3\2\2\2\u0158" +
                    "\u0159\3\2\2\2\u0159?\3\2\2\2\u015a\u0158\3\2\2\2#HLPUZkr|\u0091\u00a0" +
                    "\u00a7\u00ac\u00b0\u00b5\u00b9\u00c1\u00c4\u00c8\u00cd\u00d1\u00e7\u00ef" +
                    "\u00f5\u00fe\u0101\u0109\u0112\u011c\u0120\u0136\u013e\u014e\u0158";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}