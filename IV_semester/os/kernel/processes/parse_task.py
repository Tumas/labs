#!/usr/bin/env python

import sys
sys.path.append('.')

import configs
from kernel.process import Process
from kernel import kernel_data
from kernel import kernel
from real import data

from kernel.resources.task_at_sm import TaskAtSM
from kernel.resources.task_data_at_sm import TaskDataAtSM
from kernel.resources.line_to_print import LineToPrint

class ParseTask(Process):
    def __init__(self, opts = {}):
        opts['name'] = 'parse_task_' + str(kernel_data.PID)
        opts['priority'] = configs.LOADER_PRIORITY
        Process.__init__(self, opts)

    def run(self):
        ''' interprets loaded task '''

        # get task
        if self.used_resources.get_by_class(TaskAtSM) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(TaskAtSM), self, TaskAtSM)
            return
        
        if data.SM[-1][:3] != "END" or data.SM[0][:4] != "DATA" or not data.SM.__contains__("CODE"):
            kernel.create_resource(LineToPrint, self, {'data' : "PARSING TASK FAILED: task name: " + self.used_resources.get_by_class(TaskAtSM).name + 'at: ' + str(data.SM[0][:3]) + '\n' })
            kernel.delete_resource(self.used_resources.get_by_class(TaskAtSM))
            return

        kernel.create_resource(TaskDataAtSM, self)
        kernel.delete_resource(self.used_resources.get_by_class(TaskAtSM))
