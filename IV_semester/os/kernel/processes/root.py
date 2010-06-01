#!/usr/bin/env python

import sys
import os
sys.path.append('.')

import configs

from kernel import kernel
from kernel import kernel_data
from kernel.process import Process
from kernel.resource import Resource
from kernel.helpers.process_queue import ProcessQueue
from kernel.helpers.resource_list import ResourceList

# resources 
from kernel.resources.input_device import InputDevice
from kernel.resources.output_device import OutputDevice
from kernel.resources.task_at_input import TaskAtInput
from kernel.resources.channel1 import Channel1
from kernel.resources.channel2 import Channel2
from kernel.resources.channel3 import Channel3
from kernel.resources.kernel_memory import KernelMemory 
from kernel.resources.user_memory import UserMemory
from kernel.resources.external_memory import ExternalMemory
from kernel.resources.terminate import Terminate
from kernel.resources.enough_space import EnoughSpace
from kernel.resources.tasks_found import TasksFound

# processes
from kernel.processes.read import Read
from kernel.processes.parse_task import ParseTask
from kernel.processes.job_to_disk import JobToDisk
from kernel.processes.job_to_mem import JobToMem
from kernel.processes.interrupt import Interrupt
from kernel.processes.print_p import Print
from kernel.processes.manager import Manager

class Root(Process):
    def __init__(self):
        Process.__init__(self, {'parent' : None, 'name' : 'root', 'priority' : configs.ROOT_PRIORITY })

        # init kernel
        kernel_data.BLOCKED_PROCESSES = ProcessQueue()
        kernel_data.READY_PROCESSES = ProcessQueue()
        kernel_data.RESOURCES = ResourceList()
        kernel_data.ACTIVE_PROCESS = self

        # create static resources
        for resource in [InputDevice, OutputDevice, UserMemory, ExternalMemory,
            Terminate, EnoughSpace, Channel3, Channel1, Channel2, KernelMemory]:
            kernel.create_resource(resource, self)

        # create dynamic resources
        # for that many files in tasks folder create task_at_input resource
        counter = 0
        for file in os.listdir('tasks'):
            kernel.create_resource(TaskAtInput, self, {'file_path' : 'tasks/' + str(file) })
            counter += 1

        # create child processes
        for process in [Interrupt, Print]:
            kernel.create_process(process, self)

        #create resources if there are tasks to execute
        if kernel_data.RESOURCES.is_instance_created(TaskAtInput):
            for process in [Read, ParseTask, JobToDisk, JobToMem, Manager]:
                kernel.create_process(process, self)
        
        # create tasks_found resource
        if counter != 0:
            kernel.create_resource(TasksFound, self, {'count' : counter})

    def run(self):
        ''' '''
        if self.used_resources.get_by_class(Terminate) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(Terminate), self, Terminate)
            return 

        kernel.terminate_process(self)

if __name__ == '__main__':
    r = Root()
    # start os
    while kernel_data.RESOURCES.is_instance_created(Terminate):
      kernel_data.ACTIVE_PROCESS.run()
