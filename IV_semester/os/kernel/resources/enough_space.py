#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data

class EnoughSpace(Resource):
    ''' '''

    def __init__(self, opts = {}):
      opts['name'] = 'enough_space_' + str(kernel_data.RID)
      Resource.__init__(self, opts)
