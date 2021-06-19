#include "genetic_planner.h"

template <typename T>
std::vector<int> findSame(const std::vector<T> &nLeft, const std::vector<T> &nRight)
{
  vector<int> idxRes;
  vector<int> left, right;
  for (typename std::vector<T>::const_iterator nIterator = nLeft.begin(); nIterator != nLeft.end(); nIterator++)
  {
    typename vector<T>::const_iterator findRes = std::find(nRight.begin(), nRight.end(), *nIterator);
    if (findRes != nRight.end())
    {
      left.push_back(findRes - nRight.begin());
      right.push_back(nIterator - nLeft.begin());
    }
  }
  int sameSize = left.size();
  if (sameSize == 0)
  {
    return idxRes;
  }
  int randIdx = rand() % sameSize;
  idxRes.push_back(left.at(randIdx));
  idxRes.push_back(right.at(randIdx));
  return idxRes;
}

float randFloat(float left = 0, float right = 1)
{
  return left + (right - left) * rand() / float(RAND_MAX);
}

// template<typename T>
// std::ostream & operator<<(std::ostream & os, std::vector<T> vec)
// {
//     os<<"{";
//     std::copy(vec.begin(), vec.end(), std::ostream_iterator<T>(os," "));
//     os<<"}"<<endl;
//     return os;
// }

std::ostream &operator<<(std::ostream &os, Point2d point)
{
  os << "(" << point.getX() << "," << point.getY() << ")";
  return os;
}

template <typename T>
std::ostream &operator<<(std::ostream &os, std::vector<T> vec)
{
  os << "{";
  for (int i = 0; i < vec.size(); i++)
  {
    os << vec.at(i);
    if (i != vec.size() - 1)
    {
      os << ",";
    }
  }
  os << "}" << endl;
  return os;
}

// 交换部分元素
template <typename T>
bool swapVector(vector<T> &left, vector<T> &right, int lidx, int ridx)
{
  vector<T> leftEnd;
  // vector<T> rightEnd;
  int lSize = left.size();
  int rSize = right.size();
  for (int i = lidx; i < lSize; i++)
  {
    leftEnd.push_back(left.at(i));
  }
  for (int i = 0; i < lSize - lidx; i++)
  {
    left.pop_back();
  }
  for (int i = ridx; i < rSize; i++)
  {
    left.push_back(right.at(i));
  }
  for (int i = 0; i < rSize - ridx; i++)
  {
    right.pop_back();
  }
  for (int i = 0; i < lSize - lidx; i++)
  {
    right.push_back(leftEnd.at(i));
  }
}

//两个vector求交集
template <typename T>
vector<T> vectors_intersection(vector<T> v1, vector<T> v2)
{
  vector<T> v;
  sort(v1.begin(), v1.end());
  sort(v2.begin(), v2.end());
  set_intersection(v1.begin(), v1.end(), v2.begin(), v2.end(), back_inserter(v)); //求交集
  return v;
}

// 返回source去除remove中也有的元素
template <typename T>
vector<T> removeItemInVector(const vector<T> &source, const vector<T> &remove)
{
  vector<T> res;
  for (size_t i = 0; i < source.size(); i++)
  {
    if (find(remove.begin(), remove.end(), source.at(i)) == remove.end())
    {
      res.push_back(source.at(i));
    }
  }
  return res;
}

template <typename T>
vector<T> rouletteWheelSelection(vector<T> sources, int num, vector<float> probs, float probsum = 0)// = 0
{
  // printf("rouletteWheelSelection\n");
  if(sources.size()!=probs.size()){
      printf("error!!!!!!!!!!!!!!!");
  }
  vector<T> res;
  if (probsum == 0)
  {
    for (size_t i = 0; i < probs.size(); i++)
    {
      probsum += probs.at(i);
    }
  }
  vector<float> rands;
  for (size_t i = 0; i < num; i++)
  {
    rands.push_back(randFloat(0, probsum));
  }
  sort(rands.begin(), rands.end());
  float cur_sum = 0;
  int rand_idx = 0;
  for (size_t i = 0; i < probs.size(); i++)
  {
    cur_sum += probs.at(i);
    while (rand_idx < rands.size() && rands.at(rand_idx) <= cur_sum)
    {
      res.push_back(sources.at(i));
      rand_idx++;
    }
  }
  random_shuffle(res.begin(), res.end());
  return res;
}

vector<Point2d> getBezierControl(vector<Point2d> path, float a = 1/4, float b = 1/4){
  vector<Point2d> control_points;
  float ax,ay,bx,by;
  for (size_t i = 0; i < path.size(); i++)
  {
    if (i == 0)
    {
      ax = path[i].getX() + (path[i + 1].getX() - path[i].getX()) * a;
      ay = path[i].getY() + (path[i + 1].getY() - path[i].getY()) * a;
    }else{
      ax = path[i].getX() + (path[i + 1].getX() - path[i - 1].getX()) * a;
      ay = path[i].getY() + (path[i + 1].getY() - path[i - 1].getY()) * a;
    }
    if (i == path.size()-2)
    {
      bx = path[i + 1].getX() - (path[i + 1].getX() - path[i].getX()) * b;
      by = path[i + 1].getY() - (path[i + 1].getY() - path[i].getY()) * b;
    }else{
      bx = path[i + 1].getX() - (path[i + 2].getX() - path[i].getX()) * b;
      by = path[i + 1].getY() - (path[i + 2].getY() - path[i].getY()) * b;
    }
    control_points.push_back(Point2d(round(ax),round(ay)));
    control_points.push_back(Point2d(round(bx),round(by)));
  }
  return control_points;
}

Point2d threeBezier(Point2d p1,Point2d p2,Point2d p3,Point2d p4,float t){
  float parm_1 = pow(1-t,3);
  float parm_2 = 3 * pow(1-t,2) * t;
  float parm_3 = 3 * pow(t, 2) * (1-t);
  float parm_4 = pow(t, 3);
  float px = p1.getX() * parm_1 + p2.getX() * parm_2 + p3.getX() * parm_3 + p4.getX() * parm_4;
  float py = p1.getY() * parm_1 + p2.getY() * parm_2 + p3.getY() * parm_3 + p4.getY() * parm_4;
  return Point2d(px,py);
}

vector<Point2d> bezierWithCount(Point2d p1,Point2d p2,Point2d p3,Point2d p4,int count){
  float diff = 1.0/count;
  vector<Point2d> res;
  res.push_back(p1);
  for (size_t i = 1; i < count; i++)
  {
    res.push_back(threeBezier(p1, p2, p3, p4, diff * i));
  }
  res.push_back(p4);
  return res;
}

vector<Point2d> controlBezier(vector<Point2d> &path){
  vector<Point2d> control_points = getBezierControl(path,1/8,1/8);
  vector<Point2d> res;
  for (size_t i = 0; i < path.size()-1; i++)
  {
    int count = path[i].manhattanDis(path[i+1]);
    vector<Point2d> tmp = bezierWithCount(path[i], control_points[2 * i], control_points[2 * i + 1], path[i + 1], count);
    res.insert(res.end(),tmp.begin(),tmp.end());
  }
  return res;
}

vector<Point2d> directBezier(vector<Point2d> &path){
  int i = 0;
  vector<Point2d> res;
  for (i = 3; i < path.size(); i+=3)
  {
    int count = path[i-3].manhattanDis(path[i]);
    vector<Point2d> tmp = bezierWithCount(path[i-3], path[i-2], path[i-1], path[i], count);
    res.insert(res.end(),tmp.begin(),tmp.end());
  }
  vector<Point2d> leave(path.end()-(path.size()-i),path.end());
  vector<Point2d> tmp = controlBezier(leave);
  res.insert(res.end(),tmp.begin(),tmp.end());
  return res;
}

const vector<Point2d> GeneticPlanner::EMPTY_PATH = vector<Point2d>(0);

GeneticPlanner::GeneticPlanner()
{
}

GeneticPlanner::GeneticPlanner(GridMap *map,int pop_size,float cross_rate,float mutation_rate,int n_generations, int step_len,int mutation_step_len,Point2d start,Point2d goal){
    _map = map;
    _pop_size = pop_size;
    _cross_rate = cross_rate;
    _mutation_rate = mutation_rate;
    _n_generations = n_generations;
    _step_len = step_len;
    _mutation_step_len = mutation_step_len;
    _start = start;
    _goal = goal;
    printf("start:(%d,%d),goal:(%d,%d)\n",_start.getX(),_start.getY(),_goal.getX(),_goal.getY());
}

GeneticPlanner::~GeneticPlanner()
{
}

void GeneticPlanner::initPaths()
{
  // generatePath(_start, _goal);
  // generatePath(_start, _goal);
  // exit(0);
  printf("init paths.\n");
  while (_paths.size() < _pop_size)
  {
    // printf("paths's size: %d", _paths.size());  
    vector<Point2d> path = generatePath(_start, _goal);
    if (path.size() != 0)
    {
        _paths.push_back(path);
    }
  }
}
vector<Point2d> GeneticPlanner::generatePath(Point2d origin, Point2d aim, vector<Point2d> no_includes)
{
    vector<Point2d> empty_path(0);
    vector<Point2d> path;
    path.push_back(origin);
    vector<Point2d> neighbors = _map->getFreeNeighbors(origin, _step_len, aim);
    neighbors = removeItemInVector(neighbors, path);
    neighbors = removeItemInVector(neighbors, no_includes);
    // printf("neighbors's size: %d", neighbors.size());
    // cout << neighbors << endl;
    if (neighbors.size() == 0)
    {
        return empty_path;
    }
    while (neighbors.at(0) != aim)
    {
        vector<float> probs;
        for (size_t i = neighbors.size(); i > 0; i--)
        {
            probs.push_back(pow(i, 3));
        }
        Point2d neighbor = rouletteWheelSelection(neighbors, 1, probs).at(0);
        path.push_back(neighbor);
        neighbors = _map->getFreeNeighbors(neighbor, _step_len, aim);
        neighbors = removeItemInVector(neighbors, path);
        neighbors = removeItemInVector(neighbors, no_includes);
        // printf("neighbors's size: %d", neighbors.size());
        // cout << neighbors << endl;
        if (neighbors.size() == 0)
        {
          // cout << path << endl;
          return empty_path;
        }
    }
    path.push_back(neighbors.at(0));
    return path;
}
float GeneticPlanner::calFitness(vector<Point2d> &path){
    float len = 0;
    for (size_t i = 0; i < path.size()-1; i++)
    {
        len += _map->calDis(path.at(i),path.at(i+1));
    }
    float min_dis = min(10, _map->calPathMinDis(path));
    return (_map->getHeight() + _map->getWidth()) / len + min_dis;
}
vector<float> GeneticPlanner::getFitness(vector<vector<Point2d> > &paths) {
    // printf("getFitness\n");
    vector<float> fitness;
    for (size_t i = 0; i < paths.size(); i++)
    {
        fitness.push_back(calFitness(paths.at(i)));
    }
    return fitness;
}
vector<vector<Point2d> > GeneticPlanner::select(vector<vector<Point2d> > &paths) {
    return rouletteWheelSelection(paths,_pop_size,getFitness(paths));
}
vector<vector<Point2d> > &GeneticPlanner::crossover(vector<vector<Point2d> > &paths) {
    for(int i=0;i<paths.size();i+=2){
        vector<Point2d> left = paths.at(i);
        vector<Point2d> right = paths.at(i+1);
        vector<int> sameIdx = findSame(left,right);
        if(sameIdx.size()!=0){
            swapVector(left,right,sameIdx.at(0),sameIdx.at(1));
            paths[i]=left;
            paths[i+1]=right;
        }
    }
    return paths;
}
vector<vector<Point2d> > &GeneticPlanner::mutate(vector<vector<Point2d> > &paths) {
    for (size_t i = 0; i < paths.size(); i++)
    {
        vector<Point2d> path = paths.at(i);
        if (randFloat()<_mutation_rate)
        {
            if (path.size()<3)
            {
                continue;
            }
            int mutate_idx = randFloat(1,path.size()-2);
            vector<Point2d> mutate_points = _map->getFreeNeighbors(path.at(mutate_idx),_mutation_step_len);
            if (mutate_points.size()==0)
            {
                continue;
            }
            for (size_t j = 0; j < mutate_points.size(); j++)
            {
                if (_map->checkLine(mutate_points.at(j),path.at(mutate_idx-1)) == Point2d::NONE_POINT && _map->checkLine(mutate_points.at(j),path.at(mutate_idx+1)) != Point2d::NONE_POINT)
                {
                    path.at(mutate_idx) = mutate_points.at(j);
                    break;
                }
            }
        }
    }
    return paths;
}
void GeneticPlanner::evolve() {
    initPaths();
    printf("start generation.\n");
    for (size_t i = 1; i <= _n_generations; i++)
    {
        if(i%50==0){
            printf("generation---[%d]/[%d]\n",i, _n_generations);
        }
        printf("select-%d\n", i);
        _paths = select(_paths);
        if (_cross_rate != 0)
        {
          printf("crossover-%d\n", i);        
          _paths = crossover(_paths);
        }
        if(_mutation_rate != 0){
          printf("mutate-%d\n", i);        
          _paths = mutate(_paths);
        }
    }
}

vector<Point2d> GeneticPlanner::bestPath(){
    vector<float> fitness = getFitness(_paths);
    int best_idx = 0;
    for (size_t i = 0; i < fitness.size(); i++)
    {
        if (fitness.at(i)>fitness.at(best_idx))
        {
            best_idx = i;
        }
        
    }
    return _paths.at(best_idx);
}