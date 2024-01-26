grammar SScript;

parse
    : mainBlock
    ;

mainBlock
    : main=block EOF
    ;

block
    : (stmt+=statement)*?
    ;


statement
    : varStatement SEMI
    | assignment SEMI
    | ifStatement
    | assertStatement SEMI
    | forStatement
    | foreachStatement
    | whileStatement
    | fDefinition SEMI
    | exp=expression SEMI
    | Break SEMI
    | Continue SEMI
    | Return (ret=expression)? SEMI
    | Throw (err=expression) SEMI
    ;

forStatement
    : FOR '(' (assign=assignment | varAssign=varStatement)? ';' (test=expression)? ';' (loopChange=assignment)? ')' '{' b=block '}'
    ;

foreachStatement
    : FOREACH '(' id=Identifier ':' listExp=expression ')' '{' block '}'
    ;

whileStatement
    : WHILE '(' test=expression ')' '{' block '}'
    ;

varStatement
    : VAR defs+=varDef (',' defs+=varDef)*
    ;

varDef
    : id=Identifier ('=' exp=expression)?
    ;

assignment
    : plusplus=Identifier '++'
    | minusminus=Identifier '--'
    | id=(Identifier | DottedIdentifier) '=' exp=expression
    | indexId=indexIdentifier '=' exp=expression
    | (opequals=(Identifier | DottedIdentifier) | indexIdentifier) op=(LOGICALAND | LOGICALOR | PLUS | MINUS | MUL | DIV | MOD | POWER) '=' exp=expression
    | lambdaId=Identifier '=' lexp=lambdaExpression
    ;

ifStatement
    : IF ifTrue=testAndBlock (ELSE IF elifs+=testAndBlock)* (ELSE '{' el=block '}' )?
    ;

testAndBlock
    : '(' test=expression ')' '{' xb=block '}'
    ;

assertStatement
    : ty=(ASSERT | ASSERTTRUE | ASSERTFALSE | ASSERTEXCEPTION) '(' (exp=expression | stmt=statement) (',' m=expression)? ')'
    ;

fDefinition
    : DEFFN name=Identifier '(' params+=Identifier? (COMMA params+=Identifier)* ')' '{' xb=block '}'
    ;

expression
    : main=valueExpression
    ;

valueExpression
    : '(' valueExpression ')'                           #parenthesisExpr
    | <assoc=right> valueExpression op=POWER valueExpression #powerExpr
    | valueExpression op=(MUL | DIV | MOD) valueExpression #mulExpr
    | valueExpression op=(PLUS | MINUS) valueExpression #addExpr
    | valueExpression op=(GTEQ | LTEQ | GT | LT) valueExpression #relExpr
    | valueExpression op=(EQUALS | NOTEQUALS) valueExpression #equExpr
    | (
        lambdaCall |
        funcChain |
        unaryExpr) #baseExpr
    ;

generalFunc
    : isDef
    | mfrExpr
    | name=Identifier '(' params+=namedParameter? (COMMA params+=namedParameter)* ')'
    ;

isDef
    : 'isDefined' '(' var=Identifier ')'
    ;

mfrExpr
    : type=(MAP | FILTER | REDUCE) '(' exp=expression (',' initial=expression)? ',' lambda=lambdaExpression ')'
    ;

MAP: 'map';
FILTER: 'filter';
REDUCE: 'reduce';

funcChain
    : funcChain CHAIN funcChain #chainExpr
    | (func=generalFunc | proc=procCall) #callExpr
    ;

namedParameter
    : (name=Identifier '=')? (exp=expression | lexp=lambdaExpression)
    ;

lambdaExpression
    : ('(' id+=Identifier? (COMMA id+=Identifier)* ')' | id+=Identifier) CHAIN exp=expression
    | ('(' id+=Identifier? (COMMA id+=Identifier)* ')' | id+=Identifier) CHAIN '{' b=block '}'
    ;

procCall
    : '$' name=(Identifier | DottedIdentifier) '(' params+=namedParameter? (COMMA params+=namedParameter)* ')'
    ;

lambdaCall
    : id=Identifier '<' (e+=expression)? (COMMA e+=expression)* '>'
    ;

unaryExpr
    : op=MINUS (atom | expression)
    | op=NOT (atom | expression)
    | op=NOTEACH (atom | expression)
    | atom
    ;

atom
    : indexIdentifier
    | list
    | set
    | map
    | Identifier
    | queryDot=DottedIdentifier '?'
    | DottedIdentifier
    | primitive
    ;

primitive
    : Integer
    | Long
    | Number
    | Bool
    | MultiLineString
    | String
    | Null
    ;

list
    : '[' e+=expression (COMMA e+=expression)* ']'
    ;

map
    : '{' e+=mapEntry (COMMA e+=mapEntry)* '}'
    | '{' '}'
    ;

set
    : '%{' e+=expression (COMMA e+=expression)* '}'
    | '%{' '}'
    ;

mapEntry
    : i=(Identifier | String) ':' v=expression
    ;

Break: 'break';
Continue: 'continue';
Return : 'return';
Throw: 'throw';

Bool
    : 'true'
    | 'false'
    ;

Null
    : 'null'
    | 'NULL'
    ;

ASSERT: 'assert';
ASSERTTRUE: 'assertTrue';
ASSERTFALSE: 'assertFalse';
ASSERTEXCEPTION: 'assertException';

MultiLineString
    : '###' ('\r' | '\n') (.*?) '###'
    ;

Comment
    : '//' ~('\r' | '\n')* { skip(); }
    | '/*' .*? '*/' { skip(); }
    ;

fragment
DoubleQuote
    : '"'
    | '\u201C'
    | '\u201D'
    | '\u201E'
    | '\u201F'
    ;

fragment
SingleQuote
    : '\''
    | '\u2018'
    | '\u2019'
    | '\u201A'
    | '\u201B'
    ;

String
    : SingleQuote ( ~('\'' | '\n' | '\r' ) )* SingleQuote
    | DoubleQuote ( ~('"' | '\n' | '\r' ) )* DoubleQuote
    ;

Integer
    : Int
    | Int 'I'
    ;

Long
    : Int 'L'
    ;

Number
    : Int '.' Digit (Digit)* ( 'E' ('+' | '-')? Digit (Digit *))? ('f' | 'F')?
    | Int 'E' ('+'| '-')? Digit (Digit)*
    ;


DottedIdentifier
    : Identifier ('.' Identifier)+
    ;

indexIdentifier
    : id=(Identifier | DottedIdentifier) ( '[' index+=expression ']')+
    ;


Identifier
    : ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A' ..'Z' | '_' | Digit)*
    ;

fragment Int
    : '-'? '1'..'9' Digit*
    | '0'
    ;

fragment Digit
    : '0' .. '9'
    ;

SEMI: ';';
FOR: 'for';
FOREACH: 'foreach';
WHILE: 'while';
IF: 'if';
ELSE: 'else';
DEFFN: 'def';
VAR: 'var';
CHAIN: '->';
NOT: '!';
LOGICALAND: '&&';
LOGICALOR: '||';
PLUS: '+';
MINUS: '-';
MUL: '*';
DIV: '/';
MOD: '%';
POWER: '^';
EQUALS: '==';
NOTEQUALS: '!=';
GTEQ: '>=';
LTEQ: '<=';
GT: '>';
LT: '<';
COMMA: ',';
DOT: '.';
QUESTION: '?';
COLON: ':';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
LBRACK: '[';
RBRACK: ']';
NOTEACH: '!~';

WHITESPACE : (' ' | '\r' | '\n' | '\t') -> skip;


