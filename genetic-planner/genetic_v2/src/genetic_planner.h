#ifndef _GENETIC_PLANNER_H_
#define _GENETIC_PLANNER_H_

// #include "point_2d.h"
#include "grid_map.h"
// #include "utils.h"
class GeneticPlanner
{
private:
    // 超参数
    int _pop_size;
    float _cross_rate;
    float _mutation_rate;
    int _n_generations;
    int _mutation_step_len;
    int _step_len;
    // 算法需要的参数
    Point2d _start;
    Point2d _goal;
    vector<vector<Point2d> > _paths;
    GridMap *_map;
public:
    static const vector<Point2d> EMPTY_PATH;
    GeneticPlanner();
    GeneticPlanner(GridMap *map,int pop_size,float cross_rate,float mutation_rate,int n_generations, int step_len,int mutation_step_len,Point2d start,Point2d goal);
    ~GeneticPlanner();
    void initPaths();
    vector<Point2d> generatePath(Point2d origin,Point2d aim,vector<Point2d> no_includes=GeneticPlanner::EMPTY_PATH);
    float calFitness(vector<Point2d> &path);
    vector<float> getFitness(vector<vector<Point2d> > &paths);
    vector<vector<Point2d> > select(vector<vector<Point2d> > &paths);
    vector<vector<Point2d> > &crossover(vector<vector<Point2d> > &paths);
    vector<vector<Point2d> > &mutate(vector<vector<Point2d> > &paths);
    void evolve();
    vector<Point2d> bestPath();
};

#endif