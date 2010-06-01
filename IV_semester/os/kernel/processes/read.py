#!/usr/bin/env python

import sys
sys.path.append('.')

import configs
from real import channel_device
from real import data
from kernel.process import Process
from kernel import kernel_data
from kernel import kernel
from kernel.resources.task_at_input import TaskAtInput
from kernel.resources.input_device import InputDevice
from kernel.resources.kernel_memory import KernelMemory
from kernel.resources.channel1 import Channel1
from kernel.resources.task_at_sm import TaskAtSM

class Read(Process):
    def __init__(self, opts = {}):
        opts['name'] = 'read_' + str(kernel_data.PID)
        opts['priority'] = configs.LOADER_PRIORITY
        Process.__init__(self, opts)

    def run(self):
        ''' 
          Reads task from input device to kernel memory and creates
            resource task_at_sm
        '''
        while True:
            # get task
            if self.used_resources.get_by_class(TaskAtInput) is None:
                kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(TaskAtInput), self, TaskAtInput)
                return

            # get input device
            if self.used_resources.get_by_class(InputDevice) is None:
                kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(InputDevice), self, InputDevice)
                return

            # get kernel memory
            if self.used_resources.get_by_class(KernelMemory) is None:
                kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(KernelMemory), self, KernelMemory)
                return

            # get channel device
            if self.used_resources.get_by_class(Channel1) is None:
                kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(Channel1), self, Channel1)
                return
            
            # read data
            data.input_file = self.used_resources.get_by_class(TaskAtInput).file_path
            channel_device.read_from_in(True) 

            # create resource at_sm
            kernel.create_resource(TaskAtSM, self)

            # reverse order because after freeing resource it would be caught again
            # delete resource
            kernel.delete_resource(self.used_resources.get_by_class(TaskAtInput))
            #free resource
            kernel.free_resource(self.used_resources.get_by_class(KernelMemory), self, True)
            return

            kernel.free_resource(self.used_resources.get_by_class(Channel1), self, True)
            return 
