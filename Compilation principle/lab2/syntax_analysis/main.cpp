#include "syntax_analysis.h"
#include <QtWidgets/QApplication>

int main(int argc, char *argv[])
{
	QApplication a(argc, argv);
	syntax_analysis w;
	w.show();
	return a.exec();
}
