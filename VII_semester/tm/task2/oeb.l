/* Tumas Bajoras, PS3. 4 kursas. Transliavimo metodai 2011 m. rudens semestras */

%option noyywrap
%option yylineno

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
  {less}\?xml                                                { BEGIN ATTRIBUTES ; return XMLDEC; }
  {less}\!DOCTYPE\ package\ PUBLIC\ {quoted}\ {quoted}{more} { return DOCTYPE; }

    /* Tags for package + package itself */
  package     { BEGIN ATTRIBUTES; return PACKAGE_TAG; } 
  metadata    { BEGIN ATTRIBUTES; return METADATA_TAG; }
  dc-metadata { BEGIN ATTRIBUTES; return DC_METADATA_TAG; }
  manifest    { BEGIN ATTRIBUTES; return MANIFEST_TAG; }
  reference   { BEGIN ATTRIBUTES; return REFERENCE_TAG; }
  item        { BEGIN ATTRIBUTES; return ITEM_TAG; }
  spine       { BEGIN ATTRIBUTES; return SPINE_TAG; }
  itemref     { BEGIN ATTRIBUTES; return ITEM_REF_TAG; }
  guide       { BEGIN ATTRIBUTES; return GUIDE_TAG; }

    /* Tags for metadata description 
        Subject|Description|Published|Type|Format|Source|Language|Relation|Coverage|Rights
    */
  dc:Identifier  { BEGIN ATTRIBUTES; return DC_IDENTIFIER_TAG; }
  dc:Title       { BEGIN ATTRIBUTES; return DC_TITLE_TAG; }
  dc:Type        { BEGIN ATTRIBUTES; return DC_TYPE_TAG; }
  dc:Creator     { BEGIN ATTRIBUTES; return DC_CREATOR_TAG; }
  dc:Contributor { BEGIN ATTRIBUTES; return DC_CONTRIBUTOR_TAG; }

  {less}?\/ { return CLOSE; }
  {less}    { return LESS; }
  {more}    { return MORE; }
  .
}

<TAGVAL>{
  {less} { BEGIN INITIAL; return LESS; }
  [^<]+  { return TAG_VALUE; }
}

<ATTRIBUTES>{
  {eq}           { return EQ; }
  {quote}        { BEGIN ATTRVAL; return QUOTE; }

  {xml_attribute}         { return XML_ATTRIBUTE; }
  {id_attribute}          { return ID_ATTRIBUTE; }
  {href_attribute}        { return HREF_ATTRIBUTE; }
  {package_attribute}     { return PACKAGE_ATTRIBUTE; }
  {item_attribute}        { return ITEM_ATTRIBUTE; }
  {item_ref_attribute}    { return ITEM_REF_ATTRIBUTE; }
  {reference_attribute}   { return REFERENCE_ATTRIBUTE; }
  {dc_metadata_attribute} { return DC_METADATA_ATTRIBUTE; }

  /* dc metadata attributes */
  {dc_creator_attribute} { return DC_CREATOR_ATTRIBUTE; }

  [\/\?]{more}   { BEGIN INITIAL; return INLINE_CLOSE; }
  {more}{white}* { BEGIN TAGVAL; return MORE; } 
  .
}

<ATTRVAL>{
  {quote} { BEGIN ATTRIBUTES; return QUOTE; }
  [^\"]+  { return ATTR_VALUE; }
}

%%
