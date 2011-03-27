#!/usr/bin/env python

class WallException(Exception):
    def __init__(self, msg, point):
        self.msg = msg
        self.point = point

class TargetReachedException(Exception):
    pass

class StartReachedException(Exception):
    pass
