#!/usr/bin/env python

import sys
sys.path.append('.')

import configs
from kernel.process import Process
from kernel import kernel_data
from kernel import kernel

from kernel.resources.task_data_at_sm import TaskDataAtSM
from kernel.resources.external_memory import ExternalMemory
from kernel.resources.channel3 import Channel3
from kernel.resources.task_at_hd import TaskAtHD
import real.channel_device

class JobToDisk(Process):
    def __init__(self, opts = {}):
        opts['name'] = 'job_to_disk_' + str(kernel_data.PID)
        opts['priority'] = configs.LOADER_PRIORITY
        Process.__init__(self, opts)

    def run(self):
        ''' move data from kernel memory to HD '''

        # get task data
        if self.used_resources.get_by_class(TaskDataAtSM) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(TaskDataAtSM), self, TaskDataAtSM)
            return

        # get external memory
        if self.used_resources.get_by_class(ExternalMemory) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(ExternalMemory), self, ExternalMemory)
            return

        # get channel3 
        if self.used_resources.get_by_class(Channel3) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(Channel3), self, Channel3)
            return

        # move data
        write_data = real.channel_device.sm_hd(True)

        kernel.create_resource(TaskAtHD, self, {'write_info' : write_data})
        kernel.delete_resource(self.used_resources.get_by_class(TaskDataAtSM))
        kernel.free_resource(self.used_resources.get_by_class(Channel3), self, True)
        return
