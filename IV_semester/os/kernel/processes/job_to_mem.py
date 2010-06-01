#!/usr/bin/env python

import sys
sys.path.append('.')

import configs
from kernel.process import Process
from kernel import kernel_data
from kernel import kernel
from kernel.resources.task_loaded import TaskLoaded
from kernel.resources.enough_space import EnoughSpace
from kernel.resources.channel3 import Channel3
from kernel.resources.task_at_hd import TaskAtHD
from real import channel_device
from real import user_memory

class JobToMem(Process):
    def __init__(self, opts = {}):
        opts['name'] = 'job_to_mem_' + str(kernel_data.PID)
        opts['priority'] = configs.LOADER_PRIORITY
        Process.__init__(self, opts)

    def run(self):
        ''' move task from HD to User memory '''

        # get task loaded
        if self.used_resources.get_by_class(TaskAtHD) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(TaskAtHD), self, TaskAtHD)
            return

        # TODO - interaction with TaskAtHD for asking space
        # Enough-space is single resource that when asked checks if there is enough space on user memory
        # and reserves it
        # get enough space
        #if self.used_resources.get_by_class(EnoughSpace) is None:
        #    kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(EnoughSpace), self, EnoughSpace)
        #    return

        # get channel3 
        if self.used_resources.get_by_class(Channel3) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(Channel3), self, Channel3)
            return

        addresses =  channel_device.hd_to_um(self.used_resources.get_by_class(TaskAtHD).write_info)

        if addresses is not None:
            kernel.create_resource(TaskLoaded, self, {'addresses' : addresses })
            kernel.delete_resource(self.used_resources.get_by_class(TaskAtHD))

        #kernel.free_resource(self.used_resources.get_by_class(EnoughSpace), self)
        #return 
        kernel.free_resource(self.used_resources.get_by_class(Channel3), self, True)
        return
