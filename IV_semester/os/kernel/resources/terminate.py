#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data
from kernel.resources.task_completed import TaskCompleted
from kernel.resources.tasks_found import TasksFound

class Terminate(Resource):
    ''' Resources that is needed to terminate OS work. 
    
        This resources is freed to root when there is only root
         process left 
    '''

    def __init__(self, opts = {}):
      opts['name'] = 'terminate_' + str(kernel_data.RID)
      Resource.__init__(self, opts)


    def free(self):
        ''' resource is freed when all user tasks are completed'''
        return kernel_data.RESOURCES.count_instances(TaskCompleted) == kernel_data.RESOURCES.get_by_class(TasksFound).count
