program_NAME := server

CC = gcc
CFLAGS += -W -Wall
#CFLAGS += -Wall -ansi
OBJECTS = main.c server.c source.c client.c ../helpers.c ../protocol.c

all: $(program_NAME)

$(program_NAME): $(program_OBJS)
	$(LINK.c) $(OBJECTS) -o $(program_NAME)
