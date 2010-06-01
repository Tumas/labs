#!/usr/bin/env python

import sys
sys.path.append('.')

import configs
from kernel.process import Process
from kernel import kernel_data
from kernel import kernel

from kernel.resources.channel2 import Channel2
from kernel.resources.line_to_print import LineToPrint
from real import output_device


class Print(Process):
    def __init__(self, opts = {}):
        opts['name'] = 'print_' + str(kernel_data.PID)
        opts['priority'] = configs.PRINT_PRIORITY
        Process.__init__(self, opts)

    def run(self):
        while True:
            if self.used_resources.get_by_class(LineToPrint) is None:
                kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(LineToPrint), self, LineToPrint)
                return

            output_device.write(self.used_resources.get_by_class(LineToPrint).data_to_print)

            kernel.delete_resource(self.used_resources.get_by_class(LineToPrint))
