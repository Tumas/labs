#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data
from kernel.helpers.bin_sm import BinarySemaphore

class Channel3(Resource):
    ''' '''

    def __init__(self, opts = {}):
        opts['name'] = 'channel3_' + str(kernel_data.RID)
        Resource.__init__(self, opts)
        self.sem = BinarySemaphore()

    def free(self):
        return self.sem.s == 1
