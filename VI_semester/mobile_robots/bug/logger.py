#!/usr/bin/env python

class Logger:
    ''' Simple logger class '''

    def __init__(self):
        self.log = []

    def clear(self):
        self.log = []

    def write(self, msg):
        self.log.append(str(msg))

    def show(self, num = 1):
        list = [ '\n\t' + s for s in self.log[-num:]]
        list.reverse()
        logs = "".join(list)
        return logs
