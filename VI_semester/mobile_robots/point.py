#!/usr/bin/env python

class Point:
    ''' Class that represents a point in a map '''
    def __init__(self, x = 0, y = 0):
        self.x = x
        self.y = y

    def values(self):
        return [self.x, self.y]

    def __repr__(self):
        return "({0}, {1})".format(self.x, self.y)

    def __eq__(self, other):
        return self.x == other.x and self.y == other.y
