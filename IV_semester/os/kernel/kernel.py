#!/usr/bin/env python

import sys
sys.path.append('.')
from configs import *

import kernel_data
import kernel
from helpers import logger

# process primitives
def create_process(proc_class, father_proc, opts = {}):
    ''' creates a process
      
      1. create instance 
      2. created instance is added to ready processes list
    '''
    opts['parent'] = father_proc
    process = proc_class(opts) # parent option must be set
    father_proc.children.append(process)
    process.make_ready()
    logger.log("Process created: " + process.name)


def terminate_process(process):
    '''
      1. child processes are terminated
      2. resources are freed?
      3. resources created by this process are destroyed
      4. process is removed from its parents child processes list
      5. process is removed from kernel process list
    '''
    logger.log("Process terminated: " + process.name)
    for child in process.children:
        kernel.terminate_process(child)

    temp = process.used_resources.resources()
    for res in temp:
        kernel.free_resource(res, process)

    temp = process.created_resources.resources()
    for res in temp:
        kernel.delete_resource(res)

    if not process.__class__.__name__ == 'Root':
        process.parent.children.remove(process)

    kernel_data.READY_PROCESSES.remove(process) or kernel_data.BLOCKED_PROCESSES.remove(process)

# resource primitives
def create_resource(res_class, process, opts = {}):
    ''' create resource 

      1. create new instance of resource
      2. created resource is added to creators created resources list
      3. created resource is added to kernels resources list 
        
    '''
    opts['creator'] = process
    resource = res_class(opts) 
    process.created_resources.add(resource)
    kernel_data.RESOURCES.add(resource)
    logger.log("Resource created: " + resource.name)

    # update blocked processes
    for proc in kernel_data.BLOCKED_PROCESSES.processes():
        if proc.is_waiting_for(res_class):
            proc.awaiting_for_creation = None
            proc.make_ready()

def delete_resource(resource):
    ''' 
        1. remove resource from creators resources list
        2. unblock processes avaiting this resource
        3. remove resource from resources 
    '''
    logger.log("Resource deleted: " + resource.name)
    resource.creator.created_resources.remove(resource)
    kernel_data.ACTIVE_PROCESS.used_resources.remove(resource)
    for p in resource.awaiting_processes.processes():
        p.make_ready()
    kernel_data.RESOURCES.remove(resource)
    #resource.distribute()

def ask_for_resource(resource, process, res_class):
    ''' 
        1. process is blocked and added to resources awaiting processes list
        2. resource planner is called
    ''' 
    if resource is not None:
        logger.log("Process: " + process.name + " asked for resource " + resource.name)
        resource.awaiting_processes.add(process)
        process.awaiting_for_creation = None
        resource.distribute()
    else:
        # there is no such resource created
        logger.log("Process: " + process.name + " asked for resource that is not yet created")
        kernel_data.ACTIVE_PROCESS.state = BLOCKED_STATE
        logger.log(process.name + " now waits for resource " + str(res_class) +" to be created")
        process.awaiting_for_creation = res_class
        kernel.schedule()


def free_resource(resource, process, semaphore_guarded = False):
    '''
        1. process is removed from resources awaiting processes list
        1.1. resource is removed from process used_resources list
        2. resource planner is called
    '''
    logger.log("Resource " + resource.name + " freed by " + process.name)
    resource.awaiting_processes.remove(process)
    process.used_resources.remove(resource)

    # if process is guarded with semaphore, release the as well lock
    if semaphore_guarded:
        resource.sem.v()
        logger.log("RELEASED SEMAPHORE LOCK on: " + resource.name)
    resource.distribute()

# scheduler
def schedule():
    ''' 
      1. change active process if active is blocked
    '''
    if kernel_data.ACTIVE_PROCESS.state == BLOCKED_STATE:
        kernel_data.BLOCKED_PROCESSES.add(kernel_data.ACTIVE_PROCESS)

    if kernel_data.READY_PROCESSES.not_waiting_processes() != [] and kernel_data.ACTIVE_PROCESS.state != RUNNING_STATE:
        p = kernel_data.READY_PROCESSES.get()
        p.state = RUNNING_STATE
        kernel_data.ACTIVE_PROCESS = p
    else: # there only blocked processes are left 
        # change active process only if it was blocked
        if kernel_data.ACTIVE_PROCESS.state == BLOCKED_STATE:
            kernel_data.ACTIVE_PROCESS = kernel_data.BLOCKED_PROCESSES.get()
            kernel_data.ACTIVE_PROCESS.state = RUNNING_STATE

        # cleaner 
          # checks all resources and if it is free, makes processes that are waiting for it ready
        logger.log("CLEANER ACTIVATED: ")
        for res in kernel_data.RESOURCES.resources():
            if res.free():
                for proc in kernel_data.BLOCKED_PROCESSES.processes():
                    if not len(res.awaiting_processes.processes()) == 0:
                        lucky_process = res.awaiting_processes.get()
                        lucky_process.make_ready()
