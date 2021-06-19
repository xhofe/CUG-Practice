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

#include "genetic.h"
#include "utils.h"

#include <pluginlib/class_list_macros.h>
// using namespace std;
// register this planner as a BaseGlobalPlanner plugin
PLUGINLIB_EXPORT_CLASS(Genetic_planner::GeneticPlannerROSV1,
                       nav_core::BaseGlobalPlanner)

// int value;
int mapSize;
bool *OGM;
static const float INFINIT_COST = INT_MAX; // int 的最大值
float infinity = std::numeric_limits<float>::infinity(); // 无穷大
// float tBreak;  // coefficient for breaking ties
ofstream MyExcelFile("RA_result.xlsx", ios::trunc);

int clock_gettime(clockid_t clk_id, struct timespect *tp);

namespace Genetic_planner {

// Default Constructor
GeneticPlannerROSV1::GeneticPlannerROSV1() {}
GeneticPlannerROSV1::GeneticPlannerROSV1(ros::NodeHandle &nh) {
  ROSNodeHandle = nh;
}

GeneticPlannerROSV1::GeneticPlannerROSV1(std::string name,
                                     costmap_2d::Costmap2DROS *costmap_ros) {
  initialize(name, costmap_ros);
}

void GeneticPlannerROSV1::initialize(std::string name,
                                   costmap_2d::Costmap2DROS *costmap_ros) {

  if (!initialized_) {
    initHyperParam();
    costmap_ros_ = costmap_ros;
    costmap_ = costmap_ros_->getCostmap();

    ros::NodeHandle private_nh("~/" + name);

    originX = costmap_->getOriginX();
    originY = costmap_->getOriginY();

    width = costmap_->getSizeInCellsX();
    height = costmap_->getSizeInCellsY();
    resolution = costmap_->getResolution();
    mapSize = width * height;
    // tBreak = 1+1/(mapSize);
    // value = 0;
    _width = width;
    _height = height;

    OGM = new bool[mapSize];
    for (unsigned int iy = 0; iy < costmap_->getSizeInCellsY(); iy++) {
      for (unsigned int ix = 0; ix < costmap_->getSizeInCellsX(); ix++) {
        unsigned int cost = static_cast<int>(costmap_->getCost(ix, iy));
        // cout<<cost;
        if (cost == 0)
          OGM[iy * width + ix] = true;
        else
          OGM[iy * width + ix] = false;
      }
    }

    MyExcelFile << "StartID\tStartX\tStartY\tGoalID\tGoalX\tGoalY\tPlannertime("
                   "ms)\tpathLength\tnumberOfCells\t"
                << endl;

    ROS_INFO("GeneticPlanner planner initialized successfully");
    initialized_ = true;
  } else
    ROS_WARN("This planner has already been initialized... doing nothing");
}

bool GeneticPlannerROSV1::makePlan(
    const geometry_msgs::PoseStamped &start,
    const geometry_msgs::PoseStamped &goal,
    std::vector<geometry_msgs::PoseStamped> &plan) {
  if (!initialized_) {
    ROS_ERROR("The planner has not been initialized, please call initialize() "
              "to use the planner");
    return false;
  }
  if (!ifget) {
    ROS_ERROR("no get all params.");
    return false;
  }
  ROS_DEBUG("Got a start: %.2f, %.2f, and a goal: %.2f, %.2f",
            start.pose.position.x, start.pose.position.y, goal.pose.position.x,
            goal.pose.position.y);
  plan.clear();
  if (goal.header.frame_id != costmap_ros_->getGlobalFrameID()) {
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

  int startCell;
  int goalCell;

  if (isCoordinateInsideMap(startX, startY) && isCoordinateInsideMap(goalX, goalY)) {
    startCell = convertToCellIndex(startX, startY);
    goalCell = convertToCellIndex(goalX, goalY);
    _goal_x = getCellRowID(goalCell);
    _goal_y = getCellColID(goalCell);
    MyExcelFile << startCell << "\t" << start.pose.position.x << "\t"
                << start.pose.position.y << "\t" << goalCell << "\t"
                << goal.pose.position.x << "\t" << goal.pose.position.y;
  } else {
    ROS_WARN("the start or goal is out of the map");
    return false;
  }

  /////////////////////////////////////////////////////////

  // call global planner

  if (isStartAndGoalCellsValid(startCell, goalCell)) {
    vector<int> bestPath;
    bestPath.clear();
    bestPath = GeneticPlanner(startCell, goalCell);
    // if the global planner find a path
    if (bestPath.size() > 0) {
      // convert the path
      for (int i = 0; i < bestPath.size(); i++) {
        float x = 0.0;
        float y = 0.0;
        int index = bestPath[i];
        convertToCoordinate(index, x, y);
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
      for (; it != plan.end(); ++it) {
        path_length += hypot((*it).pose.position.x - last_pose.pose.position.x,
                             (*it).pose.position.y - last_pose.pose.position.y);
        last_pose = *it;
      }
      cout << "The global path length: " << path_length << " meters" << endl;
      MyExcelFile << "\t" << path_length << "\t" << plan.size() << endl;
      // publish the plan
      return true;
    }
    else {
      ROS_WARN("The planner failed to find a path, choose other goal position");
      return false;
    }
  }
  else {
    ROS_WARN("Not valid start or goal");
    return false;
  }
}

// 将传入的原始世界位置转换为对应数组下标
int GeneticPlannerROSV1::convertToCellIndex(float x, float y) {
  int cellIndex;
  float newX = (x - originX) / resolution;
  float newY = (y - originY) / resolution;
  cellIndex = getCellIndex(newY, newX);
  return cellIndex;
}

// 将对应数组下标转换为原始世界位置
void GeneticPlannerROSV1::convertToCoordinate(int index, float &x, float &y) {
  x = getCellColID(index) * resolution;
  y = getCellRowID(index) * resolution;
  x = x + originX;
  y = y + originY;
}

// 判断传入原始世界位置是否在地图中
bool GeneticPlannerROSV1::isCoordinateInsideMap(float x, float y) {
  bool valid = true;
  if ((x - originX) > (width * resolution) || (y - originY) > (height * resolution))
    valid = false;
  return valid;
}

// 遗传算法规划
vector<int> GeneticPlannerROSV1::GeneticPlanner(int startCell, int goalCell) {
  this->startCell = startCell;
  this->goalCell = goalCell;
  vector<int> bestPath;
  timespec time1, time2;
  /* take current time here */
  clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &time1);
  bestPath = findPath(startCell, goalCell);
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

// 起始点与目标点是否有效
bool GeneticPlannerROSV1::isStartAndGoalCellsValid(int startCell, int goalCell) {
  bool isvalid = true;
  bool isFreeStartCell = isFree(startCell);
  bool isFreeGoalCell = isFree(goalCell);
  if (startCell == goalCell) {
    // cout << "The Start and the Goal cells are the same..." << endl;
    isvalid = false;
  } else {
    if (!isFreeStartCell && !isFreeGoalCell) {
      // cout << "The start and the goal cells are obstacle positions..." <<
      // endl;
      isvalid = false;
    } else {
      if (!isFreeStartCell) {
        // cout << "The start is an obstacle..." << endl;
        isvalid = false;
      } else {
        if (!isFreeGoalCell) {
          // cout << "The goal cell is an obstacle..." << endl;
          isvalid = false;
        } else {
          if (findFreeNeighborCell(goalCell).size() == 0) {
            // cout << "The goal cell is encountred by obstacles... "<< endl;
            isvalid = false;
          } else {
            if (findFreeNeighborCell(startCell).size() == 0) {
              // cout << "The start cell is encountred by obstacles... "<< endl;
              isvalid = false;
            }
          }
        }
      }
    }
  }
  return isvalid;
}

// 计算两点距离
float GeneticPlannerROSV1::getMoveCost(int i1, int j1, int i2, int j2) {
  float moveCost = INFINIT_COST; // start cost with maximum value. Change it to
                                 // real cost of cells are connected
  // if cell2(i2,j2) exists in the diagonal of cell1(i1,j1)
  if ((j2 == j1 + 1 && i2 == i1 + 1) || (i2 == i1 - 1 && j2 == j1 + 1) ||
      (i2 == i1 - 1 && j2 == j1 - 1) || (j2 == j1 - 1 && i2 == i1 + 1)) {
    // moveCost = DIAGONAL_MOVE_COST;
    moveCost = 1.414;
  }
  // if cell 2(i2,j2) exists in the horizontal or vertical line with
  // cell1(i1,j1)
  else {
    if ((j2 == j1 && i2 == i1 - 1) || (i2 == i1 && j2 == j1 - 1) ||
        (i2 == i1 + 1 && j2 == j1) || (i1 == i2 && j2 == j1 + 1)) {
      // moveCost = MOVE_COST;
      moveCost = 1;
    }
  }
  return moveCost;
}

// 同上
float GeneticPlannerROSV1::getMoveCost(int CellID1, int CellID2) {
  int i1 = 0, i2 = 0, j1 = 0, j2 = 0;
  i1 = getCellRowID(CellID1);
  j1 = getCellColID(CellID1);
  i2 = getCellRowID(CellID2);
  j2 = getCellColID(CellID2);
  return getMoveCost(i1, j1, i2, j2);
}

// 判断指定点是否有效
bool GeneticPlannerROSV1::isFree(int i, int j) {
  int CellID = getCellIndex(i, j);
  return OGM[CellID];
}

// 同上
bool GeneticPlannerROSV1::isFree(int CellID) { 
  return OGM[CellID]; 
}

// 寻找对应点八个角可以走的位置
/**
**可以增加步长**
*/
vector <int> GeneticPlannerROSV1::findFreeNeighborCell (int CellID){
 
  int rowID=getCellRowID(CellID);
  int colID=getCellColID(CellID);
  int neighborIndex;
  vector <int>  freeNeighborCells;

  for (int i=-1;i<=1;i++)
    for (int j=-1; j<=1;j++){
      //check whether the index is valid
     if ((rowID+i>=0)&&(rowID+i<height)&&(colID+j>=0)&&(colID+j<width)&& (!(i==0 && j==0))){
	      neighborIndex = getCellIndex(rowID+i,colID+j);
        if(isFree(neighborIndex) )
	        freeNeighborCells.push_back(neighborIndex);
	    }
    }
    return  freeNeighborCells;
}

// genetic 

// 寻找路径
vector<int> GeneticPlannerROSV1::findPath(int startCell, int goalCell) {
  vector<int> emptyPath;
  vector<vector<int> > parent = initPopulation();
  vector<vector<int> > chilren;
  if(parent.size()==0)return emptyPath;
  for(int i = 0; i < this->generationNum; i++){
    parent = this->choose(parent);
    chilren = this->cross(parent);
    chilren = this->mutation(chilren);
    parent = chilren;
  }
  // vector<int> bestPath = *std::max_element(parent.begin(),parent.end(),[](vector<int> a,vector<int> b){return getFitness(a)<getFitness(b);});
  return findBest(parent);
}

// 使用几何空间路径规划生成初始路径
/**
1.沿着一个目标点方向走，碰到障碍->栅格化周围的点
2.直接四周走
*/
vector<vector<int> > GeneticPlannerROSV1::initPopulation(){
  vector<vector<int> > paths;
  vector<int> path;
  path.push_back(startCell);
  dfsInit(paths,path);
  return paths;
}
void GeneticPlannerROSV1::dfsInit(vector<vector<int> > &paths,vector<int> path){
  ROS_INFO("dfsInit:%d-%d-%f",paths.size(),path.size(),this->calculateHCost(path.back(),goalCell));
  if(paths.size()>=this->populationSize)return;
  if(path.back()==goalCell){
    vector<int> newPath;
    newPath.assign(path.begin(),path.end());
    paths.push_back(newPath);
    cout<<"new path"<<newPath;
    return;
  }
  vector<int> neighborCells = findFreeNeighborCell(path.back());
  // cout << neighborCells;
  sort(neighborCells.begin(),neighborCells.end(),cmpNeighbor);
  // cout << neighborCells;
  for(int i=0;i < neighborCells.size(); i++){
    if(find(path.begin(),path.end(),neighborCells.at(i))==path.end()){
      path.push_back(neighborCells.at(i));
      dfsInit(paths,path);
      path.pop_back();
    }
  }
}
// 一条路径的适应度
float GeneticPlannerROSV1::getFitness(vector<int> path){
  int fitness = 0;
  for(int i=1;i<path.size();i++){
    fitness += getMoveCost(path.at(i-1), path.at(i));
  }
  return fitness;
}
// 选择函数-轮盘赌
vector<vector<int> > GeneticPlannerROSV1::choose(vector<vector<int> > paths){
  vector<vector<int> >parent;
  vector<double> fitnesses;
  fitnesses.push_back(0);
  int fitnessSum = 0;
  for(int i=0;i<paths.size();i++){
    float fitness = getFitness(paths.at(i));
    fitnesses.push_back(fitness+fitnessSum);
    fitnessSum += fitness;
  }
  int rnd;
  for(int i=0;i<this->populationSize;i++){
    for(int j=0;j<paths.size();j++){
      rnd = rand()%fitnessSum;
      if(rnd>=fitnesses.at(j)){
        parent.push_back(paths.at(j));
        break;
      }
    }
  }
  return paths;
}
// 交叉
vector<vector<int> > GeneticPlannerROSV1::cross(vector<vector<int> > paths){
  for(int i=0;i<paths.size()/2;i+=2){
    vector<int> left = paths.at(i);
    vector<int> right = paths.at(i+1);
    vector<int> sameIdx = findSame(left,right);
    if(sameIdx.size()==0)continue;
    // vector<int> leftEnd;
    // leftEnd.assign(left.begin()+sameIdx.at(0),left.end());
    // leftEnd.insert(leftEnd.end(),std::make_move_iterator(left.begin()+sameIdx.at(0)),std::make_move_iterator(left.end()));
    // left.insert(left.end(),make_move_iterator(right.begin()+sameIdx.at(1)),make_move_iterator(right.end()));
    // right.insert(right.end(),make_move_iterator(leftEnd.begin()),make_move_iterator(leftEnd.end()));
    swapVector(left,right,sameIdx.at(0),sameIdx.at(1));
    paths[i]=left;
    paths[i+1]=right;
  }
  return paths;
}
// 变异
vector<vector<int> > GeneticPlannerROSV1::mutation(vector<vector<int> > paths){
  int N = 1000;
  float rnd;
  for(int i = 0;i<paths.size();i++){
    rnd = rand()%N/(float)N;
    if(rnd < this->mutationRate){
      // 变异
      vector<int> path = paths.at(i);
      if(path.size()<3)continue;
      int idx = rand()%(path.size()-2)+1;
      vector<int> v = vectors_intersection(findFreeNeighborCell(path.at(idx-1)),findFreeNeighborCell(path.at(idx+1)));
      if(v.size()<2)continue;
      int rndIdx = rand()%(v.size());
      path[idx] = v.at(rndIdx);
      paths[i] = path;
    }
  }
  return paths;
}
// 平滑(曲线优化)？


vector<int> GeneticPlannerROSV1::findBest(vector<vector<int> > paths){
  if(paths.size()==0)return vector<int>(0);
  vector<int> bestPath = paths.at(0);
  float bestFitness = getFitness(bestPath);
  for(int i=1; i<paths.size(); ++i){
    float curFitness = getFitness(paths[i]);
    if(curFitness>bestFitness){
      bestFitness = curFitness;
      bestPath = paths[i];
    }
  }
  return bestPath;
}

};
