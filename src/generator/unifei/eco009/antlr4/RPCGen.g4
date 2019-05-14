grammar RPCGen;

@header {
    package generator.unifei.eco009.antlr4;
    import java.util.HashMap;
}

@members {
    private HashMap<String, String> methodMap = new HashMap<>();
}

/* * * * * * * * * * * *
* SYNTACTICAL ANALYSIS *
* * * * * * * * * * * */

/* AST Root */
program @init {
    
}: root_method*;

root_method: method_int | method_string;

method_int: INT_TYPE ID { methodMap.get($ID.text) == null }? L_PAREN method_args R_PAREN breakline { methodMap.put($ID.text, "int"); };

method_string: STRING_TYPE ID { methodMap.get($ID.text) == null }? L_PAREN method_args R_PAREN breakline { methodMap.put($ID.text, "String"); };

breakline: SEMI_COLON;

var_type:   INT_TYPE
    |       STRING_TYPE;

method_args: (var_type ID (COMMA var_type ID)*)?;


/* * * * * * * * * * * 
*  LEXICAL ANALYSIS  *
* * * * * * * * * * */

/* Keywords */
INT_TYPE:       'int';
STRING_TYPE:    'String';

/* Non-functional symbols */
L_PAREN:        '(';
R_PAREN:        ')';
SEMI_COLON:     ';';
COMMA:          ',';

/* Variable literals */
INT_LIT:          [+-]?[0-9]+;
STRING_LIT:       '"' (~[\r\n"] | '""')* '"';

/* Identifiers */
ID:           [a-zA-Z_][a-zA-Z0-9_]*;

/* White space */
WHITESPACE:     [\r\t\n ]+ -> skip;