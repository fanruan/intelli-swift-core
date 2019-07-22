package com.fr.swift.jdbc.antlr4;

import com.fr.swift.util.Strings;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * @author yee
 * @date 2019-07-20
 */
public class SwiftSqlParseUtil {
    public static void parse(String sql, SwiftSqlParserBaseListener listener) {
        SwiftSqlParser parser = getSwiftSqlParser(sql);
        ParseTreeWalker.DEFAULT.walk(listener, parser.root());
    }

    private static SwiftSqlParser getSwiftSqlParser(String sql) {
        BaseErrorListener error = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new RuntimeException("line " + line + ":" + charPositionInLine + " " + msg);
            }
        };
        SwiftSqlLexer swiftSqlLexer = new SwiftSqlLexer(new ANTLRInputStream(sql));
        swiftSqlLexer.removeErrorListeners();
        swiftSqlLexer.addErrorListener(error);
        CommonTokenStream commonTokenStream = new CommonTokenStream(swiftSqlLexer);
        SwiftSqlParser parser = new SwiftSqlParser(commonTokenStream);
        parser.removeErrorListeners();
        parser.addErrorListener(error);
        return parser;
    }

    public static boolean isSelect(String sql) {
        SwiftSqlParser parser = getSwiftSqlParser(sql);
        final boolean[] select = new boolean[1];
        SwiftSqlParserBaseListener listener = new SwiftSqlParserBaseListener() {
            @Override
            public void exitSelect(SwiftSqlParser.SelectContext ctx) {
                select[0] = true;
            }
        };
        ParseTreeWalker.DEFAULT.walk(listener, parser.root());
        return select[0];
    }

    public static String trimQuote(String value, String quote) {
        if (Strings.isEmpty(value) || value.length() < 2) {
            return value;
        }
        if (value.startsWith(quote) && value.endsWith(quote)) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
