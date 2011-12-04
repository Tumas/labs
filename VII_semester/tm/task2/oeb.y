/* Tumas Bajoras, PS3. 4 kursas. Transliavimo metodai 2011 m. rudens semestras */
%{

#include <stdio.h>

extern char* yytext;
extern int   yylineno;

int  yylex(void);
void yyerror(char*);

%}

%start header

%token DOCTYPE XMLDEC
%token XML_ATTRIBUTE ID_ATTRIBUTE HREF_ATTRIBUTE
%token LESS MORE EQ QUOTE
%token CLOSE INLINE_CLOSE

/* package tags */
%token PACKAGE_TAG METADATA_TAG DC_METADATA_TAG MANIFEST_TAG REFERENCE_TAG SPINE_TAG GUIDE_TAG ITEM_TAG ITEM_REF_TAG

/* dc metadata tags */
%token DC_IDENTIFIER_TAG DC_TITLE_TAG DC_TYPE_TAG DC_CREATOR_TAG DC_CONTRIBUTOR_TAG

/* attributes */
%token PACKAGE_ATTRIBUTE DC_METADATA_ATTRIBUTE ITEM_ATTRIBUTE ITEM_REF_ATTRIBUTE REFERENCE_ATTRIBUTE

/* DC attributes */
%token DC_CREATOR_ATTRIBUTE

%token TAG_VALUE
%token ATTR_VALUE

%%

/* header */
header: xml_dec DOCTYPE package
      ;

xml_dec: XMLDEC xml_attr INLINE_CLOSE ;

/* package */
package: package_start metadata properties package_end
       ;

package_start:      LESS PACKAGE_TAG package_attributes MORE ;
package_end:        LESS CLOSE PACKAGE_TAG MORE ;
package_attributes: PACKAGE_ATTRIBUTE attr_value ;

/* metadata */
metadata: LESS METADATA_TAG MORE dc_metadata LESS CLOSE METADATA_TAG MORE ;
dc_metadata: LESS DC_METADATA_TAG dc_md_attributes MORE meta_tags CLOSE DC_METADATA_TAG MORE ;

dc_md_attributes: dc_md_attributes DC_METADATA_ATTRIBUTE attr_value  
                |
                ;

meta_tags: meta_tags meta_tag 
         |
         ;

meta_tag: dc_identifier
        | dc_type
        | dc_title
        | dc_creator
        | dc_contributor
        | LESS 
        ;

dc_identifier: LESS DC_IDENTIFIER_TAG dc_id_attribute tag_value CLOSE DC_IDENTIFIER_TAG MORE ; 
dc_id_attribute: ID_ATTRIBUTE attr_value ;

dc_creator: LESS DC_CREATOR_TAG dc_creator_attributes tag_value CLOSE DC_CREATOR_TAG MORE;
dc_creator_attributes: dc_creator_attributes DC_CREATOR_ATTRIBUTE attr_value 
                     |
                     ;

dc_type:        LESS DC_TYPE_TAG        tag_value CLOSE DC_TYPE_TAG MORE ;
dc_title:       LESS DC_TITLE_TAG       tag_value CLOSE DC_TITLE_TAG MORE ;
dc_contributor: LESS DC_CONTRIBUTOR_TAG tag_value CLOSE DC_CONTRIBUTOR_TAG MORE ;


/* properties */
properties: manifest spine guide;

/* manifest */
manifest: LESS MANIFEST_TAG MORE items CLOSE MANIFEST_TAG MORE; 
items: items LESS ITEM_TAG item_attributes INLINE_CLOSE
     |
     ;

item_attributes: item_attributes item_attr_name attr_value
               |
               ;

item_attr_name: ITEM_ATTRIBUTE
              | ID_ATTRIBUTE
              | HREF_ATTRIBUTE
              ;

/* spine */
spine: LESS SPINE_TAG MORE itemrefs CLOSE SPINE_TAG MORE;
itemrefs: itemrefs LESS ITEM_REF_TAG itemref_attribute INLINE_CLOSE
        |
        ;
itemref_attribute: ITEM_REF_ATTRIBUTE attr_value;

/* guide */
guide: LESS GUIDE_TAG MORE refs CLOSE GUIDE_TAG MORE 
     ;

refs: refs LESS REFERENCE_TAG ref_attributes INLINE_CLOSE
    | 
    ;

ref_attributes: ref_attributes ref_attr_name attr_value
              |
              ;

ref_attr_name: REFERENCE_ATTRIBUTE
             | HREF_ATTRIBUTE
             ;

xml_attr: XML_ATTRIBUTE attr_value ;
attr_value: EQ QUOTE ATTR_VALUE QUOTE;
tag_value: MORE TAG_VALUE LESS

%%

void yyerror(char* error) {
    printf("Error: %s; Data: %s\n", error, yytext);
}

int main() {
    return yyparse();
}
