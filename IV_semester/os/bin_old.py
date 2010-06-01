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

import manager

class Main:
    def __init__(self): 
        self.xml = gtk.glade.XML('gui/pos.glade')
        self.vm = manager.create_vm()
        
        # cache variable for storing executed VM state 
        # should be reloaded with every new loaded vm
        self.cache = [] 

        # real memory management
  
        # fields
        self.ax_info = self.xml.get_widget('entry2')
        self.bx_info = self.xml.get_widget('entry1')
        self.cs_info = self.xml.get_widget('entry3')
        self.ds_info = self.xml.get_widget('entry4')
        self.c_info = self.xml.get_widget('entry5')
        self.ic_info = self.xml.get_widget('entry6')
        self.chst0_info = self.xml.get_widget('entry7')
        self.chst1_info = self.xml.get_widget('entry8')
        self.chst2_info = self.xml.get_widget('entry9')

        self.pi_info = self.xml.get_widget('entry10')
        self.si_info = self.xml.get_widget('entry11')
        self.ti_info = self.xml.get_widget('entry12')
        self.ioi_info = self.xml.get_widget('entry13')
        self.time_info = self.xml.get_widget('entry14')
        self.mode_info = self.xml.get_widget('entry15')
        self.ptr_info = self.xml.get_widget('entry16')

        # wm info fields
        self.wm0 = self.xml.get_widget('entry18')
        self.wm1 = self.xml.get_widget('entry19')
        self.wm2 = self.xml.get_widget('entry20')
        self.wm3 = self.xml.get_widget('entry21')
        self.wm4 = self.xml.get_widget('entry22')
        self.wm5 = self.xml.get_widget('entry23')
        self.wm6 = self.xml.get_widget('entry24')
        self.wm7 = self.xml.get_widget('entry25')
        self.wm8 = self.xml.get_widget('entry26')
        self.wm9 = self.xml.get_widget('entry27')
        self.wma = self.xml.get_widget('entry28')
        self.wmb = self.xml.get_widget('entry29')
        self.wmc = self.xml.get_widget('entry30')
        self.wmd = self.xml.get_widget('entry31')
        self.wme = self.xml.get_widget('entry32')
        self.wmf = self.xml.get_widget('entry33')

        # page table 
        self.pgtable = [
            self.xml.get_widget('label50'), 
            self.xml.get_widget('label51'),
            self.xml.get_widget('label52'),
            self.xml.get_widget('label53'),
            self.xml.get_widget('label54'),
            self.xml.get_widget('label55'),
            self.xml.get_widget('label56'),
            self.xml.get_widget('label57'),
            self.xml.get_widget('label58'),
            self.xml.get_widget('label59'),
            self.xml.get_widget('label60'),
            self.xml.get_widget('label61'),
            self.xml.get_widget('label62'),
            self.xml.get_widget('label63'), 
            self.xml.get_widget('label64'),
            self.xml.get_widget('label65'),
          ]

        self.real_address = self.xml.get_widget('entry17')
        self.realtable = [
            self.xml.get_widget('label36'),
            self.xml.get_widget('label37'),
            self.xml.get_widget('label38'),
            self.xml.get_widget('label39'),
            self.xml.get_widget('label40'),
            self.xml.get_widget('label41'),
            self.xml.get_widget('label42'),
            self.xml.get_widget('label43'),
            self.xml.get_widget('label44'),
            self.xml.get_widget('label45'),
            self.xml.get_widget('label46'),
            self.xml.get_widget('label47'),
            self.xml.get_widget('label48'),
            self.xml.get_widget('label49'),
            self.xml.get_widget('label66'),
            self.xml.get_widget('label67'),
          ]

        # latest adjustments: output_device + current command
        self.commands = self.xml.get_widget('textview1')
        self.commands_buffer = gtk.TextBuffer(None)
        self.commands.set_buffer(self.commands_buffer)

        self.output = self.xml.get_widget('textview2')
        self.output_buffer = gtk.TextBuffer(None)
        self.output.set_buffer(self.output_buffer)

      
    def save_state(self):
        self.cache.append({
          "ax" : str( processor.r[0] ),
          "bx" : str( processor.r[1] ),
          "cs" : str( processor.cs ),
          "ds" : str( processor.ds ),
          "c" : str( processor.c ),
          "ic" : str( processor.ic ),
          "chst0" : str( processor.chst[0] ),
          "chst1" : str( processor.chst[1] ),
          "chst2" : str( processor.chst[2] ),
          "pi" : str( processor.pi ),
          "si" : str( processor.si ),
          "ti" : str( processor.ti ),
          "ioi" : str( processor.ioi ),
          "time" : str( processor.time ),
          "mode" : str( processor.mode ),
          "ptr" : str( processor.ptr ),
          })

    def restore_state(self):
        if len(self.cache) == 0:
            print "Cache is empty"
            return 

        state = self.cache.pop() 

        processor.r[0] = state["ax"]
        processor.r[1] = state["bx"]
        processor.cs = state["cs"]
        processor.ds = state["ds"]
        processor.c = state["c"]
        processor.ic = state["ic"]
        processor.chst[0] = state["chst0"]
        processor.chst[1] = state["chst1"]
        processor.chst[2] = state["chst2"]
        processor.pi = state["pi"]
        processor.si = state["si"]
        processor.ti = state["ti"]
        processor.ioi = state["ioi"]
        processor.mode = state["mode"]
        processor.time = state["time"]
        processor.ptr = state["ptr"]

    def update_page_table(self):
        i = 0
        for widget in self.pgtable:
            widget.set_text(str( user_memory.fetch(to_hex(to_int(processor.ptr) + i)) ))
            i += 1

    def update_info(self):
        ''' update values on screen '''
        self.ax_info.set_text(str(processor.r[0]))
        self.bx_info.set_text(str(processor.r[1]))
        self.cs_info.set_text(str(processor.cs))
        self.ds_info.set_text(str(processor.ds))
        self.c_info.set_text(str(processor.c))
        self.ic_info.set_text(str(processor.ic))
        self.c_info.set_text(str(processor.c))
        self.chst0_info.set_text(str(processor.chst[0]))
        self.chst1_info.set_text(str(processor.chst[1]))
        self.chst2_info.set_text(str(processor.chst[2]))
        self.pi_info.set_text(str(processor.pi))
        self.si_info.set_text(str(processor.si))
        self.ti_info.set_text(str(processor.ti))
        self.ioi_info.set_text(str(processor.ioi))
        self.time_info.set_text(str(processor.time))
        self.mode_info.set_text(str(processor.mode))
        self.ptr_info.set_text(str(processor.ptr))

        # updating virtual memory 
        self.update_virtual_memory()
        # updating output
        self.output_buffer.set_text(output_device.output)

        # updating commands
        string = ""
        for i in range(5):
            string += str( user_memory.fetch(to_hex(to_int(processor.ic) + i))) + '\n'
        self.commands_buffer.set_text(string)

    def update_virtual_memory(self):
        wg_num = 0
        for widget in [self.wm0, self.wm1, self.wm2, self.wm3, self.wm4, self.wm5, self.wm6, self.wm7, 
            self.wm8, self.wm9, self.wma, self.wmb, self.wmc, self.wmd, self.wme, self.wmf]:
            strin = ""
            for i in range(BLOCK_SIZE):
                strin += str( self.vm.read(to_hex(wg_num * BLOCK_SIZE + i)[2:4]) ) + " "
            widget.set_text(strin)
            wg_num += 1
        
    # callbacks
    def next(self, widget):
        self.save_state()
        self.vm.iterate()
        processor.ic = to_hex(to_int(processor.ic)  + 1)
        self.update_info()

        if processor.si == 3:
            self.show_popup()

    def previous(self, widget):
        self.restore_state()
        self.update_info()

    def execute(self, widget):
        while True:
            self.vm.iterate()
            processor.ic = to_hex(to_int(processor.ic)  + 1)
            self.update_info()
              
            if processor.si == 3:
                self.show_popup()
                break

    def load_selected_task(self, widget):
        # create new vm and load new task
        input_device.input_file = self.chooser.get_filename()
        self.vm = manager.create_vm()

        processor.si = 0
        output_device.clear()

        self.update_info()
        self.update_page_table()
        self.cache = []

    def update_real(self, widget):
        addr = self.real_address.get_text()
        # some regexp validation should be here 
        
        i = 0
        for widget in self.realtable:
            widget.set_text(str( user_memory.fetch( to_hex(to_int(addr) + i) )))
            i += 1
  
    # pop up window
    def show_popup(self):
        popup = gtk.Window()
        popup.set_title("Interrupt found!")

        label = gtk.Label("<b>Task is finished!</b>")
        label.set_use_markup(True)
        popup.add(label)

        popup.set_position(gtk.WIN_POS_CENTER_ON_PARENT)
        popup.set_default_size(200, 200)
        popup.set_modal(True)
        popup.set_transient_for(self.window)
        popup.show_all()
        
    # main
    def run(self):
        # main window
        self.window = self.xml.get_widget('Main')

        # setting correct exiting 
        self.window.connect("destroy", gtk.main_quit)
        self.xml.get_widget('imagemenuitem5').connect('activate', gtk.main_quit)

        # buttons 
        next_button = self.xml.get_widget('button2')
        prev_button = self.xml.get_widget('button1')
        exec_button = self.xml.get_widget('button3')
        update_button = self.xml.get_widget('button4')
        
        next_button.connect('clicked', self.next)
        prev_button.connect('clicked', self.previous)
        exec_button.connect('clicked', self.execute)
        update_button.connect('clicked', self.update_real)

        # file chooser
        self.chooser = self.xml.get_widget('filechooserbutton1')
        self.chooser.connect('file-set', self.load_selected_task)

        self.window.show_all()
        self.update_info()
        self.update_page_table()

        gtk.main()

if __name__ == '__main__':
    Main().run()
