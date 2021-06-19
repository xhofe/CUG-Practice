#include "utils.h"


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

float randFloat(float left, float right)
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
template <typename T>
std::ostream &operator<<(std::ostream &os, std::vector<T> vec)
{
  os << "{";
  for (int i = 0; i < vec.size(); i++)
  {
    os << vec.at(i);
    if (i != vec.size() - 1)
    {
      os << " ";
    }
  }
  os << "}" << endl;
  return os;
}

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
vector<T> rouletteWheelSelection(vector<T> sources, int num, vector<float> probs, float probsum)// = 0
{
  unsigned seed;
  seed = time(0);
  srand(seed);
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
    while (rands.at(rand_idx) <= cur_sum)
    {
      res.push_back(sources.at(i));
      rand_idx++;
    }
  }
  random_shuffle(res.begin(), res.end());
  return res;
}