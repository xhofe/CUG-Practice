// PalindromeWord.cpp: 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include <string>
#include <iostream>
using namespace std;
int LCSLength(string str1, string str2, int **b)
{
	int i, j, length1, length2, len;
	length1 = str1.length();
	length2 = str2.length();
	//双指针的方法申请动态二维数组
	int **c = new int*[length1 + 1]; //共有length1+1行
	for (i = 0; i < length1 + 1; i++)
		c[i] = new int[length2 + 1];//共有length2+1列

	for (i = 0; i < length1 + 1; i++)
		c[i][0] = 0;        //第0列都初始化为0
	for (j = 0; j < length2 + 1; j++)
		c[0][j] = 0;        //第0行都初始化为0
	for (i = 1; i < length1 + 1; i++)
	{
		for (j = 1; j < length2 + 1; j++)
		{
			if (str1[i - 1] == str2[j - 1])//由于c[][]的0行0列没有使用，c[][]的第i行元素对应str1的第i-1个元素
			{
				c[i][j] = c[i - 1][j - 1] + 1;
				b[i][j] = 0;          //输出公共子串时的搜索方向
			}
			else if (c[i - 1][j]>c[i][j - 1])
			{
				c[i][j] = c[i - 1][j];
				b[i][j] = 1;
			}
			else
			{
				c[i][j] = c[i][j - 1];
				b[i][j] = -1;
			}
		}
	}

	len = c[length1][length2];
	for (i = 0; i < length1 + 1; i++)    //释放动态申请的二维数组
		delete[] c[i];
	delete[] c;
	return len;
}
void PrintLCS(int **b, string str1, int i, int j)
{
	if (i == 0 || j == 0)
		return;
	if (b[i][j] == 0)
	{
		PrintLCS(b, str1, i - 1, j - 1);//从后面开始递归，所以要先递归到子串的前面，然后从前往后开始输出子串
		cout<<str1[i - 1];//c[][]的第i行元素对应str1的第i-1个元素
	}
	else if (b[i][j] == 1)
		PrintLCS(b, str1, i - 1, j);
	else
		PrintLCS(b, str1, i, j - 1);
}

int main(void)
{
	string str1, str2;
	int i, length1, length2, len;
	cout << "请输入字符串：" << endl;
	cin>>str1;
	length1 = str1.length();
	str2 = str1;
	reverse(str2.begin(), str2.end());
	length2 = length1;
	//双指针的方法申请动态二维数组
	int **b = new int*[length1 + 1];
	for (i = 0; i < length1 + 1; i++)
		b[i] = new int[length2 + 1];
	len = LCSLength(str1, str2, b);
	cout << "最长公共子序列的长度为：" << len << endl;
	cout<<"最长公共子序列为：" << endl;
	PrintLCS(b, str1, length1, length2);
	cout<<endl;
	for (i = 0; i < length1 + 1; i++)//释放动态申请的二维数组
		delete[] b[i];
	delete[] b;
	system("pause");
	return 0;
}