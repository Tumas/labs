%{

#include <stdio.h>

%}

less  <
more  >
eq    =
quote \"

  /* general tags */
tag       package|metadata|dc-metadata|manifest|spine|guide|reference|item|itemref
attribute id|href|media-type|idref|type|title|unique-identifier|xmlns:dc|version

  /* metadata info */
dc_tag    Identifier|Title|Type|Creator|Contributor|Subject|Description|Published|Type|Format|Source|Language|Relation|Coverage|Rights
dc_attribute role|id|file-as|event

value (^(\"))*

%%

{less}         { printf("Opening tag: %s\n", yytext); }
{more}         { printf("Closing tag: %s\n", yytext); }
(\/)*{more} { printf("Inline closing tag: %s\n", yytext); }
{eq}           { printf("Equality: %s\n", yytext); }
{quote}        { printf("Quote: %s\n", yytext); }
\?xml          { printf("XML opening: %s\n", yytext); }

dc:{dc_tag}    { printf("DC tag: %s\n", yytext); }
{tag}          { printf("Tag: %s\n", yytext); }

{attribute}    { printf("Attribute: %s\n", yytext); }
{dc_attribute} { printf("DC attribute: %s\n", yytext); }

{value}        { printf("Value: %s\n", yytext); }

.              { printf("?:%s\n", yytext); }

  /* "{less}\!DOCTYPE package PUBLIC ({quote}.*{quote})*" { printf("DOCTYPE: %s\n", yytext); } */

%%

int main(void){
  yylex();
  return 0;
}

int yywrap(void) {
  return 1;
}
