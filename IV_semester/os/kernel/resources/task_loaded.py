#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data
from kernel.helpers.bin_sm import BinarySemaphore

class TaskLoaded(Resource):
    ''' '''

    def __init__(self, opts = {}):
        opts['name'] = 'task_loaded_' + str(kernel_data.RID)
        Resource.__init__(self, opts)
        self.sem = BinarySemaphore()
        self.addresses = opts['addresses']

    def free(self):
        return self.sem.s == 1
