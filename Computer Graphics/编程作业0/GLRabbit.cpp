// GLRabbit.cpp: 定义控制台应用程序的入口点。
//

#include "stdafx.h"
#include<iostream>
#include<GL/glut.h>
#include<fstream>
#include<vector>
using namespace std;

bool mouseLeftDown;
bool mouseRightDown;
bool mouseMiddleDown;
float mouseX, mouseY;
float cameraDistanceX;
float cameraDistanceY;
float cameraAngleX;
float cameraAngleY;
float times = 1;

vector<GLfloat*> vertices;
vector<int*> faces;

bool readFile(string fileName) {
	ifstream in;
	in.open(fileName);
	if (!in.is_open())
	{
		cout << "open failed" << endl;
		return false;
	}
	while (!in.eof())
	{
		char flag;
		in >> flag;
		if (flag == 'v')
		{
			GLfloat *node = new GLfloat[3];
			in >> node[0] >> node[1] >> node[2];
			vertices.push_back(node);
		}
		else
		{
			int *node = new int[3];
			in >> node[0] >> node[1] >> node[2];
			faces.push_back(node);
		}
	}
	//for (size_t i = 0; i < vertices.size(); i++)
	//{
	//	cout << vertices.at(i)[0] << vertices.at(i)[1] << vertices.at(i)[2] << endl;
	//}
}


static float roangles;
GLfloat position[] = { 1.0, 1.0, 5.0, 0.0 };
void init(void)
{
	glClearColor(1.0, 1.0, 1.0, 0.0);
	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	glEnable(GL_DEPTH_TEST);
}
GLfloat mat[8][4] = {
	{ 0.2,0.2,0.9 },{ 0.0,0.0,1.0 },{ 1.0,0.0,0.0 },{ 0.0,1.0,0.0 },
{ 0.3,0.3,0.4 },{ 0.1,0.3,0.8 },{ 0.7,0.2,0.5 },{ 0.2,0.8,0.3 }
};
void display(void)
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glScalef(times, times, times);//缩放
	glTranslatef(cameraDistanceX, cameraDistanceY, 0);
	glRotatef(cameraAngleX, 1, 0, 0);
	glRotatef(cameraAngleY, 0, 1, 0);
	glPushMatrix();
	//glRotatef(roangles, 0.0, 1.0, 0.0);
	glBegin(GL_TRIANGLES);
	//glScalef(2.0f, 2.0f, 2.0f);
	for (size_t i = 0; i < faces.size() - 1; i++)
	{
		glColor3f(mat[i % 8][0], mat[i % 12][1], mat[i % 12][2]);
		glVertex3f(vertices.at(faces.at(i)[0] - 1)[0], vertices.at(faces.at(i)[0] - 1)[1], vertices.at(faces.at(i)[0] - 1)[2]);
		glVertex3f(vertices.at(faces.at(i)[1] - 1)[0], vertices.at(faces.at(i)[1] - 1)[1], vertices.at(faces.at(i)[1] - 1)[2]);
		glVertex3f(vertices.at(faces.at(i)[2] - 1)[0], vertices.at(faces.at(i)[2] - 1)[1], vertices.at(faces.at(i)[2] - 1)[2]);
	}

	glEnd();
	glPopMatrix();
	glFlush();
}

void reshape(int w, int h)
{
	glViewport(0, 0, (GLsizei)w, (GLsizei)h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluPerspective(60.0, (GLfloat)w / (GLfloat)h, 1.0, 30.0);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	glTranslatef(0.0, 0.0, -3.6);
	glRotatef(45, 1.0, 0.0, 0.0);
}
//void idle()
//{
//	roangles += 0.01f;
//	glutPostRedisplay();
//}

void mouseCB(int button, int state, int x, int y)
{
	mouseX = x;
	mouseY = y;
	times = 1;

	if (button == GLUT_LEFT_BUTTON)
	{
		if (state == GLUT_DOWN)
		{
			mouseLeftDown = true;
		}
		else if (state == GLUT_UP)
			mouseLeftDown = false;
	}

	else if (button == GLUT_RIGHT_BUTTON)
	{
		if (state == GLUT_DOWN)
		{
			mouseRightDown = true;
		}
		else if (state == GLUT_UP)
			mouseRightDown = false;
	}

	/*
	* 鼠标滚轮控制图形缩放
	*/
	else if (state == GLUT_UP && button == GLUT_WHEEL_UP)
	{
		times = 0.016f + 1;
		glutPostRedisplay();
	}

	else if (state == GLUT_UP && button == GLUT_WHEEL_DOWN)
	{
		times = -0.016f + 1;
		glutPostRedisplay();
	}
}

void mouseMotionCB(int x, int y)
{
	cameraAngleX = cameraAngleY = 0;
	cameraDistanceX = cameraDistanceY = 0;

	if (mouseLeftDown)
	{
		cameraAngleY += (x - mouseX) * 0.3f;
		cameraAngleX += (y - mouseY) * 0.3f;
		mouseX = x;
		mouseY = y;
	}
	if (mouseRightDown)
	{
		cameraDistanceX = (x - mouseX) * 0.002f;
		cameraDistanceY = -(y - mouseY) * 0.002f;
		mouseY = y;
		mouseX = x;
	}

	glutPostRedisplay();
}

int main(int argc, char** argv)
{
	readFile("resources/bunny_1k.obj");
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB | GLUT_DEPTH);
	glutInitWindowSize(600, 600);
	glutInitWindowPosition(100, 100);
	glutCreateWindow("hello");
	init();
	glutReshapeFunc(reshape);
	glutDisplayFunc(display);
	glutMouseFunc(mouseCB);
	glutMotionFunc(mouseMotionCB);
	glutMainLoop();
	return 0;
}
