#!/usr/bin/env python

import sys
sys.path.append('.')

import configs
from kernel.process import Process
from kernel import kernel_data
from kernel import kernel
from real import user_memory
from real import processor
from real.processor import to_int
from real.processor import to_hex

from kernel.resources.interrupt_event import InterruptEvent
from kernel.resources.task_completed import TaskCompleted
from kernel.helpers import logger

class VirtualMachine(Process):
    def __init__(self, opts = {}):
        opts['name'] = 'vm_' + str(kernel_data.PID)
        opts['priority'] = configs.LOADER_PRIORITY
        Process.__init__(self, opts)
        self.cs_length = opts['cs_length']
        self.ds_length = opts['ds_length']
        self.addresses = opts['addresses']
        
        # state 
        self.ic = None
        self.num = self.ds_length + 1 # skip DATA SEGMENT

        # prepare DS 
        for i in range(1, self.ds_length):
            data = user_memory.fetch(self.addresses[i])
            if data[0:2] != "DW":
                processor.pi = 2
                kernel.create_resource(InterruptEvent, self, {'process' : self })
            else:
                user_memory.write(to_hex(to_int(data[2:4])), self.addresses[i])


    def update(self):
        ''' Update state of the vm to support vm chaning '''
        if self.num < len(self.addresses):
            self.ic = self.addresses[self.num]
            processor.ic = self.ic
            self.num += 1

    def run(self):
        ''' execute user task until interrupt is encountered '''

        self.update()
        command = user_memory.fetch(self.ic)
        if command[0:2] == 'LA':
            self.LA(command[2:4])
        elif command[0:2] == 'LB':
            self.LB(command[2:4])
        elif command[0:2] == 'MA':
            self.MA(command[2:4])
        elif command[0:2] == 'MB':
            self.MB(command[2:4])
        elif command[0:2] == 'WS':
            self.WS(command[2:4])
        elif command[0:2] == 'LS':
            self.LS(command[2:4])
        elif command[0:2] == 'JM':
            self.JM(command[2:4])
        elif command[0:2] == 'JL':
            self.JL(command[2:4])
        elif command[0:2] == 'JE':
            self.JE(command[2:4])
        elif command[0:2] == 'PD':
            pass
        elif command[0:3] == 'ADD':
            self.ADD()
        elif command[0:3] == 'SUB':
            self.SUB()
        elif command[0:3] == 'CMP':
            self.CMP()
        elif command[0:3] == 'END':
            self.END()
            return
        elif command == 'SWAP':
            self.SWAP()
        elif command == 'AXOR':
            self.AXOR()
        elif command == 'BXOR':
            self.BXOR()
        else: 
            processor.pi = 2
            kernel.create_resource(InterruptEvent, self, {'process' : self, 'message' : str(command) })
            self.state = configs.BLOCKED_STATE
            kernel.schedule()
            return

        logger.log("COMMAND EVALUATED: " + command)
        logger.log("TIME IS : " + str(processor.time))
        self.update_time(2)
        if self.time_is_over():
            processor.ti = 1
            kernel.create_resource(InterruptEvent, self, {'process' : self, 'message' : None })
            self.state = configs.BLOCKED_STATE
            kernel.schedule()

    # -- private commands 
    def get_real_address_from_cs(self, XY):
        ''' return real address of offset from code segment'''
        XY = to_int(XY)
        if XY > self.cs_length:
            processor.pi = 1
            kernel.create_resource(InterruptEvent, self, {'process' : self, 'message' : 'jumping out of program' })
            self.state = configs.BLOCKED_STATE
            kernel.schedule()
        else:
            return self.addresses[self.ds_length + 1 + XY]

    def get_real_address_of_ds(self, XY):
        ''' return real address of offset from data segment '''
        XY = to_int(XY)
        if XY > self.ds_length:
            processor.pi = 1
            kernel.create_resource(InterruptEvent, self, {'process' : self, 'message' : 'invalid data pointer' })
            self.state = configs.BLOCKED_STATE
            kernel.schedule()
        else:
            return self.addresses[XY + 1]

    def write_to_ds(self, word, XY):
        ''' Write given word to address ''' 
        user_memory.write(word, self.get_real_address_of_ds(XY))

    def read_from_ds(self, XY):
        ''' read word from given virtual address '''
        return user_memory.fetch(self.get_real_address_of_ds(XY))

    # ----------- VM ----------- COMMANDS
    # Processor     
    # user accessible registers
    def get_ax(self):
        return processor.r[0]

    def get_bx(self):
        return processor.r[1]

    def get_c(self):
        return processor.c

    def set_ax(self, value):
        processor.r[0] = value
    
    def set_bx(self, value):
        processor.r[1] = value

    def set_c(self, value):
        processor.c = value

    # * ********** COMMAND SYSTEM
    def LA(self, XY):
        '''Value at XY is sent to ax'''
        self.set_ax(self.read_from_ds(XY))

    def LB(self, XY):
        '''Value at XY is sent to bx'''
        self.set_bx(self.read_from_ds(XY))

    def MA(self, XY):
        '''Value of ax is sent to XY'''
        self.write_to_ds(self.get_ax(), XY)

    def MB(self, XY):
        '''Value of bx is sent to XY'''
        self.write_to_ds(self.get_bx(), XY)

    def WS(self, XY):
        ''' Value of XY in shared memory is sent to ax '''
        user_memory.write(self.get_ax(), to_hex(configs.SH_MEMEORY_STARTING_BLOCK * configs.BLOCK_SIZE + to_int(str(XY)[0]) * configs.BLOCK_SIZE + to_int(str(XY)[1]))) 

    def LS(self, XY):
        ''' Value of ax is sent to shared memory '''                                                                          
        value = user_memory.fetch(to_hex(configs.SH_MEMEORY_STARTING_BLOCK * configs.BLOCK_SIZE + to_int(XY[0]) * configs.BLOCK_SIZE + to_int(XY[1])))
        self.set_ax(value)

    # Arithmetic
    def ADD(self):
        '''ax = ax + bx'''
        self.set_ax( to_hex( to_int(self.get_ax()) + to_int(self.get_bx())))

    def SUB(self):
        '''ax = ax - bx'''
        answer = to_int(self.get_ax()) - to_int(self.get_bx())
        if (answer < 0):
          self.set_c(2)
        self.set_ax(to_hex(answer))

    def CMP(self):
        '''
        c = 0  if ax == bx
        c = 1  if ax > bx
        c = 2  if ax < bx 
        '''
        if self.get_ax() == self.get_bx():
            self.set_c(0)
        elif (to_int(self.get_ax()) - to_int(self.get_bx())) > 0:
            self.set_c(1)
        else:
            self.set_c(2)

    def SWAP(self):
        ''' swap ax and bx'''
        self.AXOR()
        self.BXOR()
        self.AXOR()

    def AXOR(self):
        '''ax = ax ^ bx '''
        self.set_ax( to_hex ( to_int(self.get_ax()) ^ to_int(self.get_bx()) ))

    def BXOR(self):
        '''bx = ax ^ bx '''
        self.set_bx( to_hex ( to_int(self.get_ax()) ^ to_int(self.get_bx()) ))

   # Control
    def END(self):
        ''' End task '''
        processor.si = 3
        kernel.create_resource(InterruptEvent, self, {'process' : self, 'message' : 'END event' })
        self.state = configs.BLOCKED_STATE
        kernel.schedule()

    def JL(self, XY):
        ''' jump to XY if c = 2'''
        # no to_int required because c is 1B register, that holds decimal values
        if self.get_c() == 2:
            self.JM(XY)

    def JE(self, XY):
        ''' jump to XY if c = 0'''
        if self.get_c() == 0:
            self.JM(XY)

    def JM(self, XY):
        ''' jump to XY '''
        #processor.ic =  to_hex(to_int(processor.get_real_address(XY)) + BLOCK_SIZE * DS_LENGTH - 1) 
        #processor.ic = get_real_address_from_cs(XY) 
        if to_int(XY) > self.cs_length:
            processor.pi = 3
            kernel.create_resource(InterruptEvent, self, {'process' : self, 'message' : None })
            self.state = configs.BLOCKED_STATE
            kernel.schedule()
        else:
            self.num = to_int(XY) + self.ds_length + 1
            #self.get_real_address_from_cs(XY)

    # Timer methods
    def update_time(self, time_unit):
        processor.time += time_unit

    def time_is_over(self):
        return processor.time > 10
