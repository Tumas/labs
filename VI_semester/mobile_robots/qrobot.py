#!/usr/bin/env python

from robot_exceptions import *
from robot import Robot
from random import random, randint
from sys import maxint
from copy import copy

import numpy as np
import pylab as p
import mpl_toolkits.mplot3d.axes3d as p3

class QRobot(Robot):
    '''
       Robot based on Q-learning algorithm.
         State  : position in a map
         Action : movement from one position to another
    '''

    def __init__(self, map, iterations, alpha, gama, rho, logger):
        super(QRobot, self).__init__(map)
        self.logger = logger
        
        self.q = []
        for i in range(self.map.width):
            x = []
            for j in range(self.map.height):
                x.append([0, 0, 0, 0])
            self.q.append(x)

        self.rho = self.__test_parameter(rho)     # randomnes of exploration
        self.alpha = self.__test_parameter(alpha) # learning rate (0 - no influence, the algorithmm doesn't learn)

        # discount rate -> how much current state depends on the state it leads to ~ [0,1]. Overgrowing problem
        self.gama = self.__test_parameter(gama)
        self.iterations = iterations

        # training stats
        self.target_reached = 0
        self.wall_bumped = 0
        self.returned_to_start = 0
        self.empty_moves = 0

    def move_by_code(self, num, simulated = False):
        """ Perfrom encoded move [whether simulated or real] and get reward """
        reward = 1
        try:
            self.__encoded_move(num, simulated)
            self.empty_moves += 1
            return reward
        except WallException as e:
            self.wall_bumped += 1
            return -100
        except TargetReachedException:
            self.target_reached += 1
            return 100
        except StartReachedException:
            self.returned_to_start += 1
            return -1

    def move(self):
        """ Move according to learned Q values """ 
        num = 0
        try:
            val = -sys.maxint
            for i in range(4):
                if self.q[self.current.x][self.current.y][i] > val:
                    val, num = self.q[self.current.x][self.current.y][i], i

            self.logger.write("Q: {0}".format(self.q[self.current.x][self.current.y]))
            self.logger.write("Chosen: {0}".format(num))
            self.__encoded_move(num)

        except WallException as e:
            self.logger.write("Bumped a wall.. {0} at {1}".format(e.msg, e.point))
        except TargetReachedException:
            self.logger.write("Destination reached in {0} moves".format(self.moves))
            return True
        except StartReachedException:
            self.logger.write("Am I lost?")
            pass
        return False

    def best_move(self):
        """ Get code of the best move from current position """
        reward, move = self.move_by_code(0, True), 0
        for m in range(1, 4):
            if self.move_by_code(m, True) > reward: move = m
        return move

    def train(self):
        """ Train the robot """
        move = None
        for i in range(self.iterations):
            self.current = self.map.random_pos(False)

            for j in range(self.map.width * self.map.height):
            #for j in range(3):
                # random action or best action?
                if random() < self.rho:
                    move = randint(0, 3)
                else:
                    move = self.best_move()

                previous = copy(self.current)
                #print "Iteration: {0}, Current position: {1}".format(i, self.current)
                reward = self.move_by_code(move)
                #print "Moved: {0}, Got reward: {1}".format(move, reward)
                #print "Current position: {0}, was: {1}".format(self.current, previous)
                self.learn(reward, previous, move)

        # get back to start position
        self.current = self.map.start_pos
        self.moves = 0

    def learn(self, reward, previous, action):
        """  Q(s, a) = Q(s, a) + alpha * (reward + gama * Qmax)) """
        q    = self.q[previous.x][previous.y][action]
        qmax = max(self.q[self.current.x][self.current.y])

        new_val = (1 - self.alpha) * q + self.alpha * (reward + self.gama * qmax)
        #print "q: {0}, New_val: {1}".format(q, new_val)
        self.q[previous.x][previous.y][action] = new_val

        #for x in range(self.map.width):  print self.q[x]

    def print_training_stats(self):
        """ """
        print "Target reached: {0} times\nWall bumped: {1} times".format(self.target_reached, self.wall_bumped)
        print "Returned to start position: {0} times\nEmpty moves: {1} times".format(self.returned_to_start, self.empty_moves)

    def __encoded_move(self, num, simulated = False):
        """ Perform encoded move """
        if num == 0:
            self.move_up(simulated)
        elif num == 1:
            self.move_right(simulated)
        elif num == 2:
            self.move_down(simulated)
        elif num == 3:
            self.move_left(simulated)

    def __test_parameter(self, param):
        """ test if value is between 0 and 1 """
        if param < 0 or param > 1: 
            raise Exception("Parameter {0} is not in valid range [0..1]".format(param))
        return param

        """ *** ENABLING 3D PLOTING *** """

    def get_z(self, state, action):
        """ Helper transformation function to enable 3D plotting.
        
            State (from)
            Action (to). Both expressed as numbers
        """
        sx, sy = self.__get_x_y(state)
        ax, ay = self.__get_x_y(action)

        if abs(sx - ax) + abs(sy - ay) > 1:
            return 0

        if sx - ax == 0:
            if sy - ay > 0:
                return self.q[sx][sy][0]
            else:
                return self.q[sx][sy][2]
        else:
            if sy - ax > 0:
                return self.q[sx][sy][3]
            else:
                return self.q[sx][sy][1]

    def plot(self):

        X = np.arange(0, self.map.width * self.map.height, 1)
        Y = np.arange(0, self.map.width * self.map.height, 1)
        X, Y = np.meshgrid(X, Y)
        size = self.map.width * self.map.height
        Z = np.array([self.get_z(x, y) for x in X[0] for y in X[0]]).reshape(size, size)

        fig=p.figure()
        ax = p3.Axes3D(fig)
        ax.plot_wireframe(X,Y,Z)
        ax.set_xlabel('X')
        ax.set_ylabel('Y')
        ax.set_zlabel('Z')
        p.show()

    def __get_number(self, x, y):
        """ x,y -> number transformation """
        return y * self.map.width + y

    def __get_x_y(self, number):
        """ 
        number -> x,y transformation 
          4 5 6 7
          0 1 2 3 
          h = 2, w = 4
        """
        return number % self.map.width, number / self.map.width

if __name__ == '__main__':
    import sys

    from map import Map
    from logger import Logger
    from map_curses import MapCurses

    if len(sys.argv) == 1:
        print "No map given"
        exit()

    l = Logger()
    r = QRobot(Map(sys.argv[1]), 1000, 0.7, 0.3, 0.7, l)
    r.train()

    #for x in range(r.map.width): print r.q[x]
    r.print_training_stats()
    r.plot()

    cm = MapCurses(r, l)
    cm.animate()

