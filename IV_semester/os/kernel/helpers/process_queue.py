#!/usr/bin/env python

from kernel.kernel_data import *

# Internal queues abstraction for holding lists of processes
# based on their priorities 
class ProcessQueue:
    ''' Hold queues of processes based on their priorities 
        Internally it's a dict with priorities as key and each key points
            to queue of processes. 
        
        Examples:
            { 20 = [p1, p2], 30 =[p3, p4], 60 = [p5] }
            20, 30 and 60 are process priorities
            p1 .. p5 are processes
              p5 - has highest priority 
              p1 is waiting longer than p2 
    '''
    def __init__(self):
        self.dict = {}

    def add(self, process):
        ''' add process to the queque based on its priority '''
        if not process.priority in self.dict:
            self.dict[process.priority] = [process]
        else:
            self.dict[process.priority].append(process)

    def get(self):
        ''' get element with highest priority and one that's waiting most. 
              If queues are all empty, returns None
        '''
        for i in reversed(self.dict.keys()):
            if self.dict[i] != []:
                for proc in self.dict[i]:
                    if proc.awaiting_for_creation is None:
                        self.dict[i].remove(proc)
                        return proc
        return None

    def remove(self, process):
        ''' removes given process from list and returns True. 
            If process is not in the list, False is returned '''
        if process.priority in self.dict and process in self.dict[process.priority]:
            self.dict[process.priority].remove(process)
            return True
        else:
            return False

    def processes(self):
        ''' returns an array of all processes in the queue '''
        temp = []
        processes = self.dict.values()
        for i in processes:
            temp += i
        return temp

    def not_waiting_processes(self):
        ''' returns an array of processes that are not waiting for any resource '''
        temp = []
        processes = self.dict.values()
        for p_list in processes:
            for proc in p_list:
                if proc.awaiting_for_creation is None:
                    temp.append(proc)
        return temp
