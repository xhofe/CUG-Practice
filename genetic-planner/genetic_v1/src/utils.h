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
#include <vector>
#include <algorithm>

using namespace std;

int _height = 1,_width = 1,_goal_x = 1,_goal_y = 1;

bool cmpNeighbor(int a, int b){
  // cout<< _width << " " << _goal_x << " " << _goal_y << endl;
  return (abs(_goal_x-a/_width)+abs(_goal_y-a%_width))<(abs(_goal_x-b/_width)+abs(_goal_y-b%_width));
}


std::vector<int> findSame(const std::vector<int> &nLeft,const std::vector<int> &nRight)
{
  vector<int> idxRes;
	vector<int> left,right;
	for (std::vector<int>::const_iterator nIterator = nLeft.begin(); nIterator != nLeft.end(); nIterator++)
	{
    vector<int>::const_iterator findRes = std::find(nRight.begin(),nRight.end(),*nIterator);
		if( findRes != nRight.end())
			left.push_back(findRes-nRight.begin());
      right.push_back(nIterator-nLeft.begin());
	}
  int sameSize = left.size();
  if(sameSize==0)return idxRes;
  int randIdx = rand()%sameSize;
  idxRes.push_back(left.at(randIdx));
  idxRes.push_back(right.at(randIdx));
  return idxRes;
}

template<typename T>
std::ostream & operator<<(std::ostream & os, std::vector<T> vec)
{
    os<<"{";
    std::copy(vec.begin(), vec.end(), std::ostream_iterator<T>(os," "));
    os<<"}"<<endl;
    return os;
}

timespec diff(timespec start, timespec end) {
  timespec temp;
  if ((end.tv_nsec - start.tv_nsec) < 0) {
    temp.tv_sec = end.tv_sec - start.tv_sec - 1;
    temp.tv_nsec = 1000000000 + end.tv_nsec - start.tv_nsec;
  } else {
    temp.tv_sec = end.tv_sec - start.tv_sec;
    temp.tv_nsec = end.tv_nsec - start.tv_nsec;
  }
  return temp;
}

// 交换部分元素
template<typename T>
bool swapVector(vector<T> &left,vector<T> &right,int lidx,int ridx){
    vector<T> leftEnd;
    // vector<T> rightEnd;
    int lSize = left.size();
    int rSize = right.size();
    for(int i = lidx; i < lSize; i++){
      leftEnd.push_back(left.at(i));
    }
    for(int i = 0; i< lSize - lidx; i++){
      left.pop_back();
    }
    for(int i = ridx; i< rSize; i++){
      left.push_back(right.at(i));
    }
    for(int i = 0; i< rSize - ridx; i++){
      right.pop_back();
    }
    for(int i = 0; i < lSize - lidx ;i++){
      right.push_back(leftEnd.at(i));
    }
}

//两个vector求交集
template<typename T>
vector<T> vectors_intersection(vector<T> v1, vector<T> v2) {
    vector<T> v;
    sort(v1.begin(), v1.end());
    sort(v2.begin(), v2.end());
    set_intersection(v1.begin(), v1.end(), v2.begin(), v2.end(), back_inserter(v));//求交集
    return v;
}
