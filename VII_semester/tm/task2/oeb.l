/* Tumas Bajoras, PS3. 4 kursas. Transliavimo metodai 2011 m. rudens semestras */
%{

#include <stdio.h>
#include "y.tab.h"

%}

%x ATTRIBUTES ATTRVAL TAGVAL

less  <
more  >
eq    =
quote \"
quoted \"[^\"]+\"
white \ |\n|\r\n|\t

xml_attribute         version
package_attribute     unique-identifier
dc_metadata_attribute xmlns:dc|xmlns:oebpackage

item_attribute        media-type
item_ref_attribute    idref
reference_attribute   type|title|href
id_attribute          id
href_attribute        href

  /* metadata info */
  /* TODO: dc_attribute event */
dc_creator_attribute role|file-as


%%

<INITIAL>{
    /* XML + doctype declarations */
  {less}\?xml                { printf("XML opening: %s\n", yytext); BEGIN ATTRIBUTES ; return XMLDEC; }
  {less}\!DOCTYPE\ package\ PUBLIC\ {quoted}\ {quoted}{more} { printf("DOCTYPE declaration: %s\n", yytext); return DOCTYPE; }

    /* Tags for package + package itself */
  package     { printf("Package tag: %s\n", yytext); BEGIN ATTRIBUTES; return PACKAGE_TAG; } 
  metadata    { printf("Metadata tag: %s\n", yytext); BEGIN ATTRIBUTES; return METADATA_TAG; }
  dc-metadata { printf("Dc Metadata tag: %s\n", yytext); BEGIN ATTRIBUTES; return DC_METADATA_TAG; }
  manifest    { printf("Manifest tag: %s\n", yytext); BEGIN ATTRIBUTES; return MANIFEST_TAG; }
  reference   { printf("Reference tag: %s\n", yytext); BEGIN ATTRIBUTES; return REFERENCE_TAG; }
  item        { printf("Item tag: %s\n", yytext); BEGIN ATTRIBUTES; return ITEM_TAG; }
  spine       { printf("Spine tag: %s\n", yytext); BEGIN ATTRIBUTES; return SPINE_TAG; }
  itemref     { printf("Itemref tag: %s\n", yytext); BEGIN ATTRIBUTES; return ITEM_REF_TAG; }
  guide       { printf("Guide tag: %s\n", yytext); BEGIN ATTRIBUTES; return GUIDE_TAG; }

    /* Tags for metadata description 
        Subject|Description|Published|Type|Format|Source|Language|Relation|Coverage|Rights
    */
  dc:Identifier  { printf("DC Identifer tag: %s\n", yytext); BEGIN ATTRIBUTES; return DC_IDENTIFIER_TAG; }
  dc:Title       { printf("DC Title tag: %s\n", yytext); BEGIN ATTRIBUTES; return DC_TITLE_TAG; }
  dc:Type        { printf("DC Type tag: %s\n", yytext); BEGIN ATTRIBUTES; return DC_TYPE_TAG; }
  dc:Creator     { printf("DC Creator tag: %s\n", yytext); BEGIN ATTRIBUTES; return DC_CREATOR_TAG; }
  dc:Contributor { printf("DC Contributor tag: %s\n", yytext); BEGIN ATTRIBUTES; return DC_CONTRIBUTOR_TAG; }

  {less}?\/ { printf("Closing tag: %s\n", yytext); return CLOSE; }
  {less}    { printf("LESS INITIAL\n"); return LESS; }
  {more}    { printf("MORE\n"); return MORE; }
  .
}

<TAGVAL>{
  {less} { printf("LESS TAGVAL\n"); BEGIN INITIAL; return LESS; }
  [^<]+  { 
      printf("Tag value: %s\n", yytext); 
      return TAG_VALUE; 
    }
}

<ATTRIBUTES>{
  {eq}           { printf("Equality: %s\n", yytext); return EQ; }
  {quote}        { printf("Quote: %s\n", yytext); BEGIN ATTRVAL; return QUOTE; }

  {xml_attribute}         { printf("XML attribute: %s\n", yytext); return XML_ATTRIBUTE; }
  {id_attribute}          { printf("ID attribute: %s\n", yytext); return ID_ATTRIBUTE; }
  {href_attribute}        { printf("HREF attribute: %s\n", yytext); return HREF_ATTRIBUTE; }
  {package_attribute}     { printf("Package attribute: %s\n", yytext); return PACKAGE_ATTRIBUTE; }
  {item_attribute}        { printf("Item attribute: %s\n", yytext); return ITEM_ATTRIBUTE; }
  {item_ref_attribute}    { printf("Itemref attribute: %s\n", yytext); return ITEM_REF_ATTRIBUTE; }
  {reference_attribute}   { printf("Reference Attribute: %s\n", yytext); return REFERENCE_ATTRIBUTE; }
  {dc_metadata_attribute} { printf("DC metadata attribute: %s\n", yytext); return DC_METADATA_ATTRIBUTE; }

  /* dc metadata attributes */
  {dc_creator_attribute} { printf("DC Creator attribute: %s\n", yytext); return DC_CREATOR_ATTRIBUTE; }

  [\/\?]{more}   { printf("Inline closing tag: %s\n", yytext); BEGIN INITIAL; return INLINE_CLOSE; }
  {more}{white}* { printf("MORE\n"); BEGIN TAGVAL; return MORE; } 
  .
}

<ATTRVAL>{
  {quote} { printf("Quote: %s\n", yytext); BEGIN ATTRIBUTES; return QUOTE; }
  [^\"]+  { printf("Attribute value: %s\n", yytext); return ATTR_VALUE; }
}

%%

int yywrap(void) {
  return 1;
}
