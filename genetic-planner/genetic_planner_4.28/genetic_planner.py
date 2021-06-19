from map import Map
import copy
import numpy as np
import cv2
import random
import util
import math

# STEP_SIZE = 10
POP_SIZE = 200  # population size
CROSS_RATE = 0.4  # mating probability (DNA crossover)
MUTATION_RATE = 0.01  # mutation probability
N_GENERATIONS = 1000
COUNT = 0


class GeneticPlanner:
    def __init__(self, _map: Map):
        self.map = _map
        self.paths = []
        self.step_len = util.hcf(abs(self.map.start[0]-self.map.end[0]), abs(self.map.start[1]-self.map.end[1]))
        # self.step_len = 1
        print(self.step_len)
        self.cnt =0

    def init_pop(self):
        path = [self.map.start]
        self.dfs_init(path, self.step_len)

    # 贪心bfs
    def dfs_init(self, path, step_len):
        self.cnt+=1
        if len(self.paths) >= POP_SIZE:
            return
        if len(path) > 15:
            return
        print("{}--{}".format(self.cnt, len(self.paths)))
        last_point = path[len(path) - 1]
        if last_point == self.map.end:
            self.paths.append(copy.copy(path))
            return
        neighbors = self.get_free_neighbors(last_point, step_len)
        for neighbor in neighbors:
            # if len(path) < 2 or neighbor != path[len(path)-2]:
            if neighbor not in path:
                path.append(neighbor)
                self.dfs_init(path, step_len)
                path.pop()

    def get_free_neighbors(self, point, step_len):
        delta_x = abs(self.map.end[0] - point[0])
        delta_y = abs(self.map.end[1] - point[1])
        if delta_x < step_len and delta_y < step_len:
            return self.get_free_neighbors(point, step_len // 2)
        neighbors = []
        x = [-1, 0, 1]
        y = [-1, 0, 1]
        # random.shuffle(x)
        # random.shuffle(y)
        if delta_x < step_len:
            x = [0, 1, -1]
        elif self.map.end[0] > point[0]:
            x = [1, 0, -1]
        if delta_y < step_len:
            y = [0, 1, -1]
        elif self.map.end[1] > point[1]:
            y = [1, 0, -1]
        for i in x:
            for j in y:
                neighbor = (point[0] + i * step_len, point[1] + j * step_len)
                if self.check_point(neighbor) and self.check_line(point, neighbor) is None:
                    neighbors.append(neighbor)
        if len(neighbors) == 0 and step_len != 1:
            return self.get_free_neighbors(point, step_len // 2)
        return neighbors

    def translate_dna(self, dna):
        pass

    def get_fitness(self):
        pass

    def select(self):
        pass

    def crossover(self, parent, pop):
        pass

    def mutate(self, child):
        pass

    def evolve(self):
        pass

    # 检查两点之间是否有障碍物
    def check_line(self, point1, point2):
        delta_x = point2[0] - point1[0]
        delta_y = point2[1] - point1[1]
        bool_x = abs(delta_x) >= abs(delta_y)
        points = []
        if bool_x:
            k = 0
            if delta_x != 0:
                k = delta_y / delta_x
            for i in range(1, delta_x):
                points.append((point1[0] + i, point1[1] + round(k * i)))
        else:
            k = 0
            if delta_y != 0:
                k = delta_x / delta_y
            for i in range(1, delta_y):
                points.append((point1[0] + round(k * i), point1[1] + i))
        for point in points:
            if self.map.map[point[1]][point[0]] == 0:
                return point
        return None

    def check_point(self, point):
        return 0 <= point[0] < self.map.width \
               and 0 <= point[1] < self.map.height \
               and self.map.map[point[0]][point[1]] != 0

    def drawTrace(self, path):
        image = self.map.image.copy()
        self.points = np.array(path).astype(np.int32)
        for i in range(len(self.points) - 1):
            cv2.line(image, (self.points[i][0], self.points[i][1]), (self.points[i + 1][0], self.points[i + 1][1]),
                     (255, 255, 0))
        for point in self.points[1:-1]:
            cv2.circle(image, (point[0], point[1]), 5, (255, 255, 0), thickness=2)
        # self.image = image
        cv2.imshow("Trace"+str(random.random()), image)
        cv2.waitKey(0)
        # cv2.imwrite("Trace.jpg", self.image)

def get_length(point1, point2):
    # return abs(point1[0] - point2[0]) + abs(point1[1] - point2[1])
    return math.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)

def get_path_length(path):
    l = 0
    for i in range(len(path)-1):
        l += get_length(path[i],path[i+1])
    return l

if __name__ == '__main__':
    import sys
    sys.setrecursionlimit(100000)
    _map = Map()
    planner = GeneticPlanner(_map)
    import time
    time_start = time.time()

    planner.init_pop()

    time_end = time.time()
    print('time cost {} s'.format(time_end - time_start))

    lengths = [get_path_length(path) for path in planner.paths]
    print(min(lengths))
    print(np.mean(lengths))

    # planner.drawTrace(planner.paths[0])
    # planner.drawTrace(planner.paths[POP_SIZE-1])
    for i in range(len(planner.paths)):
        planner.drawTrace(planner.paths[i])
