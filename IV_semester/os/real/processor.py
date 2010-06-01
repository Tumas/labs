#!/usr/bin/env python
'''
  Autoriai:
    Tumas Bajoras PS3
'''

import math

""" processor - module that represent a processor """

# registers for the vm
cs = "0000" 
ds = "0000" 
# page table register
prt = "0000" 
# channel registers
chst = [0, 0, 0]
# common usage register
r = ["0000", "0000"]
# time register
time = 0
# interrupt registers
ioi = 0
si = 0
ti = 0
pi = 0
# instruction counter & logical trigger
ic = 0
c = 0
# processor mode register 1 = VM state
mode = 0

# Formatting instructions
# takes word (hex) and converts to int
to_int = lambda x: int(str(x), 16) 

# takes int and returns hex representation in format: XXXX => 00FF 
to_hex = lambda x: "0" * (4 - len(hex(int(math.fabs(x)))[2:].upper())) + hex(int(math.fabs(x)))[2:].upper()
