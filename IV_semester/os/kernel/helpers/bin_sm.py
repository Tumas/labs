#!/usr/bin/env python

class BinarySemaphore:
    ''' Basic binary semaphore implementation with no waiting.
      Basically, it's a simple true/false flag
    '''

    def __init__(self):
        self.s = 1

    def v(self):
        if self.s == 0:
            self.s += 1 
      
    def s(self):
        if self.s == 1:
            self.s -= 1
