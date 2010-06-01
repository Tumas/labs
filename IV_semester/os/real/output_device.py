#!/usr/bin/env python

import sys

""" output_device - module that represent output device
OUTPUT - class data for machine's output. All data is being written to this variable.
"""


def write(word):
    """write word to the output"""
	
    if word is not None and len(word) > 0:
        word += '\n' 
    sys.stdout.write(word)
