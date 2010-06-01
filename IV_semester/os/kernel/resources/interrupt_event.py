#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data

class InterruptEvent(Resource):
    ''' '''

    def __init__(self, opts = {}):
        opts['name'] = 'interrupt_' + str(kernel_data.RID)
        self.process = opts['process']
        Resource.__init__(self, opts)
        self.message = opts['message']
