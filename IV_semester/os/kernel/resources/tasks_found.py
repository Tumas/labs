#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data

class TasksFound(Resource):
    '''  
        Resource needed to retrieve information on how many wm tasks have been found
    '''

    def __init__(self, opts = {}):
      opts['name'] = 'tasks_found_' + '_' + str(opts['count']) + str(kernel_data.RID) 
      Resource.__init__(self, opts)
      self.count = opts['count']
