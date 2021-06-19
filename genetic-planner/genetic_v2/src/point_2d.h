#ifndef _POINT2D_H_
#define _POINT2D_H_

#include <cmath>
using namespace std;


class Point2d
{
private:
    int x;
    int y;

public:
    Point2d()
    {
        x = 0;
        y = 0;
    }
    Point2d(int _x, int _y) : x(_x), y(_y)
    {
    }
    ~Point2d(){}
    const int getX() const { return x; }
    const int getY() const { return y; }
    void setX(int _x) { x = _x; }
    void setY(int _y) { y = _y; }
    int manhattanDis(const Point2d &point)const
    {
        return abs(x - point.getX()) + abs(y - point.getY());
    }
    float euclideanDis(const Point2d &point)const
    {
        return sqrt(pow(x - point.getX(), 2) + pow(y - point.getY(), 2));
    }
    Point2d move(int _x, int _y)const{
        Point2d res(x+_x,y+_y);
        return res;
    }
    static const Point2d NONE_POINT;

    bool operator<(const Point2d & point) const {
        if (y == point.y) {
            return x < point.x;
        }
        return y < point.y;
    }

    bool operator==(const Point2d &point) const {
        return x == point.x && y == point.y;
    }

    bool operator!=(const Point2d &point) const {
        return x != point.x || y != point.y;
    }
};

#endif