parser grammar SwiftSqlParser;

options {
    language=Java;
    tokenVocab=SwiftSqlLexer;
}

root: sqls EOF;

sqls: sql (SEMI sql)*? SEMI?;

sql: ddl | dml;

ddl: createTable | dropTable | alterTable;

dml: insert | delete | select | truncate;

// ddl
createTable:
    CREATE TABLE table=name L_PAR columnDefinitions R_PAR /*partitionBy?*/;

columnDefinitions:
    name columnDefinition (COMMA name columnDefinition)*;

columnDefinition:
    dataType (L_PAR length=NUMERIC_LITERAL R_PAR)? /*((NOT)? NULL)?*/;

// todo 分块
//partitionBy:
//    PARTITION BY (
//        LINE L_PAR capacity=NUMERIC_LITERAL COMMA memCapacity=NUMERIC_LITERAL R_PAR
//        | HASH functionCall
//        | RANGE
//    );

dataType:
    BIT | TINYINT | SMALLINT | INTEGER | BIGINT |
    FLOAT | REAL | DOUBLE | NUMERIC | DECIMAL |
    CHAR | VARCHAR | LONGVARCHAR |
    DATE | TIME | TIMESTAMP |
    BOOLEAN;

dropTable: DROP TABLE table=name;

alterTable: alterTableAddColumn | alterTableDropColumn;

alterTableAddColumn:
    ALTER TABLE table=name ADD columnDefinitions;

alterTableDropColumn:
    ALTER TABLE table=name DROP columnNames=names;

// dml
insert:
    INSERT INTO table=name
    (L_PAR columnNames=names R_PAR)?
    VALUES
    L_PAR values R_PAR (COMMA L_PAR values R_PAR)*?;

delete: DELETE FROM table=name WHERE where=expr;

truncate: TRUNCATE table=name;

select:
    SELECT
    DISTINCT? columns
    FROM (table=name | L_PAR subQuery=select R_PAR) (AS? alias=name)?
    (WHERE where=expr)?
    (GROUP BY groupBy=names)?
    (HAVING having=expr)?
    (ORDER BY orderBy)?
    (LIMIT limit=NUMERIC_LITERAL)?;

columns:
    '*' |
    simpleExpr (AS? alias=name)? (COMMA simpleExpr (AS? alias=name)?)*?;

orderBy: name (ASC | DESC)? ( COMMA name (ASC | DESC)?)*?;

expr:
    simpleExpr |
    simpleExpr op simpleExpr | L_PAR simpleExpr op simpleExpr R_PAR |
    boolExpr |
    expr op expr | L_PAR expr op expr R_PAR;

simpleExpr: value | name | funcExpr;

funcExpr:
    funcName L_PAR (simpleExpr (COMMA simpleExpr)*?)? R_PAR;

funcName:
    MAX | MIN | SUM | AVG | COUNT | MID |
    TODATE;

keywordBoolExpr:
    simpleExpr NOT? IN L_PAR values R_PAR |
    simpleExpr NOT? BETWEEN NUMERIC_LITERAL AND NUMERIC_LITERAL |
    simpleExpr IS NOT? NULL;

boolExpr:
    simpleExpr boolOp simpleExpr | L_PAR simpleExpr boolOp simpleExpr R_PAR |
    keywordBoolExpr |
    NOT boolExpr |
    boolExpr logicOp boolExpr | L_PAR boolExpr logicOp boolExpr R_PAR;

op: PLUS | MINUS | MUL | DIV;

boolOp:
    GREATER | GEQ |
    EQ | NEQ |
    LEQ | LESS |
    LIKE;

logicOp: AND | OR;

name: IDENTIFIER;

names: name (COMMA name)*?;

value: NULL | NUMERIC_LITERAL | STRING_LITERAL;

values: value (COMMA value)*?;