#!/usr/bin/env python

import sys
sys.path.append('.')

import configs
from kernel.process import Process
from kernel import kernel_data
from kernel import kernel
from real import processor

from kernel.resources.interrupt_event import InterruptEvent
from kernel.resources.line_to_print import LineToPrint
from kernel.resources.task_completed import TaskCompleted

class Interrupt(Process):
    def __init__(self, opts = {}):
        opts['name'] = 'interrupt_' + str(kernel_data.PID) 
        opts['priority'] = configs.INTERRUPT_PRIORITY
        Process.__init__(self, opts)

    def run(self):
        ''' Waits for InterruptEvent resource, identifies it and deals with it  '''

        # get interrupt event 
        if self.used_resources.get_by_class(InterruptEvent) is None:
            kernel.ask_for_resource(kernel_data.RESOURCES.get_by_class(InterruptEvent), self, InterruptEvent)
            return

        interrupt_event = self.used_resources.get_by_class(InterruptEvent)

        if processor.pi != 0:
            rOpts = {}
            
            '''
                    Don't forget to change messages into less lame ones.
                    Also need to think of what to do with the offending processes.
            '''
            
            if processor.pi == 1: #Some bad memory thingie
                rOpts['data'] = 'Bad memory thingie message: '  
            elif processor.pi == 2: #Illegal operation
                rOpts['data'] = 'Illegal operation: '
            elif processor.pi == 3: #Critical system error
                rOpts['data'] = 'Critical system error: '
            
            rOpts['data'] += interrupt_event.message
            kernel.create_resource(LineToPrint, self, rOpts)
            kernel.create_resource(TaskCompleted, self)
            kernel.terminate_process(self.used_resources.get_by_class(InterruptEvent).process) 
            processor.pi = 0
        
        elif processor.si != 0:
                
            if processor.si == 1: #GD
                    #???
                    pass
            elif processor.si == 2: #PD
                    #???
                    pass
            elif processor. si == 3: #END statement found
                    kernel.terminate_process(self.used_resources.get_by_class(InterruptEvent).process) 
                    kernel.create_resource(TaskCompleted, self)

            processor.si = 0
        
        # this actually wrong : ioi interrupts don't work this way. See docs
        #elif processor.ioi != 0:
        #    print "IOI"
        #    rOpts = {}
        #    rOpts['data'] = '' #find out where to get data
        #    kernel.create_resource(LineToPrint, self, rOpts)
        #    processor.ioi = 0
        
        elif processor.ti != 0: #blocks process when time runs out 
            processor.ti = 0
            processor.time = 0
            # this is not needed: process was already blocked at this time
            #self.used_resources.get_by_class(InterruptEvent).process.make_blocked()

        # if VM was terminated, the resource it created will no longer exist
        if not self.used_resources.get_by_class(InterruptEvent) is None: 
            kernel.delete_resource(self.used_resources.get_by_class(InterruptEvent))
