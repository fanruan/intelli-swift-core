/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fr.swift.jdbc.druid.sql.visitor;

import com.fr.swift.jdbc.druid.sql.ast.SQLArgument;
import com.fr.swift.jdbc.druid.sql.ast.SQLArrayDataType;
import com.fr.swift.jdbc.druid.sql.ast.SQLCommentHint;
import com.fr.swift.jdbc.druid.sql.ast.SQLDataType;
import com.fr.swift.jdbc.druid.sql.ast.SQLDeclareItem;
import com.fr.swift.jdbc.druid.sql.ast.SQLKeep;
import com.fr.swift.jdbc.druid.sql.ast.SQLLimit;
import com.fr.swift.jdbc.druid.sql.ast.SQLMapDataType;
import com.fr.swift.jdbc.druid.sql.ast.SQLObject;
import com.fr.swift.jdbc.druid.sql.ast.SQLOrderBy;
import com.fr.swift.jdbc.druid.sql.ast.SQLOver;
import com.fr.swift.jdbc.druid.sql.ast.SQLParameter;
import com.fr.swift.jdbc.druid.sql.ast.SQLPartitionValue;
import com.fr.swift.jdbc.druid.sql.ast.SQLRecordDataType;
import com.fr.swift.jdbc.druid.sql.ast.SQLStructDataType;
import com.fr.swift.jdbc.druid.sql.ast.SQLSubPartition;
import com.fr.swift.jdbc.druid.sql.ast.SQLSubPartitionByHash;
import com.fr.swift.jdbc.druid.sql.ast.SQLSubPartitionByList;
import com.fr.swift.jdbc.druid.sql.ast.SQLWindow;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLAggregateExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLAllColumnExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLAllExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLAnyExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLArrayExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLBetweenExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLBinaryExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLBinaryOpExprGroup;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLBooleanExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLCaseExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLCaseStatement;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLCastExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLCharExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLContainsExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLDateExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLDefaultExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLExistsExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLFlashbackExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLGroupingSetExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLHexExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIdentifierExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLInListExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIntegerExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLIntervalExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLListExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNCharExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNotExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNullExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLNumberExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLPropertyExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLQueryExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLRealExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLSequenceExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLSomeExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLTimestampExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLUnaryExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLValuesExpr;
import com.fr.swift.jdbc.druid.sql.ast.expr.SQLVariantRefExpr;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterCharacter;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterDatabaseStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterFunctionStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterProcedureStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterSequenceStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableAddIndex;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableAddPartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableAnalyzePartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableCheckPartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableCoalescePartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableConvertCharSet;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDisableKeys;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDisableLifecycle;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDiscardPartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDropForeignKey;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDropIndex;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDropKey;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDropPartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableEnableKeys;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableEnableLifecycle;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableExchangePartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableImportPartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableOptimizePartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableReOrganizePartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableRebuildPartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableRename;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableRenameColumn;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableRenameIndex;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableRenamePartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableRepairPartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableSetComment;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableSetLifecycle;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableTouch;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTableTruncatePartition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterTypeStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterViewRenameStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAlterViewStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLAssignItem;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLBlockStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCallStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCharacterDataType;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCheck;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCloseStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLColumnCheck;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLColumnDefinition;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLColumnPrimaryKey;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLColumnReference;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLColumnUniqueKey;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCommentStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCommitStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateFunctionStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateMaterializedViewStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateProcedureStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateSequenceStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateUserStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLCreateViewStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDeclareStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDeleteStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDescribeStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropDatabaseStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropEventStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropFunctionStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropIndexStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropLogFileGroupStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropMaterializedViewStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropProcedureStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropSequenceStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropServerStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropSynonymStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTableSpaceStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTableStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTriggerStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropTypeStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropUserStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLDropViewStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLErrorLoggingClause;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExplainStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExprHint;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExprStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExprTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLFetchStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLForeignKeyImpl;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLGrantStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLIfStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLInsertStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLJoinTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLLateralViewTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLLoopStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLMergeStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLMergeStatement.MergeInsertClause;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLMergeStatement.MergeUpdateClause;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLNotNullConstraint;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLNullConstraint;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLOpenStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLReleaseSavePointStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLReplaceStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLReturnStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLRevokeStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLRollbackStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSavePointStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLScriptCommitStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelect;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectItem;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSelectStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSetStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLShowErrorsStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLShowTablesStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLStartTransactionStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLTruncateStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLUnionQuery;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLUnionQueryTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLUnique;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLUpdateSetItem;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLUpdateStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLUseStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLValuesTableSource;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLWhileStatement;
import com.fr.swift.jdbc.druid.sql.ast.statement.SQLWithSubqueryClause;

public class SQLASTVisitorAdapter implements SQLASTVisitor {
    protected int features;

    public void endVisit(SQLAllColumnExpr x) {
    }

    public void endVisit(SQLBetweenExpr x) {
    }

    public void endVisit(SQLBinaryOpExpr x) {
    }

    public void endVisit(SQLCaseExpr x) {
    }

    public void endVisit(SQLCaseExpr.Item x) {
    }

    public void endVisit(SQLCaseStatement x) {
    }

    public void endVisit(SQLCaseStatement.Item x) {
    }

    public void endVisit(SQLCharExpr x) {
    }

    public void endVisit(SQLIdentifierExpr x) {
    }

    public void endVisit(SQLInListExpr x) {
    }

    public void endVisit(SQLIntegerExpr x) {
    }

    public void endVisit(SQLExistsExpr x) {
    }

    public void endVisit(SQLNCharExpr x) {
    }

    public void endVisit(SQLNotExpr x) {
    }

    public void endVisit(SQLNullExpr x) {
    }

    public void endVisit(SQLNumberExpr x) {
    }

    public void endVisit(SQLPropertyExpr x) {
    }

    public void endVisit(SQLSelectGroupByClause x) {
    }

    public void endVisit(SQLSelectItem x) {
    }

    public void endVisit(SQLSelectStatement selectStatement) {
    }

    public void postVisit(SQLObject astNode) {
    }

    public void preVisit(SQLObject astNode) {
    }

    public boolean visit(SQLAllColumnExpr x) {
        return true;
    }

    public boolean visit(SQLBetweenExpr x) {
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        return true;
    }

    public boolean visit(SQLCaseExpr x) {
        return true;
    }

    public boolean visit(SQLCaseExpr.Item x) {
        return true;
    }

    public boolean visit(SQLCaseStatement x) {
        return true;
    }

    public boolean visit(SQLCaseStatement.Item x) {
        return true;
    }

    public boolean visit(SQLCastExpr x) {
        return true;
    }

    public boolean visit(SQLCharExpr x) {
        return true;
    }

    public boolean visit(SQLExistsExpr x) {
        return true;
    }

    public boolean visit(SQLIdentifierExpr x) {
        return true;
    }

    public boolean visit(SQLInListExpr x) {
        return true;
    }

    public boolean visit(SQLIntegerExpr x) {
        return true;
    }

    public boolean visit(SQLNCharExpr x) {
        return true;
    }

    public boolean visit(SQLNotExpr x) {
        return true;
    }

    public boolean visit(SQLNullExpr x) {
        return true;
    }

    public boolean visit(SQLNumberExpr x) {
        return true;
    }

    public boolean visit(SQLPropertyExpr x) {
        return true;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        return true;
    }

    public boolean visit(SQLSelectItem x) {
        return true;
    }

    public void endVisit(SQLCastExpr x) {
    }

    public boolean visit(SQLSelectStatement astNode) {
        return true;
    }

    public void endVisit(SQLAggregateExpr x) {
    }

    public boolean visit(SQLAggregateExpr x) {
        return true;
    }

    public boolean visit(SQLVariantRefExpr x) {
        return true;
    }

    public void endVisit(SQLVariantRefExpr x) {
    }

    public boolean visit(SQLQueryExpr x) {
        return true;
    }

    public void endVisit(SQLQueryExpr x) {
    }

    public boolean visit(SQLSelect x) {
        return true;
    }

    public void endVisit(SQLSelect select) {
    }

    public boolean visit(SQLSelectQueryBlock x) {
        return true;
    }

    public void endVisit(SQLSelectQueryBlock x) {
    }

    public boolean visit(SQLExprTableSource x) {
        return true;
    }

    public void endVisit(SQLExprTableSource x) {
    }

    public boolean visit(SQLOrderBy x) {
        return true;
    }

    public void endVisit(SQLOrderBy x) {
    }

    public boolean visit(SQLSelectOrderByItem x) {
        return true;
    }

    public void endVisit(SQLSelectOrderByItem x) {
    }

    public boolean visit(SQLDropTableStatement x) {
        return true;
    }

    public void endVisit(SQLDropTableStatement x) {
    }

    public boolean visit(SQLCreateTableStatement x) {
        return true;
    }

    public void endVisit(SQLCreateTableStatement x) {
    }

    public boolean visit(SQLColumnDefinition x) {
        return true;
    }

    public void endVisit(SQLColumnDefinition x) {
    }

    public boolean visit(SQLColumnDefinition.Identity x) {
        return true;
    }

    public void endVisit(SQLColumnDefinition.Identity x) {
    }

    public boolean visit(SQLDataType x) {
        return true;
    }

    public void endVisit(SQLDataType x) {
    }

    public boolean visit(SQLDeleteStatement x) {
        return true;
    }

    public void endVisit(SQLDeleteStatement x) {
    }

    public boolean visit(SQLCurrentOfCursorExpr x) {
        return true;
    }

    public void endVisit(SQLCurrentOfCursorExpr x) {
    }

    public boolean visit(SQLInsertStatement x) {
        return true;
    }

    public void endVisit(SQLInsertStatement x) {
    }

    public boolean visit(SQLUpdateSetItem x) {
        return true;
    }

    public void endVisit(SQLUpdateSetItem x) {
    }

    public boolean visit(SQLUpdateStatement x) {
        return true;
    }

    public void endVisit(SQLUpdateStatement x) {
    }

    public boolean visit(SQLCreateViewStatement x) {
        return true;
    }

    public void endVisit(SQLCreateViewStatement x) {
    }

    public boolean visit(SQLAlterViewStatement x) {
        return true;
    }

    public void endVisit(SQLAlterViewStatement x) {
    }

    public boolean visit(SQLCreateViewStatement.Column x) {
        return true;
    }

    public void endVisit(SQLCreateViewStatement.Column x) {
    }

    public boolean visit(SQLNotNullConstraint x) {
        return true;
    }

    public void endVisit(SQLNotNullConstraint x) {
    }

    @Override
    public void endVisit(SQLMethodInvokeExpr x) {

    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnionQuery x) {

    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        return true;
    }

    @Override
    public boolean visit(SQLUnaryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnaryExpr x) {

    }

    @Override
    public boolean visit(SQLHexExpr x) {
        return false;
    }

    @Override
    public void endVisit(SQLHexExpr x) {

    }

    @Override
    public void endVisit(SQLSetStatement x) {

    }

    @Override
    public boolean visit(SQLSetStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAssignItem x) {

    }

    @Override
    public boolean visit(SQLAssignItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLCallStatement x) {

    }

    @Override
    public boolean visit(SQLCallStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLJoinTableSource x) {

    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        return true;
    }

    @Override
    public boolean visit(ValuesClause x) {
        return true;
    }

    @Override
    public void endVisit(ValuesClause x) {

    }

    @Override
    public void endVisit(SQLSomeExpr x) {

    }

    @Override
    public boolean visit(SQLSomeExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAnyExpr x) {

    }

    @Override
    public boolean visit(SQLAnyExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAllExpr x) {

    }

    @Override
    public boolean visit(SQLAllExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLInSubQueryExpr x) {

    }

    @Override
    public boolean visit(SQLInSubQueryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLListExpr x) {

    }

    @Override
    public boolean visit(SQLListExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubqueryTableSource x) {

    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLTruncateStatement x) {

    }

    @Override
    public boolean visit(SQLTruncateStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDefaultExpr x) {

    }

    @Override
    public boolean visit(SQLDefaultExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommentStatement x) {

    }

    @Override
    public boolean visit(SQLCommentStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLUseStatement x) {

    }

    @Override
    public boolean visit(SQLUseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLAlterTableAddColumn x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddColumn x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropColumnItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropColumnItem x) {

    }

    @Override
    public boolean visit(SQLDropIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropIndexStatement x) {

    }

    @Override
    public boolean visit(SQLDropViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropViewStatement x) {

    }

    @Override
    public boolean visit(SQLSavePointStatement x) {
        return false;
    }

    @Override
    public void endVisit(SQLSavePointStatement x) {

    }

    @Override
    public boolean visit(SQLRollbackStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLRollbackStatement x) {

    }

    @Override
    public boolean visit(SQLReleaseSavePointStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLReleaseSavePointStatement x) {
    }

    @Override
    public boolean visit(SQLCommentHint x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommentHint x) {

    }

    @Override
    public void endVisit(SQLCreateDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLCreateDatabaseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLAlterTableDropIndex x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropIndex x) {

    }

    @Override
    public void endVisit(SQLOver x) {
    }

    @Override
    public boolean visit(SQLOver x) {
        return true;
    }

    @Override
    public void endVisit(SQLKeep x) {
    }

    @Override
    public boolean visit(SQLKeep x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnPrimaryKey x) {

    }

    @Override
    public boolean visit(SQLColumnPrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnUniqueKey x) {

    }

    @Override
    public boolean visit(SQLColumnUniqueKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLWithSubqueryClause x) {
    }

    @Override
    public boolean visit(SQLWithSubqueryClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLWithSubqueryClause.Entry x) {
    }

    @Override
    public boolean visit(SQLWithSubqueryClause.Entry x) {
        return true;
    }

    @Override
    public boolean visit(SQLCharacterDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLCharacterDataType x) {

    }

    @Override
    public void endVisit(SQLAlterTableAlterColumn x) {

    }

    @Override
    public boolean visit(SQLAlterTableAlterColumn x) {
        return true;
    }

    @Override
    public boolean visit(SQLCheck x) {
        return true;
    }

    @Override
    public void endVisit(SQLCheck x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropForeignKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropForeignKey x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropPrimaryKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropPrimaryKey x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableKeys x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableKeys x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableKeys x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableKeys x) {

    }

    @Override
    public boolean visit(SQLAlterTableStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableConstraint x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableConstraint x) {

    }

    @Override
    public boolean visit(SQLColumnCheck x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnCheck x) {

    }

    @Override
    public boolean visit(SQLExprHint x) {
        return true;
    }

    @Override
    public void endVisit(SQLExprHint x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropConstraint x) {

    }

    @Override
    public boolean visit(SQLUnique x) {
        for (SQLSelectOrderByItem column : x.getColumns()) {
            column.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(SQLUnique x) {

    }

    @Override
    public boolean visit(SQLCreateIndexStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateIndexStatement x) {

    }

    @Override
    public boolean visit(SQLPrimaryKeyImpl x) {
        return true;
    }

    @Override
    public void endVisit(SQLPrimaryKeyImpl x) {

    }

    @Override
    public boolean visit(SQLAlterTableRenameColumn x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRenameColumn x) {

    }

    @Override
    public boolean visit(SQLColumnReference x) {
        return true;
    }

    @Override
    public void endVisit(SQLColumnReference x) {

    }

    @Override
    public boolean visit(SQLForeignKeyImpl x) {
        return true;
    }

    @Override
    public void endVisit(SQLForeignKeyImpl x) {

    }

    @Override
    public boolean visit(SQLDropSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropSequenceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTriggerStatement x) {

    }

    @Override
    public void endVisit(SQLDropUserStatement x) {

    }

    @Override
    public boolean visit(SQLDropUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLExplainStatement x) {

    }

    @Override
    public boolean visit(SQLExplainStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLGrantStatement x) {

    }

    @Override
    public boolean visit(SQLGrantStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLDropDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddIndex x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddIndex x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddConstraint x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateTriggerStatement x) {

    }

    @Override
    public boolean visit(SQLCreateTriggerStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLDropFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTableSpaceStatement x) {

    }

    @Override
    public boolean visit(SQLDropTableSpaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropProcedureStatement x) {

    }

    @Override
    public boolean visit(SQLDropProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBooleanExpr x) {

    }

    @Override
    public boolean visit(SQLBooleanExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLUnionQueryTableSource x) {

    }

    @Override
    public boolean visit(SQLUnionQueryTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLTimestampExpr x) {

    }

    @Override
    public boolean visit(SQLTimestampExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLRevokeStatement x) {

    }

    @Override
    public boolean visit(SQLRevokeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBinaryExpr x) {

    }

    @Override
    public boolean visit(SQLBinaryExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRename x) {

    }

    @Override
    public boolean visit(SQLAlterTableRename x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterViewRenameStatement x) {

    }

    @Override
    public boolean visit(SQLAlterViewRenameStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowTablesStatement x) {

    }

    @Override
    public boolean visit(SQLShowTablesStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAddPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableAddPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRenamePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRenamePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableSetComment x) {

    }

    @Override
    public boolean visit(SQLAlterTableSetComment x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableSetLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableSetLifecycle x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableEnableLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableEnableLifecycle x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDisableLifecycle x) {

    }

    @Override
    public boolean visit(SQLAlterTableDisableLifecycle x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableTouch x) {

    }

    @Override
    public boolean visit(SQLAlterTableTouch x) {
        return true;
    }

    @Override
    public void endVisit(SQLArrayExpr x) {

    }

    @Override
    public boolean visit(SQLArrayExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLOpenStatement x) {

    }

    @Override
    public boolean visit(SQLOpenStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLFetchStatement x) {

    }

    @Override
    public boolean visit(SQLFetchStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCloseStatement x) {

    }

    @Override
    public boolean visit(SQLCloseStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLGroupingSetExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLGroupingSetExpr x) {

    }

    @Override
    public boolean visit(SQLIfStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement x) {

    }

    @Override
    public boolean visit(SQLIfStatement.Else x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement.Else x) {

    }

    @Override
    public boolean visit(SQLIfStatement.ElseIf x) {
        return true;
    }

    @Override
    public void endVisit(SQLIfStatement.ElseIf x) {

    }

    @Override
    public boolean visit(SQLLoopStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLLoopStatement x) {

    }

    @Override
    public boolean visit(SQLParameter x) {
        return true;
    }

    @Override
    public void endVisit(SQLParameter x) {

    }

    @Override
    public boolean visit(SQLCreateProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateProcedureStatement x) {

    }

    @Override
    public boolean visit(SQLCreateFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLBlockStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLBlockStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableDropKey x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDropKey x) {

    }

    @Override
    public boolean visit(SQLDeclareItem x) {
        return true;
    }

    @Override
    public void endVisit(SQLDeclareItem x) {
    }

    @Override
    public boolean visit(SQLPartitionValue x) {
        return true;
    }

    @Override
    public void endVisit(SQLPartitionValue x) {

    }

    @Override
    public boolean visit(SQLSubPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartition x) {

    }

    @Override
    public boolean visit(SQLSubPartitionByHash x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartitionByHash x) {

    }

    @Override
    public boolean visit(SQLSubPartitionByList x) {
        return true;
    }

    @Override
    public void endVisit(SQLSubPartitionByList x) {

    }

    @Override
    public boolean visit(SQLAlterDatabaseStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterDatabaseStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableConvertCharSet x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableConvertCharSet x) {

    }

    @Override
    public boolean visit(SQLAlterTableReOrganizePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableReOrganizePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableCoalescePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableCoalescePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableTruncatePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableTruncatePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableDiscardPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableDiscardPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableImportPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableImportPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableAnalyzePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableAnalyzePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableCheckPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableCheckPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableOptimizePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableOptimizePartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRebuildPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRebuildPartition x) {

    }

    @Override
    public boolean visit(SQLAlterTableRepairPartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRepairPartition x) {

    }

    @Override
    public boolean visit(SQLSequenceExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLSequenceExpr x) {

    }

    @Override
    public boolean visit(SQLMergeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLMergeStatement x) {

    }

    @Override
    public boolean visit(MergeUpdateClause x) {
        return true;
    }

    @Override
    public void endVisit(MergeUpdateClause x) {

    }

    @Override
    public boolean visit(MergeInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(MergeInsertClause x) {

    }

    @Override
    public boolean visit(SQLErrorLoggingClause x) {
        return true;
    }

    @Override
    public void endVisit(SQLErrorLoggingClause x) {

    }

    @Override
    public boolean visit(SQLNullConstraint x) {
        return true;
    }

    @Override
    public void endVisit(SQLNullConstraint x) {
    }

    @Override
    public boolean visit(SQLCreateSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateSequenceStatement x) {
    }

    @Override
    public boolean visit(SQLDateExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLDateExpr x) {

    }

    @Override
    public boolean visit(SQLLimit x) {
        return true;
    }

    @Override
    public void endVisit(SQLLimit x) {

    }

    @Override
    public void endVisit(SQLStartTransactionStatement x) {

    }

    @Override
    public boolean visit(SQLStartTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDescribeStatement x) {

    }

    @Override
    public boolean visit(SQLDescribeStatement x) {
        return true;
    }

    @Override
    public boolean visit(SQLWhileStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLWhileStatement x) {

    }


    @Override
    public boolean visit(SQLDeclareStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDeclareStatement x) {

    }

    @Override
    public boolean visit(SQLReturnStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLReturnStatement x) {

    }

    @Override
    public boolean visit(SQLArgument x) {
        return true;
    }

    @Override
    public void endVisit(SQLArgument x) {

    }

    @Override
    public boolean visit(SQLCommitStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCommitStatement x) {

    }

    @Override
    public boolean visit(SQLFlashbackExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLFlashbackExpr x) {

    }

    @Override
    public boolean visit(SQLCreateMaterializedViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateMaterializedViewStatement x) {

    }

    @Override
    public boolean visit(SQLBinaryOpExprGroup x) {
        return true;
    }

    @Override
    public void endVisit(SQLBinaryOpExprGroup x) {

    }

    public void config(VisitorFeature feature, boolean state) {
        features = VisitorFeature.config(features, feature, state);
    }

    @Override
    public boolean visit(SQLScriptCommitStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLScriptCommitStatement x) {

    }

    @Override
    public boolean visit(SQLReplaceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLReplaceStatement x) {

    }

    @Override
    public boolean visit(SQLCreateUserStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLCreateUserStatement x) {

    }

    @Override
    public boolean visit(SQLAlterFunctionStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterFunctionStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTypeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTypeStatement x) {

    }

    @Override
    public boolean visit(SQLIntervalExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLIntervalExpr x) {

    }

    @Override
    public boolean visit(SQLLateralViewTableSource x) {
        return true;
    }

    @Override
    public void endVisit(SQLLateralViewTableSource x) {

    }

    @Override
    public boolean visit(SQLShowErrorsStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLShowErrorsStatement x) {

    }

    @Override
    public boolean visit(SQLAlterCharacter x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterCharacter x) {

    }

    @Override
    public boolean visit(SQLExprStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLExprStatement x) {

    }

    @Override
    public boolean visit(SQLAlterProcedureStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterProcedureStatement x) {

    }

    @Override
    public boolean visit(SQLDropEventStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropEventStatement x) {

    }

    @Override
    public boolean visit(SQLDropLogFileGroupStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropLogFileGroupStatement x) {

    }

    @Override
    public boolean visit(SQLDropServerStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropServerStatement x) {

    }

    @Override
    public boolean visit(SQLDropSynonymStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropSynonymStatement x) {

    }

    @Override
    public boolean visit(SQLDropTypeStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropTypeStatement x) {

    }

    @Override
    public boolean visit(SQLRecordDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLRecordDataType x) {

    }

    public boolean visit(SQLExternalRecordFormat x) {
        return true;
    }

    public void endVisit(SQLExternalRecordFormat x) {

    }

    @Override
    public boolean visit(SQLArrayDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLArrayDataType x) {

    }

    @Override
    public boolean visit(SQLMapDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLMapDataType x) {

    }

    @Override
    public boolean visit(SQLStructDataType x) {
        return true;
    }

    @Override
    public void endVisit(SQLStructDataType x) {

    }

    @Override
    public boolean visit(SQLStructDataType.Field x) {
        return true;
    }

    @Override
    public void endVisit(SQLStructDataType.Field x) {

    }

    @Override
    public boolean visit(SQLDropMaterializedViewStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLDropMaterializedViewStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableRenameIndex x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableRenameIndex x) {

    }

    @Override
    public boolean visit(SQLAlterSequenceStatement x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterSequenceStatement x) {

    }

    @Override
    public boolean visit(SQLAlterTableExchangePartition x) {
        return true;
    }

    @Override
    public void endVisit(SQLAlterTableExchangePartition x) {

    }

    @Override
    public boolean visit(SQLValuesExpr x) {
        return true;
    }

    @Override
    public void endVisit(SQLValuesExpr x) {

    }

    @Override
    public boolean visit(SQLValuesTableSource x) {
        return true;
    }

    public void endVisit(SQLValuesTableSource x) {

    }

    @Override
    public boolean visit(SQLContainsExpr x) {
        return true;
    }

    public void endVisit(SQLContainsExpr x) {

    }

    @Override
    public boolean visit(SQLRealExpr x) {
        return true;
    }

    public void endVisit(SQLRealExpr x) {

    }


    @Override
    public boolean visit(SQLWindow x) {
        return true;
    }

    public void endVisit(SQLWindow x) {

    }


    public final boolean isEnabled(VisitorFeature feature) {
        return VisitorFeature.isEnabled(this.features, feature);
    }

    public int getFeatures() {
        return features;
    }

    public void setFeatures(int features) {
        this.features = features;
    }
}
