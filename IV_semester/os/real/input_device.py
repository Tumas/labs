#!/usr/bin/env python
'''
  Autoriai:
    Tumas Bajoras PS3
'''

"""
input_device - module that represent input device and imitates the low-level input.

The only thing that input device does - fills buffer with data from input device
"""

import sys
sys.path.append('.')
from configs import BLOCK_SIZE

import processor
import data

file_handler = None

def read(reset = False):
    """Performs low level reading. 
    
    Fills buffer with words from STDIN
    """

    # as soon as you try to update a class variable, that variable is automatically
    # considered local to the method, so we need to use global keyword
    global file_handler 

    # local variable to the method
    word = ""

    try:
        if reset or not file_handler:
            file_handler = open(data.input_file, "r")
        for i in range(BLOCK_SIZE):
            word = file_handler.readline()[0:4]  # read one word at a time
            if not word:
                file_handler.close()
                file_handler = None
                return 
            else:
                data.buffer[i] = word
    except IOError:
        processor.pi = 3       # fatal error
