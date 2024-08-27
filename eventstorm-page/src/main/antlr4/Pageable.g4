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
	: 'filter' equal filterContent
	;
	
filterContent
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
	: '[eq]'
	| '[neq]'
	| '[gt]'
	| '[ge]'
	| '[lt]'
	| '[le]'
	| '[cnt]'
	| '[sw]'
	| '[ew]'
	| '[in]'
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
