#!/usr/bin/env python

from logger import Logger
import curses

class MapCurses:
    def __init__(self, robot, logger = Logger()):
        self.robot = robot
        self.logger = logger

        # formatting
        self.tab_token = "\n\t\t\t\t"

    def __repr__(self):
        "Print map to curses screen" 
        string = self.tab_token
        string += '   |' + ('-' * self.robot.map.width) + "|"  + self.tab_token
        for y in range(0, self.robot.map.height):
            temp = self.robot.map.map[y]
            if self.robot.current.y == y:
                temp = temp[:self.robot.current.x] + '@' + temp[self.robot.current.x+1:]
            string += ("{0:2}".format(repr(y))) + ":|" + temp + "|" + self.tab_token
        string += '   |' + ('-' * self.robot.map.width) + "|" 
        return string

    def animate(self):
        """ Movement animation """
        screen = curses.initscr()
        curses.start_color()
        curses.init_pair(1, curses.COLOR_RED, curses.COLOR_WHITE)

        found, x = None, False
        while x != ord('4'):
            screen.clear()
            screen.addstr(7, 20, "Line:  {0}".format(self.robot.goal_line))
            screen.addstr(8, 20, "Moves: {0}".format(self.robot.moves))
            screen.addstr(10, 20, self.__repr__())
            screen.addstr(0, 0, self.logger.show(4), curses.color_pair(1))

            if found:
                screen.addstr(4, 50, "Item found!", curses.color_pair(1))
            else:
                found = self.robot.move()

            screen.refresh()
            x = screen.getch()
        curses.endwin()

if __name__ == '__main__':
    from bug2 import Bug2
    from map import Map

    l = Logger()
    b = Bug2(Map('maps/map3'), l)

    cm = MapCurses(b, l)
    cm.animate()
