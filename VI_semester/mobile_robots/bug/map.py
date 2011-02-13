#!/usr/bin/env python

from warnings import warn
from point import Point

class Map:
    def __init__(self, filename, width = 10, height = 10, tokens = {}):
        ''' Initialize map :
              filename - path to a file with map 
              width    - width of a map
              height   - height of a map
              tokens   - dictionary of map characters
                wall  : denotes wall in a map
                empty : denotes empty space in map
                start : start token
                finish : finish token

            Default tokens:
                empty:  ' '
                wall:   '*'
                start:  'a'
                finish: 'b'
            '''
        self.map = []
        self.tokens = { 'wall': '*', 'empty': ' ', 'start': 'a', 'finish': 'b' }
        self.tokens.update(tokens)
        self.width = width
        self.height = height

        with open(filename) as f:
            for line in f:
                if len(self.map) == self.height:
                    break

                l = len(line)
                if l < self.width:
                    line = line + ((self.width - l) * self.tokens['empty'])
                self.map.append(line[:self.width]) 

        if len(self.map) < self.height:
            warn("Current map height is lower than specified. Updating to an actual height")
            self.height = len(self.map)

        if self.__char_count(self.tokens['start']) != 1:
            raise Exception("Map should contain only one start position")
        self.start_pos = self.__find_pos(self.tokens['start'])

        if self.__char_count(self.tokens['finish']) != 1:
            raise Exception("Map should contain only one finish position")
        self.finish_pos = self.__find_pos(self.tokens['finish'])

    def empty_at(self, x, y):
        ''' True if cell at given coordinates is empty '''
        return self.map[y][x] == self.tokens['empty']

    def start_at(self, x, y):
        ''' True if cell at given coordinates is starting point '''
        return self.map[y][x] == self.tokens['start']

    def finish_at(self, x, y):
        ''' True if cell at given coordinates is finishing point '''
        return self.map[y][x] == self.tokens['finish']

    def outside(self, x, y):
        ''' True if cell at given coordinates is outside of map '''
        if x < 0 or y < 0 or x >= self.width or y >= self.height:
            return True
        else:
            return False

    def wall_at(self, x, y):
        ''' True if cell at given coordinates is a wall or a part of it '''
        return self.outside(x, y) or self.map[y][x] == self.tokens['wall']

    def __char_count(self, char):
        ''' Count given characters in a map '''
        count = 0
        for line in self.map:
            count += line.count(char)
        return count

    def __find_pos(self, char):
        ''' Return coordinates of given char in a map '''
        for y in range(0, self.height):
            x = self.map[y].find(char)
            if x != -1:
                return Point(x, y)
        return None

    def __repr__(self):
        horizontal_bar = '|' + (("-") * (self.width)) + '|\n'
        return horizontal_bar + "".join(['|' + s + '|\n' for s in self.map]) + horizontal_bar

if __name__ == '__main__':
    Map('maps/map1')
