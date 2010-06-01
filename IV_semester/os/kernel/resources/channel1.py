#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel.helpers.bin_sm import BinarySemaphore
from kernel import kernel_data

class Channel1(Resource):
    ''' '''

    def __init__(self, opts = {}):
        opts['name'] = 'channel1_' + str(kernel_data.RID)
        Resource.__init__(self, opts)
        self.sem = BinarySemaphore()

    def free(self):
        return self.sem.s == 1
        
