#!/usr/bin/env python
'''
  Autoriai:
    Tumas Bajoras PS3
'''

import sys
sys.path.append('.')
from configs import *

import processor
import input_device 
import output_device 
import data
import external_memory
import user_memory
from kernel.helpers import logger

"""
channel_device - module that represent channel device.
Channels operate work with data at higher level than IO devices, they transfer data in blocks or in variable length data arrays
"""

# channel 1
def read_from_in(reset = False):
    """ CHANNEL 1
      
      CHANNEL1 uses input device read() operation to fill the buffer and then transfers this buffer to supervisor memory
    """
    processor.chst[0] = 1
    
    # cleaning lists 
    data.SM = []
    data.buffer = [[]] * BLOCK_SIZE

    # read from buffer 
    count = 0
    while not data.buffer.__contains__('END '):
        count += 1
        input_device.read(reset)
        data.SM.extend(data.buffer)
        if count != 0:
            reset = False
   
    # remove last empty elements
    while len(data.SM) != 0 and data.SM[-1][:3] != 'END':
        data.SM.pop()

    processor.chst[0] = 0
    processor.ioi += 1

# implement channel 2 and 3
def sm_to_out():
    """ CHANNEL 2
      
      CHANNEL 2 fills buffer from supervisor memory and relies on output device to print buffer to STDOUT
    """

    processor.chst[1] = 1
    
    i = 0
    for x in data.SM:
        data.buffer[i % BUFFER_SIZE] = x
        i += 1

        if i == BUFFER_SIZE:
            for j in range(BUFFER_SIZE):
                output_device.write(data.buffer[j])

    # move last bytes from buffer
    for j in range(i % BUFFER_SIZE):
        output_device.write(data.buffer[j])

    processor.ioi += 2
    processor.chst[1] = 0

def sm_hd(to_hd = False, hd_read_info = []):
    ''' 
      Channel3 moves data
        from hd to mem - to_hd == False
        from sm to hd - to_hd == True
    '''
    write_info = []
    block_num = 0
    word_num = 0

    if to_hd:
        # move data from SM to hard disk
        while len(write_info) != len(data.SM):
            if block_num * BUFFER_SIZE + word_num not in data.HD_BLOCKS:
                write_info.append([block_num, word_num])
            if word_num >= BLOCK_SIZE - 1:
                block_num += 1
            word_num = (word_num + 1) % (BLOCK_SIZE) 

        for i in range(len(data.SM)):
            external_memory.write(data.SM[i], write_info[i][0], write_info[i][1]) 
        return write_info
    else:
        data.SM = []
        for i in hd_read_info:
            data.SM.append(external_memory.fetch(i[0], i[1]))

def hd_to_um(hd_read_data = []):
    '''
      move data from hd to user memory
        one-way communication

      Reads given data fields from HD and transfers them to User memory
    '''
    addresses = user_memory.occupy_empty_area(len(hd_read_data))
    if addresses is None:
        return None

    j = 0
    for i in hd_read_data:
        #logger.log("J: " + str(j))
        #logger.log("ADDresses len: " + str(len(addresses)))
        #logger.log("HD READ data: " + str(len(hd_read_data)))
        user_memory.write(external_memory.fetch(i[0], i[1]), addresses[j])
        j += 1

    return addresses

if __name__ == '__main__':
    # SM and HD communication
    data.SM = ["aaaa"] * 40
    for x in range(10):
        data.SM.append("bbbb")

    print data.SM
    print len(data.SM)
    read_in =  sm_hd(True)
    print read_in
    print len(read_in)
    for i in range(15):
        print external_memory.fetch(0, i)
    print data.HD_BLOCKS
    print(len(data.HD_BLOCKS))
    sm_hd(False, read_in)
    print data.SM
    
    # HD and UM communication
    a = hd_to_um(read_in)

    print "USER MEMORY"
    for addr in a:
        print user_memory.fetch(addr)
        
