#!/usr/bin/env python

import sys
sys.path.append('.')
from configs import *

import kernel
from kernel import kernel_data
from helpers.process_queue import ProcessQueue
from helpers import logger

''' Resource class '''

class Resource:
    def __init__(self, opts): 
        '''expects one dictionary full of params.
        
          opts should have these fields:
            name : visual identified of resource
            creator : process that created this resource
        '''
        self.rid = kernel_data.RID
        kernel_data.RID += 1
        self.name = opts['name']
        self.creator = opts['creator']
        self.awaiting_processes = ProcessQueue()
  
    # Resurso planuotojas
    def distribute(self):
        ''' Algorithm that distributes resource. (Resurso planuotojas) '''
        logger.log("DISTRIBUTOR called on: " + self.name)
        if self.free():
            logger.log("GOT TRUE")
            pr = self.awaiting_processes.get()
            # if there are processes waiting for this resource
            # give resource to them
            if pr is not None:
                pr.used_resources.add(self)
                pr.make_ready()
                self.awaiting_processes.remove(pr)
                # lock_with_semaphore when asking_for_resoure
                # if resource is guarded with semaphore create lock 
                if hasattr(self, 'sem'):
                    self.sem.v() 
                    logger.log("LOCKED SEMAPHORE on " + self.name)
        else:
            kernel_data.ACTIVE_PROCESS.state = BLOCKED_STATE 
            self.awaiting_processes.add(kernel_data.ACTIVE_PROCESS)
            logger.log("GOT FALSE")
        kernel.schedule()

    def free(self):
        ''' returns True if is freed and false otherwise '''
        return True
