#!/usr/bin/env python

from robot_exceptions import *
from robot import Robot
from random import random, randint
from sys import maxint

class QRobot(Robot):
    '''
       Robot based on Q-learning algorithm.
         State  : position in a map
         Action : movement from one position to another
    '''

    def __init__(self, map, iterations, alpha, gama, rho, logger):
        super(QRobot, self).__init__(map)
        self.logger = logger
        
        # THIS IS evil:
        #self.q = [[[0] * 4] * self.map.height] * self.map.width  # robot knows nothing in the beginning 

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

    def move_by_code(self, num, simulated = False):
        """ Perfrom encoded move [whether simulated or real] and get reward """
        reward = 1
        try:
            self.__encoded_move(num, simulated)
            return reward
        except WallException as e:
            return -100
        except TargetReachedException:
            return 100
        except StartReachedException:
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
                # random action or best action?
                if random() < self.rho:
                    move = randint(0, 3)
                else:
                    move = self.best_move()

                reward = self.move_by_code(move)
                self.learn(reward, move)

        # get back to start position
        self.current = self.map.start_pos
        self.moves = 0

    def learn(self, reward, action):
        """  Q(s, a) = Q(s, a) + alpha * (reward + gama * Qmax)) """
        current = self.q[self.current.x][self.current.y][action]
        qmax = max(self.q[self.current.x][self.current.y])

        new_val = current + self.alpha * (reward + self.gama * qmax)
        self.q[self.current.x][self.current.y][action] = new_val

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

if __name__ == '__main__':
    import sys

    from map import Map
    from logger import Logger
    from map_curses import MapCurses

    l = Logger()
    r = QRobot(Map(sys.argv[1]), 1000, 0.3, 0.3, 0.3, l)
    r.train()

    for x in range(r.map.width):
        #for i in range(r.map.height):
        print r.q[x]

    cm = MapCurses(r, l)
    cm.animate()
