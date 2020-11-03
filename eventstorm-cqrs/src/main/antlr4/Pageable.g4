grammar Pageable;

@header {
package eu.eventstorm.cqrs.query;    
}

request
	: range	
	| range '&' filter
	| range '&' filter '&' sort
	| range '&' sort
	| filter '&' sort
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
	: (sortAsc | sortDesc) STRING
	;	
	
sortAsc 
	: plus
	;

sortDesc 
	: minus
	;

property
	: STRING;
   
op
	: '[eq]'
	| '[neq]'
	| '[gt]'
	| '[ge]'
	| '[lt]'
	| '[le]'
	| '[cnt]'
	;

value
	: STRING
	| integer;
	 
equal 
	: '=';

plus
	: '+';
		
minus
	: '-';
		
integer
	: DIGIT+;

STRING
   : [a-zA-Z] [a-zA-Z0-9_]*
   ;

DIGIT
   : [0-9]
   ;