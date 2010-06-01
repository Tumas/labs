#!/usr/bin/env python

import sys
sys.path.append('.')

import configs
from kernel.process import Process
from kernel import kernel_data
from kernel import kernel

from kernel.resources.task_loaded import TaskLoaded
from kernel.processes.virtual_machine import VirtualMachine
from real import user_memory

class Manager(Process):
    def __init__(self, opts = {}):
        opts['name'] = 'manager_' + str(kernel_data.PID)
        opts['priority'] = configs.LOADER_PRIORITY
        Process.__init__(self, opts)

    def run(self):
        ''' create virtual machine '''

        # get load task
        if self.used_resources.get_by_class(TaskLoaded) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(TaskLoaded), self, TaskLoaded)
            return
        
        in_data = True 
        ds_length = 0
        cs_length = 0

        # manual ugly counting
        for address in self.used_resources.get_by_class(TaskLoaded).addresses:
            if user_memory.fetch(address) == "CODE":
                in_data = False
                cs_length -= 1

            if in_data:
                ds_length += 1
            else:
                cs_length += 1

        kernel.create_process(VirtualMachine, self, { 'ds_length' : ds_length, 'cs_length' : cs_length, 'addresses' : self.used_resources.get_by_class(TaskLoaded).addresses })
        kernel.delete_resource(self.used_resources.get_by_class(TaskLoaded))
