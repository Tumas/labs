#!/usr/bin/env python

from robot_exceptions import *
from robot import Robot
from random import random, randint, choice
from sys import maxint
from copy import copy

import numpy as np
import pylab as p
import mpl_toolkits.mplot3d.axes3d as p3

class QSmarterRobot(Robot):
    '''
       Robot based on Q-learning algorithm.
         State  : position in a map
         Action : movement from one position to another

      This is different from simple qrobot. QSmarterRobot choses one from the valid moves while
        QRobot may choose an invalid move.

      QRobot due to its nature of movement does not learn well with reinforcment learning.
    '''

    def __init__(self, map, iterations, alpha, gama, rho, logger):
        super(QSmarterRobot, self).__init__(map)
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
        self.returned_to_start = 0
        self.empty_moves = 0

    def move_by_code(self, num, simulated = False):
        """ Perfrom encoded move [whether simulated or real] and get reward """
        try:
            self.__encoded_move(num, simulated)
            self.empty_moves += 1
            return 0
        except WallException as e:
            raise Exception("it should not happen")
        except TargetReachedException:
            self.target_reached += 1
            return 100
        except StartReachedException:
            self.returned_to_start += 1
            return 0

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
            print "IT SHOULD NOT HAPPEN"
            #raise Exception("It should not happen")
            #self.logger.write("Bumped a wall.. {0} at {1}".format(e.msg, e.point))
        except TargetReachedException:
            self.logger.write("Destination reached in {0} moves".format(self.moves))
            return True
        except StartReachedException:
            self.logger.write("Am I lost?")
            pass
        return False


    def best_move(self):
        """ Get code of the best move from current position """
        reward, move = -1, -1
        for m in range(0, 4):
            x, y = self.__decode_new_coordinates(m)
            if not self.map.wall_at(x, y) and self.move_by_code(m, True) > reward:
                move = m
        return move

    def train(self):
        """ Train the robot """

        """ Q:
              q[x][y][0..3]
        """
        
        move = None
        for i in range(self.iterations):
            # get random position in a map
            self.current = self.map.random_pos(False)

            for j in range(100):
                # random action or best action?
                if random() < self.rho:
                    # collect valid moves and choose one randomly
                    test = []
                    for m in range(4):
                        x, y = self.__decode_new_coordinates(m)
                        if not self.map.wall_at(x, y):
                            test.append(m) 
                    move = choice(test) 
                else:
                    # choose best move known so far
                    move = self.best_move()

                previous = copy(self.current)
                reward = self.move_by_code(move)
                self.learn(reward, previous, move)

        # get back to start position
        self.current = self.map.start_pos
        self.moves = 0

        print "Q TABLE: ********************************"
        print self.q
        print " ********************************"

    def max_values(self):
        val = []
        for x in range(self.map.width):
            for y in range(self.map.height):
                val.append(max(self.q[x][y]))
        return val

    def learn(self, reward, previous, action):
        """ 
          reward - number of how good move was
          previous - previous move code
          move     - move code of current move

          Q(s, a) = Q(s, a) + alpha * (reward + gama * Qmax)) 
        
        """
        q    = self.q[previous.x][previous.y][action]
        qmax = max(self.q[self.current.x][self.current.y])
        self.q[previous.x][previous.y][action] = (1 - self.alpha) * q + self.alpha * (reward + self.gama * qmax)

    def print_training_stats(self):
        """ """
        print "Target reached: {0} times\n".format(self.target_reached)
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

    def __decode_new_coordinates(self, num):
        """ Get coordinates of a new move given its encoded representation """
        if num == 0:
            return self.current.x, self.current.y - 1 
        elif num == 1:
            return self.current.x + 1, self.current.y
        elif num == 2:
            return self.current.x, self.current.y + 1 
        elif num == 3:
            return self.current.x - 1, self.current.y

    def __test_parameter(self, param):
        """ test if value is between 0 and 1 """
        if param < 0 or param > 1: 
            raise Exception("Parameter {0} is not in valid range [0..1]".format(param))
        return param

    def plot3(self):
        X = np.arange(0, self.map.width, 1)
        Y = np.arange(0, self.map.height, 1)
        X, Y = np.meshgrid(X, Y)
        Z = np.array([max(self.q[x][y]) for x in X[0] for y in X[0]]).reshape(self.map.width, self.map.height)
        
        print Z
        # print [max(el) for el in Z]

        fig=p.figure()
        ax = p3.Axes3D(fig)
        ax.plot_wireframe(X,Y,Z)
        ax.set_xlabel('X')
        ax.set_ylabel('Y')
        ax.set_zlabel('Z')
        p.show()

if __name__ == '__main__':
    import sys

    from map import Map
    from logger import Logger
    from map_curses import MapCurses

    if len(sys.argv) == 1:
        print "No map given"
        exit()

    l = Logger()
    alpha = 0.7 # learning rate
    gamma = 0.7 # discount rate -> how much current state depends on the state it leads to ~ [0,1].
    rho = 0.5   # randomness of exploration -> choose random action or best action (on increase, random actions are taken)

    r = QSmarterRobot(Map(sys.argv[1]), 1000, alpha, gamma, rho, l)
    r.train()

    r.print_training_stats()
    r.plot3()

    cm = MapCurses(r, l)
    cm.animate()
