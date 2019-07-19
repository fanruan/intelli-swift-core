lexer grammar SwiftSqlLexer;

// select
SELECT: S E L E C T;
DISTINCT: D I S T I N C T;
AS: A S;
FROM: F R O M;
WHERE: W H E R E;
GROUP: G R O U P;
BY: B Y;
HAVING: H A V I N G;
ORDER: O R D E R;
ASC: A S C;
DESC: D E S C;
LIMIT: L I M I T;

// insert
INSERT: I N S E R T;
INTO: I N T O;
VALUES: V A L U E S;

// delete
DELETE: D E L E T E;

// truncate
TRUNCATE: T R U N C A T E;

// create table
CREATE: C R E A T E;
TABLE: T A B L E;
NULL: N U L L;
PARTITION: P A R T I T I O N;

// data type
BIT: B I T;
TINYINT: T I N Y I N T;
SMALLINT: S M A L L I N T;
INTEGER: I N T E G E R;
BIGINT: B I G I N T;
FLOAT: F L O A T;
REAL: R E A L;
DOUBLE: D O U B L E;
NUMERIC: N U M E R I C;
DECIMAL: D E C I M A L;
CHAR: C H A R;
VARCHAR: V A R C H A R;
LONGVARCHAR: L O N G V A R C H A R;
DATE: D A T E;
TIME: T I M E;
TIMESTAMP: T I M E S T A M P;
BOOLEAN: B O O L E A N;

// drop table
DROP: D R O P;

// alter table
ALTER: A L T E R;
ADD: A D D;
COLUMN: C O L U M N;

// function
MAX: M A X;
MIN: M I N;
SUM: S U M;
AVG: A V G;
COUNT: C O U N T;
MID: M I D;
// swift featured function
TODATE: T O D A T E;

NOT: N O T;
IN: I N;
BETWEEN: B E T W E E N;
AND: A N D;
OR: O R;
LIKE: L I K E;
IS: I S;

// Swift keywords
LINE: L I N E;
HASH: H A S H;
RANGE: R A N G E;

// Operators. Arithmetics
MUL: '*';
DIV: '/';
MOD: '%';
PLUS: '+';
MINUS: '-';

// Operators. Comparation
EQ: '=';
GREATER: '>';
LESS: '<';
GEQ: '>=';
LEQ: '<=';
NEQ: '!=';
EXCLAMATION: '!';

// Operators. Bit
BIT_NOT: '~';
BIT_OR: '|';
BIT_AND: '&';
BIT_XOR: '^';

// Constructors symbols
DOT: '.';
L_PAR: '(';
R_PAR: ')';
COMMA: ',';
SEMI: ';';
AT: '@';
SINGLE_QUOTE: '\'';
DOUBLE_QUOTE: '"';
REVERSE_QUOTE: '`';
COLON: ':';

IDENTIFIER:
    '"' (~'"' | '""')* '"'
    | '`' (~'`' | '``')* '`'
    | '[' ~']'* ']'
    | [a-zA-Z_] [a-zA-Z_0-9]*; // TODO check: needs more chars in set

NUMERIC_LITERAL:
    DIGIT+ ('.' DIGIT*)? (E [-+]? DIGIT+)?
    | '.' DIGIT+ ( E [-+]? DIGIT+)?;

STRING_LITERAL: '\'' ( ~'\'' | '\'\'')* '\'';

DIGIT: [0-9];

// case insensitive
fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];

// white space
WS: [ \t\n\r]+ -> skip;