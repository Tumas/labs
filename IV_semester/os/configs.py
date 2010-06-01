#!/usr/bin/env python

''' This module provides configuration options for OS project. No more magic numbers! '''

BLOCK_SIZE = 16   # words
WORD_SIZE = 4 # bytes

# length od RS in blocks
RESTRICTED_LENGTH = 1

# length of DS in blocks
DS_LENGTH = 6

# timer value
TIMER_VALUE = 10

# buffer size
BUFFER_SIZE = 16

# number of blocks in HD
HD_BLOCKS_SIZE = 500

# default priorities
ROOT_PRIORITY = 40
VM_PRIORITY = 50
LOADER_PRIORITY = 60
INTERRUPT_PRIORITY = 70
PRINT_PRIORITY = 70

# Process states
RUNNING_STATE = 'running' 
READY_STATE = 'ready'
BLOCKED_STATE = 'blocked'

# Page tables
PAGE_TABLE_STARTING_BLOCK = 0
PAGE_TABLE_ENDING_BLOCK = 14
# Shared memory
SH_MEMEORY_STARTING_BLOCK = 15
SH_MEMORY_ENDING_BLOCK = 31

# blocks dedicated for user tasks are from
USER_STARTING_BLOCK = 32
USER_ENDING_BLOCK = 255
