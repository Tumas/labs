#!/usr/bin/env python

from robot_exceptions import *
from point import Point

class Robot(object):
    ''' Generic Robot class '''
   
    def __init__(self, map):
        self.map = map
        self.moves = 0
        self.current = map.start_pos

    def __move_to(self, x, y, simulated = False):
        ''' Perform a simple move '''
        #print "*** moving to {0}***".format(Point(x, y)) 
        if self.map.wall_at(x, y):
            raise WallException("BUMP!", Point(x, y))

        if not simulated:
            self.current.x = x
            self.current.y = y
            self.moves += 1

        if self.map.finish_at(x, y):
            raise TargetReachedException("Arrived at target location")

        if self.map.start_at(x, y):
            raise StartReachedException("Walking in circles!")
        return self.current

    def move_left(self, simulated = False):
        self.__move_to(self.current.x - 1, self.current.y, simulated)

    def move_right(self, simulated = False):
        self.__move_to(self.current.x + 1, self.current.y, simulated)

    def move_up(self, simulated = False):
        self.__move_to(self.current.x, self.current.y - 1, simulated)

    def move_down(self, simulated = False):
        self.__move_to(self.current.x, self.current.y + 1, simulated)

    def move(self):
        ''' generic robot can't move '''
        pass

    def line_to_target(self, f, to):
        ''' Calulate points that belong to a line between starting point and finishing point.
                                  (Bresenham's line alogrithm) 
        '''
        x0, y0 = f.values()
        x1, y1 = to.values()
        dx, dy = abs(x1 - x0), abs(y1 - y0) 
        sx = x0 < x1 and 1 or -1
        sy = y0 < y1 and 1 or -1
        err = dx - dy

        line = []
        while True:
            line.append(Point(x0, y0))
            if x0 == x1 and y0 == y1:
              break
            e2 = 2 * err
            if e2 > -dy:
                err, x0 = err - dy, x0 + sx
            if e2 < dx:
              err, y0 = err + dx, y0 + sy
        return line

    def distance(self, p1, p2):
        ''' distance between two points in a map. (not moves) '''
        return len(self.__path_to_target(p1, p2)) 

    def __repr__(self):
        return " moves: {0}, current_pos: {1} ".format(self.moves, self.current)

if __name__ == '__main__':
    from map import Map
    r = Robot(Map('maps/map1'))
    r.move_down()
