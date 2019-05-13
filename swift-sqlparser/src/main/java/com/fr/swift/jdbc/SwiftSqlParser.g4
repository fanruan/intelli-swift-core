parser grammar SwiftSqlParser;

/*
如何生成Parser？

cd到g4文件目录，cp指定antlr4-tool jar的path

java -cp antlr4-4.5.3.jar org.antlr.v4.Tool -package com.fr.swift.jdbc.antlr4 -o antlr4 SwiftSqlLexer.g4 SwiftSqlParser.g4

Parser即生成到了com.fr.swift.jdbc.antlr4包下
*/

options {
    language = Java;
    tokenVocab = SwiftSqlLexer;
}

root: sqls EOF;

sqls: sql SEMI? | sql (SEMI sql)* SEMI?;

sql: ddl | dml;

ddl: createTable | dropTable | alterTable;

dml: insert | delete | select;

// ddl
createTable:
    CREATE TABLE tableName = name L_PAR columnDefinitions R_PAR /*partitionBy?*/;

columnDefinitions:
    name columnDefinition (COMMA name columnDefinition)*?;

// todo 分块
//partitionBy:
//    PARTITION BY (
//        LINE L_PAR capacity = NUMERIC_LITERAL COMMA memCapacity = NUMERIC_LITERAL R_PAR
//        | HASH functionCall
//        | RANGE
//    );

dataType:
    BIT | TINYINT | SMALLINT | INTEGER | BIGINT
    | FLOAT | REAL | DOUBLE | NUMERIC | DECIMAL
    | CHAR | VARCHAR | LONGVARCHAR
    | DATE | TIME | TIMESTAMP
    | BOOLEAN;

function: MAX | MIN | SUM | AVG | COUNT | MID
        | TODATE;

columnDefinition:
    dataType (L_PAR length = NUMERIC_LITERAL R_PAR)?/* (
        (NOT)? NULL
    )?*/;

dropTable: DROP TABLE tableName = name;

alterTable: alterTableAddColumn | alterTableDropColumn;

alterTableAddColumn:
    ALTER TABLE tableName = name ADD columnDefinitions;

alterTableDropColumn:
    ALTER TABLE tableName = name DROP columnNames = names;

// dml
insert:
    INSERT INTO tableName = name (
        L_PAR columnNames = names R_PAR
    )? VALUES L_PAR values R_PAR;

values: literal (COMMA literal)*;

delete: DELETE FROM tableName = name WHERE where = expr;

select:
    SELECT DISTINCT? (
        MUL
        | (name | funcCall) (COMMA name | funcCall)*
    ) FROM tableName = name (WHERE where = expr)? (
        GROUP BY columnNames = names
    )? (HAVING having = expr)? (ORDER BY columnOrders)? (
        LIMIT limit = NUMERIC_LITERAL
    )?;

columnOrders: name (ASC | DESC)? ( COMMA name (ASC | DESC)?)*;

names: name (COMMA name)*;

expr:
    simpleExpr
    | simpleExpr binaryOp simpleExpr
    | expr (unaryBoolOp)? binaryBoolOp expr
    | unaryBoolOp expr
    | funcCall
    | keywordBoolExpr;

keywordBoolExpr:
    IDENTIFIER NOT? inExpr
    | IDENTIFIER NOT? betweenExpr
    | IDENTIFIER IS NOT? NULL;

inExpr: IN L_PAR literal (COMMA literal)* R_PAR;

betweenExpr: BETWEEN literal AND literal;

simpleExpr: literal | IDENTIFIER;

funcCall:
    function L_PAR (DISTINCT? simpleExpr (COMMA simpleExpr)*)? R_PAR;

literal: NULL | NUMERIC_LITERAL | STRING_LITERAL;

unaryBoolOp: NOT;

binaryOp: PLUS | MINUS | MUL | DIV;

binaryBoolOp:
    GREATER | GEQ
    | EQ | NEQ
    | LEQ | LESS
    | AND | OR
    | LIKE;

name: IDENTIFIER | L_PAR name R_PAR;