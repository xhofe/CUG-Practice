#ifndef _GRID_MAP_H
#define _GRID_MAP_H

#include "stdio.h"
#include "point_2d.h"
#include <vector>
#include <iostream>
#include <cmath>
#include <algorithm>
#include <costmap_2d/costmap_2d.h>
#include <costmap_2d/costmap_2d_ros.h>
using namespace std;

class GridMap
{
private:
    bool **_map;

    int _height;
    int _width;
    float _origin_x;
    float _origin_y;
    float _resolution;

    int **_min_dis;
    // Point2d _start;
    // Point2d _goal;

public:
    GridMap(/* args */);
    GridMap(costmap_2d::Costmap2D *costmap_);
    ~GridMap();
    // 计算所有点到最近障碍物的距离
    void calMinDis();
    // 获得两点之间的所有点
    const vector<Point2d> getPointsByLine(const Point2d &point1, const Point2d &point2);
    // 计算一条路径离最近障碍物的最近距离
    int calPathMinDis(const vector<Point2d> &path);
    // 计算两个点连成的线离最近障碍物的最近距离
    int calLineMinDis(const Point2d &point1, const Point2d &point2);
    // 计算指定点离最近障碍物的最近距离
    int calPointsMinDis(const vector<Point2d> &points);
    // 检查两点之间是否有障碍物
    const Point2d &checkLine(const Point2d &point1, const Point2d &point2);
    // 检查点是否在地图内
    bool inMap(const Point2d &point);
    // 检查点是否free
    bool checkPoint(const Point2d &point);
    // 计算两点之间距离
    int calDis(const Point2d &point1, const Point2d &point2);
    // 获得周围可用点
    vector<Point2d> getFreeNeighbors(const Point2d &point, int step_len, const Point2d &aim = Point2d::NONE_POINT);
    // Point2d getStart(){return _start;}
    // Point2d getGoal(){return _goal;}
    int getHeight(){return _height;}
    int getWidth(){return _width;}
    // void setStart(Point2d start){_start = start;}
    // void setGoal(Point2d goal){_goal=goal;}
    Point2d convertToGridPoint(float x, float y);
    void convertToCoordinate(Point2d &point, float &x, float &y);
};

#endif