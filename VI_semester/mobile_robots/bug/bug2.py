#!/usr/bin/env python

# todo: consider refactoring to packages
from robot_exceptions import *
from robot import Robot

class Bug2(Robot):
    ''' Bug2 algorithm '''

    def __init__(self, map, logger):
        super(Bug2, self).__init__(map)
        self.goal_line = self.line_to_target(self.map.start_pos, self.map.finish_pos)
        self.min_distance = len(self.goal_line) 
        self.pos = 0
        self.mode = 'LINE'
        self.wall_x = 0 
        self.wall_y = 0
        self.logger = logger

    def move(self):
        '''
            1. head toward goal on the m-line
            2. if an obstacle is in the way, follow it until you encounter the m-line again
            3. leave the obstacle and continue toward the goal
        '''
        try:
            if self.mode == 'LINE':
                if self.goal_line[self.pos].x == self.current.x and self.current.y == self.goal_line[self.pos].y:
                    self.pos += 1       # advance for another goal

                diff_x = self.current.x - self.goal_line[self.pos].x 
                diff_y = self.current.y - self.goal_line[self.pos].y

                print self.goal_line[self.pos] 
                print diff_x
                print diff_y

                if diff_x > 0:
                    self.move_left()
                elif diff_x < 0:
                    self.move_right()
                elif diff_y > 0:
                    self.move_up()
                elif diff_y < 0:
                    self.move_down()
            else:
                if self.mode == 'OBSTACLE':
                    if self.wall_y > 0:
                        self.move_left()
                    elif self.wall_y < 0:
                        self.move_right()
                    elif self.wall_x > 0:
                        self.move_down()
                    elif self.wall_x < 0:
                        self.move_up()

                    self.mode = 'TEST'        # mode is entered when corner of an obstacle is encountered
                elif self.mode == 'TEST': 
                    if self.wall_y > 0:
                        self.move_up()
                        self.wall_y, self.wall_x = 0, -1
                    elif self.wall_y < 0:
                        self.move_down()
                        self.wall_y, self.wall_x = 0, 1
                    elif self.wall_x > 0:
                        self.move_left()
                        self.wall_y, self.wall_x = 1, 0
                    elif self.wall_x < 0:
                        self.move_right()
                        self.wall_y, self.wall_x = -1, 0
                    self.mode = 'OBSTACLE'

                if self.current in self.goal_line and self.goal_line.index(self.current) > self.pos:
                    self.mode = 'LINE'
                    self.pos = self.goal_line.index(self.current)
                    self.logger.write("Found m-line, following it.")

        except WallException as e:
            self.logger.write("Bumped a wall.. {0} at {1}".format(e.msg, e.point))
            print self.current.x
            print self.current.y

            self.mode = 'OBSTACLE' 
            self.wall_x = self.current.x - e.point.x
            self.wall_y = self.current.y - e.point.y

            print self.wall_y
            print self.wall_x
        except TargetReachedException:
            self.logger.write("Destination reached in {0} moves".format(self.moves))
            return True
        except StartReachedException:
            self.logger.write("Am I lost?")
            pass
        return False

if __name__ == '__main__':
    from map import Map
    from logger import Logger

    #b = Bug2(Map('maps/map2'), Logger())
    b = Bug2(Map('maps/map3', 20, 15), Logger())

    print b.map
    while True:
        if b.move():
            break
        x = raw_input()
