#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data
from kernel.helpers.bin_sm import BinarySemaphore

class InputDevice(Resource):
    ''' Shows whether input device is available '''

    def __init__(self, opts = {}):
        opts['name'] = 'input_device_' + str(kernel_data.RID)
        Resource.__init__(self, opts)
        self.sem = BinarySemaphore()

    def free(self):
        return self.sem.s == 1
