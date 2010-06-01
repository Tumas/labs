#!/usr/bin/env python

import sys
sys.path.append('.')
from configs import *

# aka supervisor memory 
SM = []

# buffer
buffer = [[]] * BLOCK_SIZE

# hd blocks
HD_BLOCKS = {} 

# input_file
input_file = None
