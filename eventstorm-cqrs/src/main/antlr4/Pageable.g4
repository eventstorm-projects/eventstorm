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
	: key op value	
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

key
	: STRING;
   
op
	: '[eq]'
	| '[gt]'
	| '[lt]'
	;

value
	: STRING;
	 
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