/* Tumas Bajoras, PS3. Transliavimo metodai 2011 m. rudens semestras */
%{

#include <stdio.h>

%}

%x ATTRIBUTES ATTRVAL TAGVAL

less  <
more  >
eq    =
quote \"

  /* general tags */
tag       package|metadata|dc-metadata|manifest|spine|guide|reference|item|itemref
attribute id|href|media-type|idref|type|title|unique-identifier|xmlns:dc|xmlns:oebpackage|version

  /* metadata info */
dc_tag    Identifier|Title|Type|Creator|Contributor|Subject|Description|Published|Type|Format|Source|Language|Relation|Coverage|Rights
dc_attribute role|id|file-as|event


%%

<INITIAL>{
  {less}                     { printf("LESS\n"); }
  {more}                     { printf("MORE\n"); }

  \?xml                      { printf("XML opening: %s\n", yytext); BEGIN ATTRIBUTES ; }
  \!DOCTYPE\ package\ PUBLIC { printf("DOCTYPE declaration: %s\n", yytext); BEGIN ATTRIBUTES ; }

  dc:{dc_tag} { printf("DC tag: %s\n", yytext); BEGIN ATTRIBUTES ; }
  {tag}       { printf("Tag: %s\n", yytext); BEGIN ATTRIBUTES ; }
  
  \/dc:{dc_tag} { printf("DC closing tag: %s\n", yytext); }
  \/{tag}       { printf("Closing tag: %s\n", yytext); }
  .
}

<TAGVAL>{
  {less} { printf("LESS\n"); BEGIN INITIAL ; }
  [^<]+  { printf("Tag value: %s\n", yytext); }
}

<ATTRIBUTES>{
  {eq}           { printf("Equality: %s\n", yytext); }
  {quote}        { printf("Quote: %s\n", yytext); BEGIN ATTRVAL ; }

  {attribute}    { printf("Attribute: %s\n", yytext); }
  {dc_attribute} { printf("DC attribute: %s\n", yytext); }

  [\/\?]{more}   { printf("Inline closing tag: %s\n", yytext); BEGIN INITIAL ; }
  {more}         { printf("MORE\n"); BEGIN TAGVAL ; } 
  .
}

<ATTRVAL>{
  {quote} { printf("Quote: %s\n", yytext); BEGIN ATTRIBUTES ; }
  [^\"]+  { printf("Attribute value: %s\n", yytext); }
}

%%

int main(void){
  yylex();
  return 0;
}

int yywrap(void) {
  return 1;
}
