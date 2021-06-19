def hcf(x, y):
    if x > y:
        smaller = y
    else:
        smaller = x
    _hcf = 0
    for i in range(1, smaller + 1):
        if (x % i == 0) and (y % i == 0):
            _hcf = i
    return _hcf


def get_intersection(path1, path2):
    res = []
    for i in range(1, len(path1) - 1, 1):
        for j in range(1, len(path2) - 1, 1):
            if path1[i] == path2[j]:
                res.append((i, j))
    return res


def get_bezier_control(path: list[tuple[int, int]], a = 1/4, b = 1/4):
    control_points = []
    for i in range(len(path) - 1):
        if i == 0:
            ax = path[i][0] + (path[i + 1][0] - path[i][0]) * a
            ay = path[i][1] + (path[i + 1][1] - path[i][1]) * a
        else:
            ax = path[i][0] + (path[i + 1][0] - path[i - 1][0]) * a
            ay = path[i][1] + (path[i + 1][1] - path[i - 1][1]) * a
        if i == len(path) - 2:
            bx = path[i + 1][0] - (path[i + 1][0] - path[i][0]) * b
            by = path[i + 1][1] - (path[i + 1][1] - path[i][1]) * b
        else:
            bx = path[i + 1][0] - (path[i + 2][0] - path[i][0]) * b
            by = path[i + 1][1] - (path[i + 2][1] - path[i][1]) * b
        control_points.append((ax, ay))
        control_points.append((bx, by))
    return control_points


def tri_bezier(p1, p2, p3, p4, t):
    parm_1 = (1 - t) ** 3
    parm_2 = 3 * (1 - t) ** 2 * t
    parm_3 = 3 * t ** 2 * (1 - t)
    parm_4 = t ** 3

    px = p1[0] * parm_1 + p2[0] * parm_2 + p3[0] * parm_3 + p4[0] * parm_4
    py = p1[1] * parm_1 + p2[1] * parm_2 + p3[1] * parm_3 + p4[1] * parm_4

    return px, py


def bezier_with_count(p1, p2, p3, p4, count):
    diff = 1 / count
    res = [p1]
    for i in range(1, count):
        res.append(tri_bezier(p1, p2, p3, p4, diff * i))
    res.append(p4)
    return res
