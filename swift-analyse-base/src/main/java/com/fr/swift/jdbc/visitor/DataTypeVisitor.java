package com.fr.swift.jdbc.visitor;

import com.fr.swift.jdbc.antlr4.SwiftSqlParser;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ErrorNodeImpl;
import org.antlr.v4.runtime.tree.RuleNode;

import java.sql.Types;

/**
 * @author yee
 * @date 2019-07-19
 */
public class DataTypeVisitor extends AbstractParseTreeVisitor<Integer> {
    @Override
    public Integer visitChildren(RuleNode node) {
        if (node instanceof SwiftSqlParser.DataTypeContext) {
            SwiftSqlParser.DataTypeContext dataType = (SwiftSqlParser.DataTypeContext) node;
            switch (dataType.getStart().getType()) {
                case SwiftSqlParser.BIT:
                    return Types.BIT;

                case SwiftSqlParser.TINYINT:
                    return Types.TINYINT;

                case SwiftSqlParser.SMALLINT:
                    return Types.SMALLINT;

                case SwiftSqlParser.INTEGER:
                    return Types.INTEGER;

                case SwiftSqlParser.BIGINT:
                    return Types.BIGINT;

                case SwiftSqlParser.FLOAT:
                    return Types.FLOAT;

                case SwiftSqlParser.REAL:
                    return Types.REAL;

                case SwiftSqlParser.DOUBLE:
                    return Types.DOUBLE;

                case SwiftSqlParser.NUMERIC:
                    return Types.NUMERIC;

                case SwiftSqlParser.DECIMAL:
                    return Types.DECIMAL;

                case SwiftSqlParser.CHAR:
                    return Types.CHAR;

                case SwiftSqlParser.VARCHAR:
                    return Types.VARCHAR;

                case SwiftSqlParser.LONGVARCHAR:
                    return Types.LONGVARCHAR;

                case SwiftSqlParser.DATE:
                    return Types.DATE;

                case SwiftSqlParser.TIME:
                    return Types.TIME;

                case SwiftSqlParser.TIMESTAMP:
                    return Types.TIMESTAMP;

                case SwiftSqlParser.BOOLEAN:
                    return Types.BOOLEAN;

                default:
                    visitErrorNode(new ErrorNodeImpl(dataType.start));
            }
        }
        throw new RuntimeException();
    }
}
