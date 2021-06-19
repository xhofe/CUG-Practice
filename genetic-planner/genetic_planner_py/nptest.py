import numpy as np
import util


def test1():
    # a = np.array([],dtype=np.uint)
    # b = np.append(a,np.array([1,2],dtype=np.uint),axis=0)
    # print(b)
    #
    # for i in ['aa',-1,3]:
    #     print(i)
    # a=(1,2)
    # b=(1,2)
    # print(a==b)
    # print(id(a)==id(b))
    # print(id(a))
    # print(np.random.choice(['a', 'b'], 1, replace=True, p=[0.6, 0.4])[0]
    # print([[1]]*5)
    # print(np.array([(1, 2), (2, 3)]).tolist())
    # print([1, 2] == (1, 2))

    # path1 = np.array([(1,2),(3,4),(5,6),[8,9]])
    # path2 = np.array([(1,2),(3,4),(5,7),[8,9]])
    path1 = [(1, 2), (3, 4), (5, 6), (8, 9)]
    path2 = [(1, 2), (3, 4), (5, 7), (8, 9)]
    # path1 = np.array(path1[1:len(path1)-1],dtype=tuple)
    # path2 = np.array(path2[1:len(path2)-1])
    points = util.get_intersection(path1, path2)
    print(points)
    # for point in points:
    #     print(point)


if __name__ == '__main__':
    path1 = [[(1, 2), (3, 4), (5, 6), (8, 9)], [(1, 3), (2, 2)]]
    # print(path1[0][1:])
    # a = np.array(path1,dtype=object)
    # a0 = a[0]
    # a1 = a[1]
    # a0[2:],a1[1:] = a1[1:],a0[2:]
    # print(a)
    # a = np.array(path1,dtype=object)
    # b = a[np.array([0, 0])]
    a = [[0] * 5 for _ in range(10)]
    print(a[7][3])