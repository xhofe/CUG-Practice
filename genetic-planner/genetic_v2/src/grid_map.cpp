#include "grid_map.h"

GridMap::GridMap(/* args */)
{
    _map = NULL;
    _min_dis = NULL;
    _height = 0;
    _width = 0;
}

GridMap::GridMap(costmap_2d::Costmap2D *costmap_){
    _origin_x = costmap_->getOriginX();
    _origin_y = costmap_->getOriginY();

    _width = costmap_->getSizeInCellsX();
    _height = costmap_->getSizeInCellsY();
    _resolution = costmap_->getResolution();

    _map = new bool*[_height];
    for (size_t i = 0; i < _height; i++)
    {
        _map[i] = new bool[_width];
    }

    for (unsigned int iy = 0; iy < costmap_->getSizeInCellsY(); iy++) {
      for (unsigned int ix = 0; ix < costmap_->getSizeInCellsX(); ix++) {
        unsigned int cost = static_cast<int>(costmap_->getCost(ix, iy));
        _map[iy][ix] = cost==0;
      }
    }
    printf("create grid map finished.\n");
}

GridMap::~GridMap()
{
    if (_map != NULL)
    {
        for (size_t i = 0; i < _height; i++)
        {
            delete[] _map[i];
        }
        delete[] _map;
    }
    if (_min_dis != NULL)
    {
        for (size_t i = 0; i < _height; i++)
        {
            delete[] _min_dis[i];
        }
        delete[] _min_dis;
    }
}

void GridMap::calMinDis()
{
    printf("cal min dis.\n");
    if (_map == NULL)
    {
        printf("map haven't init.\n");
        return;
    }

    _min_dis = new int *[_height];
    for (size_t i = 0; i < _height; i++)
    {
        _min_dis[i] = new int[_width];
    }
    int MAX_INT = 0xffffffff >> 1;
    for (size_t i = 0; i < _height; i++)
    {
        for (size_t j = 0; j < _width; j++)
        {
            if (_map[i][j])
            {
                _min_dis[i][j] = MAX_INT;
            }
            else
            {
                _min_dis[i][j] = 0;
            }
        }
    }
    for (size_t i = 0; i < _height; i++)
    {
        for (size_t j = 0; j < _width; j++)
        {
            if (!_map[i][j])
            {
                continue;
            }
            if (i > 0 && _min_dis[i - 1][j] < _min_dis[i][j])
            {
                _min_dis[i][j] = _min_dis[i - 1][j] + 1;
            }
            if (j > 0 && _min_dis[i][j - 1] < _min_dis[i][j])
            {
                _min_dis[i][j] = _min_dis[i][j - 1] + 1;
            }
        }
    }
    for (int i = _height - 1; i >= 0; i--)
    {
        for (int j = _width - 1; j >= 0; j--)
        {
            if (!_map[i][j])
            {
                continue;
            }
            if (i + 1 < _height && _min_dis[i + 1][j] < _min_dis[i][j])
            {
                _min_dis[i][j] = _min_dis[i + 1][j] + 1;
            }
            if (j + 1 < _width && _min_dis[i][j + 1] < _min_dis[i][j])
            {
                _min_dis[i][j] = _min_dis[i][j + 1] + 1;
            }
        }
    }
    printf("finished cal min dis.\n");
}

const vector<Point2d> GridMap::getPointsByLine(const Point2d &point1, const Point2d &point2)
{
    if (point1 == point2)
    {
        return vector<Point2d>(1, point1);
    }
    float delta_x = point2.getX() - point1.getX();
    float delta_y = point2.getY() - point1.getY();
    bool bool_x = abs(delta_x) > abs(delta_y);
    vector<Point2d> points;
    if (bool_x)
    {
        float k = 0;
        if (delta_x != 0)
        {
            k = delta_y / delta_x;
        }
        int symbol = delta_x > 0 ? 1 : -1;
        for (size_t i = 0; i < abs(delta_x); i++)
        {
            points.push_back(Point2d(point1.getX() + i * symbol, point1.getY() + round(k * i * symbol)));
        }
    }
    else
    {
        float k = 0;
        if (delta_y != 0)
        {
            k = delta_x / delta_y;
        }
        int symbol = delta_y > 0 ? 1 : -1;
        for (size_t i = 0; i < abs(delta_y); i++)
        {
            points.push_back(Point2d(point1.getX() + round(k * i * symbol), point1.getY() + i * symbol));
        }
    }
    return points;
}

int GridMap::calPathMinDis(const vector<Point2d> &path)
{
    if (path.size()<2)
    {
        for (size_t i = 0; i < path.size(); i++)
        {
            printf("(%d-%d)",path.at(i));
        }
        printf("\n");
        return 10;
    }
    
    int min_dis = calLineMinDis(path.at(0), path.at(1));
    for (size_t i = 1; i < path.size() - 1; i++)
    {
        min_dis = min(min_dis, calLineMinDis(path.at(i), path.at(i + 1)));
    }
    return min_dis;
}

int GridMap::calLineMinDis(const Point2d &point1, const Point2d &point2)
{
    vector<Point2d> points = getPointsByLine(point1, point2);
    return calPointsMinDis(points);
}

int GridMap::calPointsMinDis(const vector<Point2d> &points)
{
    int min_dis = _min_dis[points.at(0).getY()][points.at(0).getX()];
    for (size_t i = 1; i < points.size(); i++)
    {
        min_dis = min(min_dis, _min_dis[points.at(i).getY()][points.at(i).getX()]);
    }
    return min_dis;
}

const Point2d &GridMap::checkLine(const Point2d &point1, const Point2d &point2)
{
    vector<Point2d> points = getPointsByLine(point1, point2);
    for (size_t i = 0; i < points.size(); i++)
    {
        if (!checkPoint(points.at(i)))
        {
            return points.at(i);
        }
    }
    return Point2d::NONE_POINT;
}

bool GridMap::inMap(const Point2d &point)
{
    return point.getX() >= 0 && point.getX() < _width && point.getY() >= 0 && point.getY() < _height;
}

bool GridMap::checkPoint(const Point2d &point)
{
    return inMap(point) && _map[point.getY()][point.getX()];
}

int GridMap::calDis(const Point2d &point1, const Point2d &point2)
{
    return point1.manhattanDis(point2);
}

vector<Point2d> GridMap::getFreeNeighbors(const Point2d &point, int step_len, const Point2d &aim)
{
    vector<Point2d> neighbors;
    if (aim == Point2d::NONE_POINT)
    {
        int tmp[3] = {-1, 0, 1};
        vector<int> x(tmp, tmp + 3);
        vector<int> y(tmp, tmp + 3);
        random_shuffle(x.begin(), x.end());
        random_shuffle(y.begin(), y.end());
        for (size_t i = 0; i < 3; i++)
        {
            for (size_t j = 0; j < 3; j++)
            {
                Point2d neighbor = point.move(x.at(i) * step_len, y.at(j) * step_len);
                if (checkLine(point, neighbor) != Point2d::NONE_POINT)
                {
                    neighbors.push_back(neighbor);
                }
            }
        }
        if (neighbors.size() == 0 && step_len != 1)
        {
            return getFreeNeighbors(point, step_len / 2, aim);
        }
        return neighbors;
    }
    int delta_x = abs(aim.getX() - point.getX());
    int delta_y = abs(aim.getY() - point.getY());
    if (checkLine(point, aim) == Point2d::NONE_POINT) //&& max(delta_x,delta_y) < step_len
    {
        neighbors.push_back(aim);
        return neighbors;
    }
    if (delta_x < step_len && delta_y < step_len)
    {
        return getFreeNeighbors(point, step_len / 2, aim);
    }
    int tmp_x[3] = {-1, 0, 1};
    int tmp_y[3] = {-1, 0, 1};
    if (delta_x < step_len)
    {
        tmp_x[0] = 0;
        tmp_x[1] = -1;
    }
    else if (aim.getX() > point.getX())
    {
        tmp_x[0] = 1;
        tmp_x[2] = -1;
    }
    if (delta_y < step_len)
    {
        tmp_y[0] = 0;
        tmp_y[1] = -1;
    }
    else if (aim.getY() > point.getY())
    {
        tmp_y[0] = 1;
        tmp_y[2] = -1;
    }
    vector<int> x(tmp_x, tmp_x + 3);
    vector<int> y(tmp_y, tmp_y + 3);
    for (size_t i = 0; i < 3; i++)
    {
        for (size_t j = 0; j < 3; j++)
        {
            Point2d neighbor = point.move(x.at(i) * step_len, y.at(j) * step_len);
            if (checkLine(point, neighbor) == Point2d::NONE_POINT)
            {
                neighbors.push_back(neighbor);
            }
        }
    }
    if (neighbors.size() == 0 && step_len != 1)
    {
        return getFreeNeighbors(point, step_len / 2, aim);
    }
    return neighbors;
}

Point2d GridMap::convertToGridPoint(float x, float y){
    int newX = round((x - _origin_x) / _resolution);
    int newY = round((y - _origin_y) / _resolution);
    Point2d point(newX,newY);
    return point;
}
void GridMap::convertToCoordinate(Point2d &point, float &x, float &y){
    x = point.getX() * _resolution + _origin_x;
    y = point.getY() * _resolution + _origin_y;
}