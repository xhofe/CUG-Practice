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
// #include "grid_map.h"
#include "genetic_planner.h"
using namespace std;
using std::string;

namespace Genetic_planner {

class GeneticPlannerROSV2 : public nav_core::BaseGlobalPlanner {
public:
  GeneticPlannerROSV2(ros::NodeHandle &); // this constructor is may be not needed
  GeneticPlannerROSV2();
  GeneticPlannerROSV2(std::string name, costmap_2d::Costmap2DROS *costmap_ros);

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
    ifget = ros::param::get("step_len", stepLen);
    if(!ifget){
      ROS_WARN("stepLen get error.");
      return; 
    }
    ifget = ros::param::get("mutation_step_len", mutationStepLen);
    if(!ifget){
      ROS_WARN("mutationStepLen get error.");
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
  vector<Point2d> geneticPlanner(Point2d &startP, Point2d &goalP);

  GridMap *grid_map;

  costmap_2d::Costmap2DROS *costmap_ros_;
  costmap_2d::Costmap2D *costmap_;
  bool initialized_;


  // 一些超参数 hyper param
  int populationSize;//种群数量
  float crossRate; //交叉概率
  float mutationRate; //变异概率
  int generationNum; //迭代次数
  int stepLen;
  int mutationStepLen;
  bool ifget; // 是否获得所有参数
};
};
