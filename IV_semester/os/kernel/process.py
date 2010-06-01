#!/usr/bin/env python

import sys
sys.path.append('.')

import kernel_data 
from configs import *
from real import processor
from helpers.resource_list import ResourceList
from helpers import logger

''' Process class '''

class Process:
    def __init__(self, opts): 
        ''' Expects one big dictionary full of params
        
            opts should have these fields:
                name : visual identified of process
                parent : process that created this process
                priority : priority value declared in configs.py
        '''
        self.pid = kernel_data.PID
        kernel_data.PID += 1
        self.parent = opts['parent']
        self.name = opts['name']

        # saves a dict as a context:
        self.context = {}
        self.context['cs'] = processor.cs 
        self.context['ds'] = processor.ds 
        self.context['r'] = processor.r
        self.context['ic'] = processor.ic
        self.context['c'] = processor.c
        self.context['chst'] = processor.chst

        # page table should be needed only for vm process 
        # self.page_table  = 

        self.mode = processor.mode
        self.priority = opts['priority']
        self.state = READY_STATE

        self.used_resources = ResourceList()
        self.created_resources = ResourceList()
        self.children = [] 
        self.awaiting_for_creation = None

    def run(self):
        ''' run process : actual implementation of process'''
        pass

    def make_ready(self):
        ''' Little helper that makes a process ready:
            
            1. removes it from blocked processes list if it's in list (ex. not in list if active)
            2. adds it to ready processes list
            3. changes process state to ready
        '''
        if self in kernel_data.BLOCKED_PROCESSES.processes():
            kernel_data.BLOCKED_PROCESSES.remove(self) 
        kernel_data.READY_PROCESSES.add(self)
        self.state = READY_STATE
        if not kernel_data.ACTIVE_PROCESS != self:
            logger.log("Process " + self.name + " was made ready")

    def make_blocked(self):
        ''' Little helper that removes process from a ready list if it's in there and makes the process ready '''
        if self in kernel_data.READY_PROCESSES.processes():
            kernel_data.READY_PROCESSES.remove_process(self)
        kernel_data.BLOCKED_PROCESSES.add(self)
        self.state = BLOCKED_STATE
        logger.log("Process: " + self.name + " was blocked")

    def is_waiting_for(self, resource_class):
        ''' returns true if process is waiting for the given resource '''
        return self.awaiting_for_creation == resource_class
