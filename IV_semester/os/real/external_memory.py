#!/usr/bin/env python

'''
  Autoriai:
    Tumas Bajoras PS3
'''

import sys
sys.path.append('.')
from configs import *
from data import *

import user_memory

"""external_memory - module that represents external memory.
External memory consists of 500 blocks

HD_PATH - path to a file that implements external memory
"""

HD_PATH = "devices/hd"
file_handler = None

def fetch(block_number, word_number = 0):
    """fetch given word from specified block in external memory"""

    global HD_PATH
    global file_handler

    if block_number < 0 or block_number > HD_BLOCKS_SIZE - 1:
        raise Exception('External memory access failure: invalid block size specified on fetch')
    if word_number < 0 or word_number > BUFFER_SIZE - 1: 
        raise Exception('External memory access failure: invalid word size specified on fetch')
    if not block_number * BLOCK_SIZE + word_number in HD_BLOCKS:
        return ""
    
    data = ""
    try: 
        if not file_handler or file_handler.mode != "r":
            file_handler = open(HD_PATH, "r")
        file_handler.seek((block_number * BLOCK_SIZE + word_number)* WORD_SIZE)
        data = file_handler.read(4)
        file_handler.seek(0)
        return data
    except IOError:
        print "Whoops! External memory access failure"
         

def write(word_to_write, block_number, word_number):
    """write given word to the specified place in the external memory"""

    global HD_PATH
    global file_handler

    if word_to_write == []:
        return False 

    try:
        if not file_handler or file_handler.mode != 'w':
            file_handler = open(HD_PATH, "w")
        if not block_number * BLOCK_SIZE + word_number in HD_BLOCKS:
            file_handler.seek((block_number * BLOCK_SIZE + word_number)* WORD_SIZE)
            file_handler.write(word_to_write)
            file_handler.seek(0)
            HD_BLOCKS[block_number * BLOCK_SIZE + word_number] = 1
        else:
            return False
    except IOError:
        print "Whoops! External memory access failure"

if __name__ == "__main__":
  for i in ["aaaa", "bbbb", "cccc"]:
      write(i, 2, len(i))

  for i in range(16):
      print fetch(2, i)
