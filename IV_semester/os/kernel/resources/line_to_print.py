#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data

class LineToPrint(Resource):
    def __init__(self, opts = {}):
        opts['name'] = 'line_to_print_' + str(kernel_data.RID)
        Resource.__init__(self, opts)
        self.data_to_print = opts['data'] 
