#!/usr/bin/env python

import sys
sys.path.append('.')

from kernel.resource import Resource
from kernel import kernel_data
from kernel.helpers.bin_sm import BinarySemaphore
from kernel.resources.task_at_sm import TaskAtSM
from kernel.resources.task_data_at_sm import TaskDataAtSM

class KernelMemory(Resource):
    ''' '''

    def __init__(self, opts = {}):
        opts['name'] = 'kernel_memory_' + str(kernel_data.RID)
        Resource.__init__(self, opts)
        self.sem = BinarySemaphore()

    def free(self):
        # another lock if it has some data in it and cannot be overwritten
        return self.sem.s == 1 and kernel_data.RESOURCES.get_by_class(TaskAtSM) is None and kernel_data.RESOURCES.get_by_class(TaskDataAtSM) is None
