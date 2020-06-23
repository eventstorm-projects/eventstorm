grammar Pageable;

@header {
package eu.eventstorm.core.page;    
}

request
	: range	
	| range '&' filter
	| range '&' filter '&' sort
	| range '&' sort
	| filter '&' sort
	;
	
range
	: 'range' equal rangeStart minus rangeEnd; 

rangeStart
	: integer;
	
rangeEnd
	: integer;
		
filter
	: 'filter' equal filterList
	;
	
filterList
	: filterItem
	| filterItem ',' filterList;
	
filterItem
	: key op value	
	;

sort 
	: 'sort' equal sortList
	;
 
sortList
	: sortItem 
	| sortItem ',' sortList
	;

sortItem
	: ('+' | '-') STRING
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