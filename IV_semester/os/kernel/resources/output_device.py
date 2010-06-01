#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data

class OutputDevice(Resource):
    ''' Shows whether output device is available '''

    def __init__(self, opts = {}):
      opts['name'] = 'output_device_' + str(kernel_data.RID)
      Resource.__init__(self, opts)
      # no need of semaphore. only print should use it
