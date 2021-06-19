import cv2
import numpy as np

start = (599,500)
end = (512, 491)

image = np.ones((992, 992, 3), dtype=np.uint8) * 255
# _map = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
# _, _map = cv2.threshold(map, 125, 255, cv2.THRESH_BINARY)
cv2.circle(image, start, 5, (255, 0, 0), thickness=2)
cv2.circle(image, end, 5, (0, 255, 0), thickness=2)

# path = [(599,500),(589,490),(599,490),(599,510),(589,500),(589,510),(609,500),(609,490),(609,510)]
path = [(599,500),(599,550),(549,450),(599,450),(649,450)]

points = np.array(path).astype(np.int32)
for i in range(len(points) - 1):
    cv2.line(image, (points[i][0], points[i][1]), (points[i + 1][0], points[i + 1][1]),
             (255, 255, 0))
for point in points[1:-1]:
    cv2.circle(image, (point[0], point[1]), 5, (255, 255, 0), thickness=2)
# self.image = image
cv2.imshow("Trace", image)
cv2.waitKey(0)