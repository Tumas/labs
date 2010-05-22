CC = gcc
CFLAGS = -Wall
OBJECTS = main.o list.o strlist.o
oop: $(OBJECTS)
	$(CC) $(CFLAGS) $(OBJECTS) -o oop
main.o: main.c strlist.o list.o
	gcc -c main.c
strlist.o: strlist.c strlist.h list.o 
	gcc -c strlist.c
list.o:	list.c list.h
	gcc -c list.c
