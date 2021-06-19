#include <netdb.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

/** include ros libraries**********************/
#include <ros/ros.h>

#include <actionlib/client/simple_action_client.h>
#include <move_base_msgs/MoveBaseAction.h>

#include <geometry_msgs/PoseStamped.h>
#include <geometry_msgs/PoseWithCovarianceStamped.h>
#include <geometry_msgs/Twist.h>
#include <move_base_msgs/MoveBaseActionGoal.h>
#include <move_base_msgs/MoveBaseGoal.h>

#include "sensor_msgs/LaserScan.h"
#include "sensor_msgs/PointCloud2.h"

#include <nav_msgs/GetPlan.h>
#include <nav_msgs/OccupancyGrid.h>
#include <nav_msgs/Odometry.h>
#include <nav_msgs/Path.h>

#include <tf/tf.h>
#include <tf/transform_datatypes.h>
#include <tf/transform_listener.h>
/** ********************************************/

#include <boost/foreach.hpp>
//#define forEach BOOST_FOREACH

/** for global path planner interface */
#include <costmap_2d/costmap_2d.h>
#include <costmap_2d/costmap_2d_ros.h>
#include <nav_core/base_global_planner.h>

#include <angles/angles.h>
#include <geometry_msgs/PoseStamped.h>

//#include <pcl_conversions/pcl_conversions.h>
#include <base_local_planner/costmap_model.h>
#include <base_local_planner/world_model.h>

#include <set>
using namespace std;
using std::string;

#ifndef RASTAR_ROS_CPP
#define RASTAR_ROS_CPP

namespace Genetic_planner {

class GeneticPlannerROSV1 : public nav_core::BaseGlobalPlanner {
public:
  GeneticPlannerROSV1(ros::NodeHandle &); // this constructor is may be not needed
  GeneticPlannerROSV1();
  GeneticPlannerROSV1(std::string name, costmap_2d::Costmap2DROS *costmap_ros);

  ros::NodeHandle ROSNodeHandle;

  /** overriden classes from interface nav_core::BaseGlobalPlanner **/
  void initialize(std::string name, costmap_2d::Costmap2DROS *costmap_ros);
  void initHyperParam(){
    ifget = true;
    ifget = ros::param::get("population_size", populationSize);
    if(!ifget){
      ROS_WARN("populationSize get error.");
      return;
    }
    ifget = ros::param::get("cross_rate", crossRate);
    if(!ifget){
      ROS_WARN("crossRate get error.");
      return;
    }
    ifget = ros::param::get("mutation_rate", mutationRate);
    if(!ifget){
      ROS_WARN("mutationRate get error.");
      return;
    }
    ifget = ros::param::get("generation_num", generationNum);
    if(!ifget){
      ROS_WARN("generationNum get error.");
      return; 
    }
    unsigned seed;  // Random generator seed
    // Use the time function to get a "seed” value for srand
    seed = time(0);
    srand(seed);
    ROS_INFO("all param get succeed.%d,%f,%f,%d",populationSize,crossRate,mutationRate,generationNum);
  }
  bool makePlan(const geometry_msgs::PoseStamped &start,
                const geometry_msgs::PoseStamped &goal,
                std::vector<geometry_msgs::PoseStamped> &plan);
  int convertToCellIndex(float x, float y);
  void convertToCoordinate(int index, float &x, float &y);
  bool isCoordinateInsideMap(float x, float y);
  vector<int> GeneticPlanner(int startCell, int goalCell);
  vector<int> findPath(int startCell, int goalCell);
  float calculateHCost(int cellID, int goalCell) {
    int x1 = getCellRowID(goalCell);
    int y1 = getCellColID(goalCell);
    int x2 = getCellRowID(cellID);
    int y2 = getCellColID(cellID);
    return abs(x1 - x2) + abs(y1 - y2);
  }
  vector <int> findFreeNeighborCell (int CellID);
  bool isStartAndGoalCellsValid(int startCell, int goalCell);
  float getMoveCost(int CellID1, int CellID2);
  float getMoveCost(int i1, int j1, int i2, int j2);
  bool isFree(int CellID); // returns true if the cell is Free
  bool isFree(int i, int j);

  int getCellIndex(int i, int j) // （行，列）
  {
    return (i * width) + j;
  }
  int getCellRowID(int index) // get the row ID from cell index
  {
    return index / width;
  }
  int getCellColID(int index) // get colunm ID from cell index
  {
    return index % width;
  }


  // genetic 
  // 使用几何空间路径规划生成初始路径
  vector<vector<int> > initPopulation();
  void dfsInit(vector<vector<int> > &paths,vector<int> path);
  // 一条路径的适应度
  float getFitness(vector<int> path);
  // 选择函数
  vector<vector<int> > choose(vector<vector<int> > paths);
  // 交叉
  vector<vector<int> > cross(vector<vector<int> > paths);
  // 变异
  vector<vector<int> > mutation(vector<vector<int> > paths);
  // 平滑(曲线优化)？

  vector<int> findBest(vector<vector<int> > paths);

  float originX;
  float originY;
  float resolution;
  costmap_2d::Costmap2DROS *costmap_ros_;
  // double step_size_, min_dist_from_robot_;
  costmap_2d::Costmap2D *costmap_;
  bool initialized_;
  int width;
  int height;

  int startCell;
  int goalCell;

  // 一些超参数 hyper param
  int populationSize;//种群数量
  float crossRate; //交叉概率
  float mutationRate; //变异概率
  int generationNum; //迭代次数
  bool ifget; // 是否获得所有参数
};
};
#endif