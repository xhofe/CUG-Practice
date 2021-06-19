#include <fstream>
#include <iomanip>
#include <iostream>
#include <netdb.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#include "ros_planner.h"
// #include "utils.h"

#include <pluginlib/class_list_macros.h>
// using namespace std;
// register this planner as a BaseGlobalPlanner plugin
PLUGINLIB_EXPORT_CLASS(Genetic_planner::GeneticPlannerROSV2,
                       nav_core::BaseGlobalPlanner)

// static const float INFINIT_COST = INT_MAX; // int 的最大值
// float infinity = std::numeric_limits<float>::infinity(); // 无穷大
ofstream MyExcelFile("/home/xhofe/tmp/RA_result.xlsx", ios::trunc);

// int clock_gettime(clockid_t clk_id, struct timespect *tp);
timespec diff(timespec start, timespec end)
{
  timespec temp;
  if ((end.tv_nsec - start.tv_nsec) < 0)
  {
    temp.tv_sec = end.tv_sec - start.tv_sec - 1;
    temp.tv_nsec = 1000000000 + end.tv_nsec - start.tv_nsec;
  }
  else
  {
    temp.tv_sec = end.tv_sec - start.tv_sec;
    temp.tv_nsec = end.tv_nsec - start.tv_nsec;
  }
  return temp;
}

namespace Genetic_planner
{

  // Default Constructor
  GeneticPlannerROSV2::GeneticPlannerROSV2() {}
  GeneticPlannerROSV2::GeneticPlannerROSV2(ros::NodeHandle &nh)
  {
    ROSNodeHandle = nh;
  }

  GeneticPlannerROSV2::GeneticPlannerROSV2(std::string name,
                                       costmap_2d::Costmap2DROS *costmap_ros)
  {
    initialize(name, costmap_ros);
  }

  void GeneticPlannerROSV2::initialize(std::string name,
                                     costmap_2d::Costmap2DROS *costmap_ros)
  {

    if (!initialized_)
    {
      initHyperParam();
      costmap_ros_ = costmap_ros;
      costmap_ = costmap_ros_->getCostmap();

      ros::NodeHandle private_nh("~/" + name);

      ROS_INFO("create grid_map");
      grid_map = new GridMap(costmap_);
      grid_map->calMinDis();

      MyExcelFile << "StartX\tStartY\tGoalX\tGoalY\tPlannertime("
                     "ms)\tpathLength\tnumberOfCells\t"
                  << endl;

      ROS_INFO("GeneticPlanner planner initialized successfully");
      initialized_ = true;
    }
    else
      ROS_WARN("This planner has already been initialized... doing nothing");
  }

  bool GeneticPlannerROSV2::makePlan(
      const geometry_msgs::PoseStamped &start,
      const geometry_msgs::PoseStamped &goal,
      std::vector<geometry_msgs::PoseStamped> &plan)
  {
    if (!initialized_)
    {
      ROS_ERROR("The planner has not been initialized, please call initialize() "
                "to use the planner");
      return false;
    }
    if (!ifget)
    {
      ROS_ERROR("no get all params.");
      return false;
    }
    ROS_INFO("Got a start: %.2f, %.2f, and a goal: %.2f, %.2f",
              start.pose.position.x, start.pose.position.y, goal.pose.position.x,
              goal.pose.position.y);
    plan.clear();
    if (goal.header.frame_id != costmap_ros_->getGlobalFrameID())
    {
      ROS_ERROR("This planner as configured will only accept goals in the %s "
                "frame, but a goal was sent in the %s frame.",
                costmap_ros_->getGlobalFrameID().c_str(),
                goal.header.frame_id.c_str());
      return false;
    }
    tf::Stamped<tf::Pose> goal_tf;
    tf::Stamped<tf::Pose> start_tf;
    poseStampedMsgToTF(goal, goal_tf);
    poseStampedMsgToTF(start, start_tf);

    // convert the start and goal positions

    float startX = start.pose.position.x;
    float startY = start.pose.position.y;
    float goalX = goal.pose.position.x;
    float goalY = goal.pose.position.y;

    Point2d startP = grid_map->convertToGridPoint(startX, startY);
    Point2d goalP = grid_map->convertToGridPoint(goalX, goalY);

    if (grid_map->checkPoint(startP) && grid_map->checkPoint(goalP))
    {
      MyExcelFile << start.pose.position.x << "\t"
                  << start.pose.position.y << "\t"
                  << goal.pose.position.x << "\t"
                  << goal.pose.position.y;
    }
    else
    {
      ROS_WARN("the start or goal is out of the map");
      return false;
    }

    vector<Point2d> bestPath;
    bestPath.clear();
    bestPath = geneticPlanner(startP, goalP);
    // if the global planner find a path
    if (bestPath.size() > 0)
    {
      // convert the path
      for (int i = 0; i < bestPath.size(); i++)
      {
        float x = 0.0;
        float y = 0.0;
        grid_map->convertToCoordinate(bestPath.at(i),x,y);
        geometry_msgs::PoseStamped pose = goal;

        pose.pose.position.x = x;
        pose.pose.position.y = y;
        pose.pose.position.z = 0.0;

        pose.pose.orientation.x = 0.0;
        pose.pose.orientation.y = 0.0;
        pose.pose.orientation.z = 0.0;
        pose.pose.orientation.w = 1.0;

        plan.push_back(pose);
      }

      float path_length = 0.0;

      std::vector<geometry_msgs::PoseStamped>::iterator it = plan.begin();

      geometry_msgs::PoseStamped last_pose;
      last_pose = *it;
      it++;
      for (; it != plan.end(); ++it)
      {
        path_length += hypot((*it).pose.position.x - last_pose.pose.position.x,
                             (*it).pose.position.y - last_pose.pose.position.y);
        last_pose = *it;
      }
      cout << "The global path length: " << path_length << " meters" << endl;
      MyExcelFile << "\t" << path_length << "\t" << plan.size() << endl;
      // publish the plan
      return true;
    }
    else
    {
      ROS_WARN("The planner failed to find a path, choose other goal position");
      return false;
    }
  }

  // 遗传算法规划
  vector<Point2d> GeneticPlannerROSV2::geneticPlanner(Point2d &startP, Point2d &goalP)
  {
    vector<Point2d> bestPath;
    timespec time1, time2;
    /* take current time here */
    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &time1);
    
    ROS_INFO("start genetic planner.");
    GeneticPlanner *planner = new GeneticPlanner(grid_map, populationSize, crossRate, mutationRate, generationNum,stepLen,mutationStepLen,startP,goalP);
    planner->evolve();
    bestPath = planner->bestPath();
    delete planner;

    clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &time2);
    cout << "time to generate best global path by genetic = "
         << (diff(time1, time2).tv_sec) * 1e3 +
                (diff(time1, time2).tv_nsec) * 1e-6
         << " microseconds" << endl;
    MyExcelFile << "\t"
                << (diff(time1, time2).tv_sec) * 1e3 +
                       (diff(time1, time2).tv_nsec) * 1e-6;
    return bestPath;
  }
}