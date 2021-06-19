import time

import cv2
from map_config import MapConfig
import numpy as np


# 使用二维编码
class Map(MapConfig):
    def __init__(self):
        # self.image = None
        # self.map = None
        self.min_dis = [[0] * self.width for _ in range(self.height)]
        self.draw_map()
        self.cal_min_dis()

    def draw_map(self):
        # 绘制地图，地图障碍物（黑色实心），起点（蓝色空心点），终点（绿色实心点）
        self.image = np.ones((self.height, self.width, 3), dtype=np.uint8) * 255
        for shape in self.obstacle:
            if shape["shape"] == "rectangle":
                pt1 = (shape["center"][0] - shape["width"] // 2, shape["center"][0] - shape["height"] // 2)
                pt2 = (shape["center"][0] + shape["width"] // 2, shape["center"][0] + shape["height"] // 2)
                cv2.rectangle(self.image, pt1, pt2, 0, thickness=-1)
            elif shape["shape"] == "circle":
                cv2.circle(self.image, shape["center"], shape["radius"], 0, thickness=-1)

        self.map = cv2.cvtColor(self.image, cv2.COLOR_BGR2GRAY)
        _, self.map = cv2.threshold(self.map, 125, 255, cv2.THRESH_BINARY)
        cv2.circle(self.image, self.start, 5, (255, 0, 0), thickness=2)
        cv2.circle(self.image, self.end, 5, (0, 255, 0), thickness=2)

    # 计算各个点距离障碍物的最短距离,python中非常耗时
    '''
    map:500*500
    py:2s
    c++:0.02s
    '''
    def cal_min_dis(self):
        max_value = 1e10
        for i in range(self.height):
            for j in range(self.width):
                if self.map[i][j] != 0:
                    self.min_dis[i][j] = max_value
                    # pass
        for i in range(self.height):
            for j in range(self.width):
                if self.map[i][j] == 0:
                    continue
                if i > 0:
                    self.min_dis[i][j] = min(self.min_dis[i][j], self.min_dis[i - 1][j] + 1)
                if j > 0:
                    self.min_dis[i][j] = min(self.min_dis[i][j], self.min_dis[i][j - 1] + 1)
        for i in range(self.height - 1, -1, -1):
            for j in range(self.width - 1, -1, -1):
                if self.map[i][j] == 0:
                    continue
                if i + 1 < self.height:
                    self.min_dis[i][j] = min(self.min_dis[i][j], self.min_dis[i + 1][j] + 1)
                if j + 1 < self.width:
                    self.min_dis[i][j] = min(self.min_dis[i][j], self.min_dis[i][j + 1] + 1)

    # TODO:bug
    def get_points_by_line(self, point1, point2):
        if point1 == point2:
            return [point1]
        delta_x = point2[0] - point1[0]
        delta_y = point2[1] - point1[1]
        bool_x = abs(delta_x) >= abs(delta_y)
        points = []
        if bool_x:
            k = 0
            if delta_x != 0:
                k = delta_y / delta_x
            for i in range(1, delta_x, delta_x//abs(delta_x)):
                points.append((point1[0] + i, point1[1] + round(k * i)))
        else:
            k = 0
            if delta_y != 0:
                k = delta_x / delta_y
            for i in range(1, delta_y, delta_y//abs(delta_y)):
                points.append((point1[0] + round(k * i), point1[1] + i))
        return points

    def cal_path_min_dis(self, path):
        min_dis = self.cal_line_min_dis(path[0],path[1])
        for i in range(1,len(path)-1):
            min_dis = min(min_dis,self.cal_line_min_dis(path[i],path[i+1]))
        return min_dis

    def cal_line_min_dis(self, point1, point2):
        points = self.get_points_by_line(point1, point2)
        return self.cal_points_min_dis(points)

    def cal_points_min_dis(self,points):
        return min([self.min_dis[point[1]][point[0]] for point in points])

if __name__ == "__main__":
    _map = Map()
    # time_start = time.time()
    # _map.cal_min_dis()
    # time_end = time.time()
    # print('cal min dis time cost {} s'.format(time_end - time_start))
    # for i in range(_map.height):
    #     for j in range(_map.width):
    #         if _map.map[i][j] == 0:
    #             print("{}-{}".format(i, j))
    # print(_map.get_points_by_line((170, 220),(170, 160)))