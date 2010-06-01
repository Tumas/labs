#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data

class TaskCompleted(Resource):
    ''' 
      Resource needed to retrieve information on how many wm tasks have been completed
    '''

    def __init__(self, opts = {}):
      opts['name'] = 'task_completed_' + str(kernel_data.RID) 
      Resource.__init__(self, opts)
