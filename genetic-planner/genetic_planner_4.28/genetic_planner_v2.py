from map import Map
import math
import numpy as np
import cv2
import random
import util
import copy

STEP_SIZE = 50
POP_SIZE = 200  # population size
CROSS_RATE = 0.2  # mating probability (DNA crossover)
MUTATION_RATE = 0.02  # mutation probability
N_GENERATIONS = 1000
MUTATION_STEP_SIZE = 1


class GeneticPlannerV2:
    def __init__(self, _map: Map):
        self.map = _map
        self.map.cal_min_dis()
        self.paths = []
        # self.step_len = util.hcf(abs(self.map.start[0] - self.map.end[0]), abs(self.map.start[1] - self.map.end[1]))
        # print(self.step_len)
        self.step_len = STEP_SIZE
        # self.cnt = 0

    def init_pop(self):
        while len(self.paths) < POP_SIZE:
            # p = Pool(5)
            # p.map(self.dfs_init,[[self.map.start]]*5)
            # path = [self.map.start]
            # self.dfs_init(path)
            path = self.generate_path(self.map.start, self.map.end)
            if path is not None:
                self.paths.append(path)
        self.paths = np.array(self.paths, dtype=object)

    # 两点之间生成一条路径
    def generate_path(self, origin, aim, no_include=None):
        if no_include is None:
            no_include = []
        path = [origin]
        neighbors = self.get_free_neighbors(origin, self.step_len, aim)
        neighbors = [x for x in neighbors if x not in path and x not in no_include]
        if len(neighbors) == 0: return None
        while neighbors[0] != aim:
            p = [x ** 4 for x in range(len(neighbors), 0, -1)]
            s = sum(p)
            rnd = random.randint(0, s)
            i = 0
            neighbor = neighbors[i]
            p_sum = 0
            for x in p:
                p_sum += x
                if p_sum >= rnd: break
                i += 1
                neighbor = neighbors[i]
            path.append(neighbor)
            neighbors = self.get_free_neighbors(neighbor, self.step_len, aim)
            neighbors = [x for x in neighbors if x not in path]
            if len(neighbors) == 0: return None
        path.append(neighbors[0])
        return path

    # 贪心bfs
    # def dfs_init(self, path):
    #     # self.cnt += 1
    #     # if len(self.paths) >= POP_SIZE:
    #     #     return
    #     # if len(path) > 15:
    #     #     return
    #     # print("{}--{}".format(self.cnt, len(self.paths)))
    #     last_point = path[len(path) - 1]
    #     if last_point == self.map.end:
    #         self.paths.append(copy.copy(path))
    #         return
    #     neighbors = self.get_free_neighbors(last_point, self.step_len, self.map.end)
    #     # 根据概率随机选择一个点作为下一个点
    #     neighbors = [x for x in neighbors if x not in path]
    #     if len(neighbors) == 0: return
    #     p = [x ** 3 for x in range(len(neighbors), 0, -1)]
    #     s = sum(p)
    #     rnd = random.randint(0, s)
    #     i = 0
    #     neighbor = neighbors[i]
    #     p_sum = 0
    #     for x in p:
    #         p_sum += x
    #         if p_sum >= rnd: break
    #         i += 1
    #         neighbor = neighbors[i]
    #     path.append(neighbor)
    #     self.dfs_init(path)
    #     # for neighbor in neighbors:
    #     #     # if len(path) < 2 or neighbor != path[len(path)-2]:
    #     #     if neighbor not in path:
    #     #         path.append(neighbor)
    #     #         self.dfs_init(path, step_len)
    #     #         path.pop()

    def get_free_neighbors(self, point, step_len, aim=None):
        neighbors = []
        if aim is None:
            x = [-1, 1]
            y = [-1, 1]
            random.shuffle(x)
            random.shuffle(y)
            for i in x:
                for j in y:
                    neighbor = (point[0] + i * random.randint(step_len//2,step_len), point[1] + j * random.randint(step_len//2,step_len))
                    if self.check_point(neighbor) and self.check_line(point, neighbor) is None:
                        neighbors.append(neighbor)
            if len(neighbors) == 0 and step_len != 1:
                return self.get_free_neighbors(point, step_len // 2, aim)
            return neighbors

        delta_x = abs(aim[0] - point[0])
        delta_y = abs(aim[1] - point[1])
        if self.check_line(point, aim) is None:
            return [aim]
        # if max(delta_x,delta_y) < self.step_len and self.check_line(point, aim) is None:
        #     return [aim]
        if delta_x < step_len and delta_y < step_len:
            return self.get_free_neighbors(point, step_len // 2, aim)
        x = [-1, 0, 1]
        y = [-1, 0, 1]
        # random.shuffle(x)
        # random.shuffle(y)
        if delta_x < step_len:
            x = [0, 1, -1]
        elif aim[0] > point[0]:
            x = [1, 0, -1]
        if delta_y < step_len:
            y = [0, 1, -1]
        elif aim[1] > point[1]:
            y = [1, 0, -1]
        for i in x:
            for j in y:
                neighbor = (point[0] + i * step_len, point[1] + j * step_len)
                if self.check_point(neighbor) and self.check_line(point, neighbor) is None:
                    neighbors.append(neighbor)
        if len(neighbors) == 0 and step_len != 1:
            return self.get_free_neighbors(point, step_len // 2, aim)
        return neighbors

    def cal_fitness(self, path):
        def get_length(point1, point2):
            # return abs(point1[0] - point2[0]) + abs(point1[1] - point2[1])
            return math.sqrt((point1[0] - point2[0]) ** 2 + (point1[1] - point2[1]) ** 2)

        length = 0
        for i in range(len(path) - 1):
            length += get_length(path[i], path[i + 1])
        min_dis = min(self.map.cal_path_min_dis(path),10)
        return max(10000 / length-5,0) + min_dis

    def get_fitness(self):
        return np.array([self.cal_fitness(path) for path in self.paths])

    # 选择
    def select(self, fitness):
        # idx = np.random.choice(np.arange(POP_SIZE), size=POP_SIZE, replace=True, p=fitness / fitness.sum())
        list_fit = fitness.tolist()
        max_index = list_fit.index(max(list_fit))
        # return self.paths[np.ones(POP_SIZE)*max_index]
        paths = []
        for i in range(POP_SIZE):
            paths.append(copy.deepcopy(self.paths[max_index]))
        return paths

    # 交叉
    def crossover(self):
        # paths = self.paths.copy()
        for i in range(0, POP_SIZE, 2):
            if np.random.rand() > CROSS_RATE:
                continue
            path1 = self.paths[i]
            path2 = self.paths[i + 1]
            intersection_points_idx = util.get_intersection(path1, path2)
            if len(intersection_points_idx) != 0:
                rand_point_idx = random.choice(intersection_points_idx)
                path1[rand_point_idx[0]:], path2[rand_point_idx[1]:] = path2[rand_point_idx[1]:], path1[
                                                                                                  rand_point_idx[0]:]
            # else:
            #     rnd_idx1, rnd_idx2 = random.randint(1, len(path1) - 1), random.randint(1, len(path2) - 1)
            #     path = self.generate_path(path1[rnd_idx1], path2[rnd_idx2])
            #     i = 0
            #     while path is None and i < 3:
            #         path = self.generate_path(path1[rnd_idx1], path2[rnd_idx2])
            #         i += 1
            #     if path is not None:
            #         path1, path2 = path1[:rnd_idx1] + path + path2[rnd_idx2 + 1:], path2[:rnd_idx2] + path + path1[
            #                                                                                                  rnd_idx1 + 1:]
            # path1[rnd_idx1:], path2[rnd_idx2:] = path + path2[rnd_idx2 + 1:], path + path1[rnd_idx1 + 1:]
            self.paths[i] = path1
            self.paths[i + 1] = path2
        return self.paths

    # 变异
    def mutate1(self):
        for path in self.paths:
            if random.random() < MUTATION_RATE:
                if len(path) < 3: continue
                rnd_idx = random.randint(1, len(path) - 2)
                repair_path = self.generate_path(path[rnd_idx - 1], path[rnd_idx + 1])
                i = 0
                while repair_path is None and i < 5:
                    repair_path = self.generate_path(path[rnd_idx - 1], path[rnd_idx + 1])
                    i += 1
                if repair_path is None:
                    continue
                path[rnd_idx - 1:] = repair_path + path[rnd_idx + 2:]
        return self.paths

    # 变异:寻找周围的可用点
    def mutate2(self):
        for path in self.paths:
            if random.random() < MUTATION_RATE:
                if len(path) < 3: continue
                rnd_idx = random.randint(1, len(path) - 2)
                mutate_points = self.get_free_neighbors(path[rnd_idx], MUTATION_STEP_SIZE)
                if len(mutate_points) == 0: continue
                for mutate_point in mutate_points:
                    if self.check_line(mutate_point, path[rnd_idx - 1]) is None and self.check_line(mutate_point,
                                                                                            path[rnd_idx + 1]) is None:
                        path[rnd_idx] = mutate_point
                        break
        return self.paths


    def mutate3(self):
        for path in self.paths:
            if len(path) < 3: continue
            for i in range(1,len(path)-1,1):
                if random.random() < MUTATION_RATE:
                    rnd_idx = i
                    mutate_points = self.get_free_neighbors(path[rnd_idx], MUTATION_STEP_SIZE)
                    if len(mutate_points) == 0: continue
                    for mutate_point in mutate_points:
                        if self.check_line(mutate_point, path[rnd_idx - 1]) is None and self.check_line(mutate_point,
                                                                                                path[rnd_idx + 1]) is None:
                            path[rnd_idx] = mutate_point
                            break
        return self.paths

    def evolve(self, fitness):
        self.paths = self.select(fitness)

        np.random.shuffle(self.paths)
        self.paths = self.crossover()
        self.paths = self.mutate2()
        # self.paths = self.mutate3()

    # 检查两点之间是否有障碍物
    def check_line(self, point1, point2):
        points = self.map.get_points_by_line(point1, point2)
        for point in points:
            if self.map.map[point[1]][point[0]] == 0:
                return point
        return None

    def check_point(self, point):
        return 0 <= point[0] < self.map.width \
               and 0 <= point[1] < self.map.height \
               and self.map.map[point[1]][point[0]] != 0


    # 插入控制点bezier
    def path_bezier(self, path):
        control_points = util.get_bezier_control(path, 1 / 8, 1 / 8)
        res = []
        for i in range(len(path) - 1):
            count = abs(path[i + 1][0] - path[i][0]) + abs(path[i + 1][1] - path[i][1])
            res += util.bezier_with_count(path[i], control_points[2 * i], control_points[2 * i + 1], path[i + 1], count)
        return res

    # 直接bezier
    def bezier(self, path):
        i = 0
        res = []
        for i in range(3,len(path),3):
            count = abs(path[i - 3][0] - path[i][0]) + abs(path[i - 3][1] - path[i][1])
            res += util.bezier_with_count(path[i-3], path[i-2], path[i-1], path[i], count)
        res += self.path_bezier(path[i:])
        return res

    def drawTrace(self, path, draw_point=False):
        image = self.map.image.copy()
        self.points = np.array(path).astype(np.int32)
        for i in range(len(self.points) - 1):
            cv2.line(image, (self.points[i][0], self.points[i][1]), (self.points[i + 1][0], self.points[i + 1][1]),
                     (255, 255, 0))
        if draw_point:
            for point in self.points[1:-1]:
                cv2.circle(image, (point[0], point[1]), 5, (255, 255, 0), thickness=2)
            self.image = image
        cv2.imshow("Trace" + str(random.random()), image)
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

def main1():
    import time

    time_start = time.time()
    print("start time: {}".format(time_start))
    _map = Map()
    planner = GeneticPlannerV2(_map)
    planner.init_pop()
    time_end = time.time()
    print('time cost {} s'.format(time_end - time_start))
    max_fitness = []
    # avg_fitness = []
    for i in range(N_GENERATIONS):
        fitness = planner.get_fitness()
        max_fitness.append(max(fitness))
        # avg_fitness.append(np.mean(fitness))
        planner.evolve(fitness)
        if i % 50 ==0:
            time_end = time.time()
            print('time cost {} s'.format(time_end - time_start))
    time_end = time.time()
    print('time cost {} s'.format(time_end - time_start))

    lengths = [get_path_length(path) for path in planner.paths]
    print(min(lengths))
    print(np.mean(lengths))
    # fitness = planner.get_fitness()
    # list_fit = fitness.tolist()
    # max_index = list_fit.index(max(list_fit))
    # print(planner.paths[max_index])
    # planner.drawTrace(planner.paths[max_index])
    # planner.drawTrace(planner.path_bezier(planner.paths[max_index]))
    # planner.drawTrace(planner.bezier(planner.paths[max_index]))
    # print(planner.paths[1])
    # planner.drawTrace(planner.paths[0])
    # planner.drawTrace(planner.paths[POP_SIZE-1])
    # for i in range(len(planner.paths)):
    #     planner.drawTrace(planner.paths[i])

    import matplotlib.pyplot as plt
    x = np.arange(N_GENERATIONS)
    plt.figure()
    plt.plot(x, np.array(max_fitness), label='max_fitness')
    # plt.plot(x, np.array(avg_fitness), color='red', label='avg_fitness')
    plt.show()

    fitness = planner.get_fitness()
    list_fit = fitness.tolist()
    max_index = list_fit.index(max(list_fit))
    print(planner.paths[max_index])
    planner.drawTrace(planner.paths[max_index],True)
    planner.drawTrace(planner.path_bezier(planner.paths[max_index]))
    planner.drawTrace(planner.bezier(planner.paths[max_index]))

def main2():
    _map = Map()
    planner = GeneticPlannerV2(_map)
    planner.init_pop()
    for path in planner.paths:
        planner.drawTrace(path,True)
    for i in range(N_GENERATIONS):
        fitness = planner.get_fitness()
        planner.evolve(fitness)
        print(i)
    # fitness = planner.get_fitness()
    # list_fit = fitness.tolist()
    # max_index = list_fit.index(max(list_fit))
    # print(planner.paths[max_index])
    # planner.drawTrace(planner.paths[max_index],True)
    # planner.drawTrace(planner.path_bezier(planner.paths[max_index]))
    # planner.drawTrace(planner.bezier(planner.paths[max_index]))

if __name__ == '__main__':
    main1()
    # main2()
    # (50, 100), (113, 154), (164, 247), (198, 301), (247, 355), (297, 401), (400, 400)
    # path = [(50, 100), (113, 154), (164, 247), (198, 301), (247, 355), (297, 401), (400, 400)]
    # _map = Map()
    # planner = GeneticPlannerV2(_map)
    # planner.drawTrace(path,True)
    # planner.drawTrace(planner.path_bezier(path))
    # planner.drawTrace(planner.bezier(path))