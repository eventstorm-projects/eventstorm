grammar Pageable;

@header {
package eu.eventstorm.page.parser;
}

request
	: range	
	| range '&' filter
	| range '&' filter '&' sort
	| range '&' sort '&' filter
	| range '&' sort
	| filter '&' sort
	| filter
	;
	
range
	: 'range' equal rangeContent
	; 
	
rangeContent
	: rangeStart minus rangeEnd;

rangeStart
	: integer;
	
rangeEnd
	: integer;
		
filter
	: 'filter' equal filterExpression
	;

filterExpression
    : filterExpressionOr
    | filterExpressionAnd
    | filterItems
    ;

filterExpressionOr
    : LPAREN filterExpressionLeft RPAREN'or' LPAREN filterExpressionRight RPAREN
    ;

filterExpressionAnd
    : LPAREN filterExpressionLeft RPAREN 'and' LPAREN filterExpressionRight RPAREN
    ;

filterExpressionLeft
    :  filterExpression
    ;

filterExpressionRight
    :  filterExpression
    ;


filterItems
    : filterItem (',' filterItem)*
    ;

filterItem
	: property op value
	;

sort 
	: 'sort' equal sortContent
	;
 
sortContent
	: sortItem (',' sortItem)*  
	;
	
sortItem
	: (sortAsc | sortDesc) IDENTIFIER
	;	
	
sortAsc 
	: plus
	;

sortDesc 
	: minus
	;

property
	: IDENTIFIER;
   
op
	: '[eq]'    // equals
	| '[neq]'   // not equals
	| '[gt]'    // greater than
	| '[ge]'    // greater or equals
	| '[lt]'    // less than
	| '[le]'    //  less or equals
	| '[cnt]'   // contains
	| '[in]'    // in
	| '[nin]'   // not in
	| '[sw]'    // starts with
    | '[ew]'    // ends with
	;

value
	: multipleValue
	| singleValue
	;
	 
singleValue
	: STRING	
	| integer;
	
multipleValue 
    : '[' singleValue (';' singleValue)* ']'
    ;
     
equal 
	: '=';

plus
	: '+';
		
minus
	: '-';
		
integer
	: DIGIT+;

DIGIT
   : [0-9]
   ;
   
IDENTIFIER
   : [a-zA-Z] [a-zA-Z0-9_]*
   ;
   
STRING
   :  '\'' ('\'\'' | ~ ('\''))* '\''
   ;

LPAREN
    : '('
    ;

RPAREN
    : ')'
    ;