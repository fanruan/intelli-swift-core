// Generated from SwiftSqlParser.g4 by ANTLR 4.5.3
package com.fr.swift.jdbc.antlr4;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SwiftSqlParser}.
 */
public interface SwiftSqlParserListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#root}.
     *
     * @param ctx the parse tree
     */
    void enterRoot(SwiftSqlParser.RootContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#root}.
     *
     * @param ctx the parse tree
     */
    void exitRoot(SwiftSqlParser.RootContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#sqls}.
     *
     * @param ctx the parse tree
     */
    void enterSqls(SwiftSqlParser.SqlsContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#sqls}.
     *
     * @param ctx the parse tree
     */
    void exitSqls(SwiftSqlParser.SqlsContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#sql}.
     *
     * @param ctx the parse tree
     */
    void enterSql(SwiftSqlParser.SqlContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#sql}.
     *
     * @param ctx the parse tree
     */
    void exitSql(SwiftSqlParser.SqlContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#ddl}.
     *
     * @param ctx the parse tree
     */
    void enterDdl(SwiftSqlParser.DdlContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#ddl}.
     *
     * @param ctx the parse tree
     */
    void exitDdl(SwiftSqlParser.DdlContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#dml}.
     *
     * @param ctx the parse tree
     */
    void enterDml(SwiftSqlParser.DmlContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#dml}.
     *
     * @param ctx the parse tree
     */
    void exitDml(SwiftSqlParser.DmlContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#createTable}.
     *
     * @param ctx the parse tree
     */
    void enterCreateTable(SwiftSqlParser.CreateTableContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#createTable}.
     *
     * @param ctx the parse tree
     */
    void exitCreateTable(SwiftSqlParser.CreateTableContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#columnDefinitions}.
     *
     * @param ctx the parse tree
     */
    void enterColumnDefinitions(SwiftSqlParser.ColumnDefinitionsContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#columnDefinitions}.
     *
     * @param ctx the parse tree
     */
    void exitColumnDefinitions(SwiftSqlParser.ColumnDefinitionsContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#dataType}.
     *
     * @param ctx the parse tree
     */
    void enterDataType(SwiftSqlParser.DataTypeContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#dataType}.
     *
     * @param ctx the parse tree
     */
    void exitDataType(SwiftSqlParser.DataTypeContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#function}.
     *
     * @param ctx the parse tree
     */
    void enterFunction(SwiftSqlParser.FunctionContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#function}.
     *
     * @param ctx the parse tree
     */
    void exitFunction(SwiftSqlParser.FunctionContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#columnDefinition}.
     *
     * @param ctx the parse tree
     */
    void enterColumnDefinition(SwiftSqlParser.ColumnDefinitionContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#columnDefinition}.
     *
     * @param ctx the parse tree
     */
    void exitColumnDefinition(SwiftSqlParser.ColumnDefinitionContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#dropTable}.
     *
     * @param ctx the parse tree
     */
    void enterDropTable(SwiftSqlParser.DropTableContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#dropTable}.
     *
     * @param ctx the parse tree
     */
    void exitDropTable(SwiftSqlParser.DropTableContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#alterTable}.
     *
     * @param ctx the parse tree
     */
    void enterAlterTable(SwiftSqlParser.AlterTableContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#alterTable}.
     *
     * @param ctx the parse tree
     */
    void exitAlterTable(SwiftSqlParser.AlterTableContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#alterTableAddColumn}.
     *
     * @param ctx the parse tree
     */
    void enterAlterTableAddColumn(SwiftSqlParser.AlterTableAddColumnContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#alterTableAddColumn}.
     *
     * @param ctx the parse tree
     */
    void exitAlterTableAddColumn(SwiftSqlParser.AlterTableAddColumnContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#alterTableDropColumn}.
     *
     * @param ctx the parse tree
     */
    void enterAlterTableDropColumn(SwiftSqlParser.AlterTableDropColumnContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#alterTableDropColumn}.
     *
     * @param ctx the parse tree
     */
    void exitAlterTableDropColumn(SwiftSqlParser.AlterTableDropColumnContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#insert}.
     *
     * @param ctx the parse tree
     */
    void enterInsert(SwiftSqlParser.InsertContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#insert}.
     *
     * @param ctx the parse tree
     */
    void exitInsert(SwiftSqlParser.InsertContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(SwiftSqlParser.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(SwiftSqlParser.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#delete}.
     *
     * @param ctx the parse tree
     */
    void enterDelete(SwiftSqlParser.DeleteContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#delete}.
     *
     * @param ctx the parse tree
     */
    void exitDelete(SwiftSqlParser.DeleteContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#select}.
     *
     * @param ctx the parse tree
     */
    void enterSelect(SwiftSqlParser.SelectContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#select}.
     *
     * @param ctx the parse tree
     */
    void exitSelect(SwiftSqlParser.SelectContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#columnOrders}.
     *
     * @param ctx the parse tree
     */
    void enterColumnOrders(SwiftSqlParser.ColumnOrdersContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#columnOrders}.
     *
     * @param ctx the parse tree
     */
    void exitColumnOrders(SwiftSqlParser.ColumnOrdersContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#names}.
     *
     * @param ctx the parse tree
     */
    void enterNames(SwiftSqlParser.NamesContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#names}.
     *
     * @param ctx the parse tree
     */
    void exitNames(SwiftSqlParser.NamesContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(SwiftSqlParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(SwiftSqlParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#keywordBoolExpr}.
     *
     * @param ctx the parse tree
     */
    void enterKeywordBoolExpr(SwiftSqlParser.KeywordBoolExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#keywordBoolExpr}.
     *
     * @param ctx the parse tree
     */
    void exitKeywordBoolExpr(SwiftSqlParser.KeywordBoolExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#inExpr}.
     *
     * @param ctx the parse tree
     */
    void enterInExpr(SwiftSqlParser.InExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#inExpr}.
     *
     * @param ctx the parse tree
     */
    void exitInExpr(SwiftSqlParser.InExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#betweenExpr}.
     *
     * @param ctx the parse tree
     */
    void enterBetweenExpr(SwiftSqlParser.BetweenExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#betweenExpr}.
     *
     * @param ctx the parse tree
     */
    void exitBetweenExpr(SwiftSqlParser.BetweenExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#simpleExpr}.
     *
     * @param ctx the parse tree
     */
    void enterSimpleExpr(SwiftSqlParser.SimpleExprContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#simpleExpr}.
     *
     * @param ctx the parse tree
     */
    void exitSimpleExpr(SwiftSqlParser.SimpleExprContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#funcCall}.
     *
     * @param ctx the parse tree
     */
    void enterFuncCall(SwiftSqlParser.FuncCallContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#funcCall}.
     *
     * @param ctx the parse tree
     */
    void exitFuncCall(SwiftSqlParser.FuncCallContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#literal}.
     *
     * @param ctx the parse tree
     */
    void enterLiteral(SwiftSqlParser.LiteralContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#literal}.
     *
     * @param ctx the parse tree
     */
    void exitLiteral(SwiftSqlParser.LiteralContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#unaryBoolOp}.
     *
     * @param ctx the parse tree
     */
    void enterUnaryBoolOp(SwiftSqlParser.UnaryBoolOpContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#unaryBoolOp}.
     *
     * @param ctx the parse tree
     */
    void exitUnaryBoolOp(SwiftSqlParser.UnaryBoolOpContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#binaryOp}.
     *
     * @param ctx the parse tree
     */
    void enterBinaryOp(SwiftSqlParser.BinaryOpContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#binaryOp}.
     *
     * @param ctx the parse tree
     */
    void exitBinaryOp(SwiftSqlParser.BinaryOpContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#binaryBoolOp}.
     *
     * @param ctx the parse tree
     */
    void enterBinaryBoolOp(SwiftSqlParser.BinaryBoolOpContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#binaryBoolOp}.
     *
     * @param ctx the parse tree
     */
    void exitBinaryBoolOp(SwiftSqlParser.BinaryBoolOpContext ctx);

    /**
     * Enter a parse tree produced by {@link SwiftSqlParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(SwiftSqlParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link SwiftSqlParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(SwiftSqlParser.NameContext ctx);
}