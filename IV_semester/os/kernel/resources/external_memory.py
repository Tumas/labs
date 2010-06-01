#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data
from kernel.helpers.bin_sm import BinarySemaphore

class ExternalMemory(Resource):
    ''' '''

    def __init__(self, opts = {}):
        opts['name'] = 'external_memory_' + str(kernel_data.RID)
        Resource.__init__(self, opts)
        self.sem = BinarySemaphore()

    def free(self):
        return self.sem.s == 1
