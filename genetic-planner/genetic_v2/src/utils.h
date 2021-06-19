#ifndef _UTILS_H_
#define _UTILS_H_

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

template <typename T>
std::vector<int> findSame(const std::vector<T> &nLeft, const std::vector<T> &nRight);

float randFloat(float left = 0, float right = 1);

template <typename T>
std::ostream &operator<<(std::ostream &os, std::vector<T> vec);

timespec diff(timespec start, timespec end);

// 交换部分元素
template <typename T>
bool swapVector(vector<T> &left, vector<T> &right, int lidx, int ridx);

//两个vector求交集
template <typename T>
vector<T> vectors_intersection(vector<T> v1, vector<T> v2);

// 返回source去除remove中也有的元素
template <typename T>
vector<T> removeItemInVector(const vector<T> &source, const vector<T> &remove);

template <typename T>
vector<T> rouletteWheelSelection(vector<T> sources, int num, vector<float> probs, float probsum = 0);

#endif