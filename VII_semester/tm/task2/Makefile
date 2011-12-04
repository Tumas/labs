# Tumas Bajoras, PS3. Transliavimo metodai 2011 m. rudens semestras

y.tab.c: oeb.y 
	yacc -d oeb.y

y.tab.h: oeb.y
	yacc -d oeb.y

lex.yy.c: oeb.l y.tab.h
	lex oeb.l

parser: lex.yy.c y.tab.h y.tab.c
	gcc lex.yy.c y.tab.c -o parser

