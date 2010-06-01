#!/usr/bin/env python
# -*- coding: utf-8 -*-

'''
  Autoriai:
    Tumas Bajoras PS3
'''

import gtk
import gtk.glade

from real import processor
from real import user_memory
from real import input_device
from real import output_device

from real.processor import to_hex
from real.processor import to_int
from configs import *

from kernel.processes.root import Root
from kernel.resources.terminate import Terminate
from kernel import kernel_data
from kernel.helpers import logger

class Main:
    def __init__(self): 
        self.xml = gtk.glade.XML('gui/os2.glade')

        # active process
        self.active_process = self.xml.get_widget('textview1')
        self.active_process_buffer = gtk.TextBuffer(None)
        self.active_process.set_buffer(self.active_process_buffer)

        # ready processes
        self.ready_processes = self.xml.get_widget('textview3')
        self.ready_processes_buffer = gtk.TextBuffer(None)
        self.ready_processes.set_buffer(self.ready_processes_buffer)

        # blocked processes
        self.blocked_processes = self.xml.get_widget('textview4')
        self.blocked_processes_buffer = gtk.TextBuffer(None)
        self.blocked_processes.set_buffer(self.blocked_processes_buffer)
  
        # resources
        self.resources = self.xml.get_widget('textview2')
        self.resources_buffer = gtk.TextBuffer(None)
        self.resources.set_buffer(self.resources_buffer)

        # log 
        self.log = self.xml.get_widget('textview5')
        self.log_buffer = gtk.TextBuffer(None)
        self.log.set_buffer(self.log_buffer)

    def update_processes(self):
        self.active_process_buffer.set_text(str(kernel_data.ACTIVE_PROCESS) + '\n')
        readys = ""
        for priority in kernel_data.READY_PROCESSES.dict:
            readys += str(priority) + "\n"
            for proc in kernel_data.READY_PROCESSES.dict[priority]:
                readys += '\t' + proc.name + '\n'

        self.ready_processes_buffer.set_text(readys)

        blockeds = ""
        for priority in kernel_data.BLOCKED_PROCESSES.dict:
            blockeds += str(priority) + "\n"
            for proc in kernel_data.BLOCKED_PROCESSES.dict[priority]:
                blockeds += '\t' + proc.name + '\n'

        self.blocked_processes_buffer.set_text(blockeds)

    def update_resources(self):
        data = ""
        for res in kernel_data.RESOURCES.resources():
            data += res.name + "\n"
        
        self.resources_buffer.set_text(data)

    def update_log(self):
        self.log_buffer.set_text('\n'.join(logger.LOG))
        logger.LOG = []

    def show_popup(self):
        popup = gtk.Window()
        popup.set_title("OS message")

        label = gtk.Label("<b>Root shuts down</b>")
        label.set_use_markup(True)
        popup.add(label)

        popup.set_position(gtk.WIN_POS_CENTER_ON_PARENT)
        popup.set_default_size(200, 200)
        popup.set_modal(True)
        popup.set_transient_for(self.window)
        popup.show_all()

    def execute(self, widget):
        if kernel_data.RESOURCES.is_instance_created(Terminate):
            self.update_processes()
            self.update_resources()
            self.update_log()
            kernel_data.ACTIVE_PROCESS.run()
        else:
            self.show_popup()
    # main
    def run(self):
        # main window
        self.window = self.xml.get_widget('Main')
        self.root = Root()

        # setting correct exiting 
        self.window.connect("destroy", gtk.main_quit)
        self.window.show_all()

        # buttons
        execute_button = self.xml.get_widget('button1')
        execute_button.connect('clicked', self.execute)

        gtk.main()

if __name__ == '__main__':
    Main().run()
