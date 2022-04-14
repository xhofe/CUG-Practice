#pragma once

#include <QtWidgets/QMainWindow>
#include "ui_syntax_analysis.h"
#include <QtCore/QDebug>
#include <QtWidgets/QFileDialog>
#include <QtGui/QStandardItem>
#include <QtGui/QStandardItemModel>
#include "LR_1.h"

class syntax_analysis : public QMainWindow
{
	Q_OBJECT

public:
	syntax_analysis(QWidget *parent = Q_NULLPTR);
	bool init();

private slots:
	void _choose_file();
	void _ok();
	void _verification();
private:
	Ui::syntax_analysisClass ui;

	QStandardItemModel *model1 = NULL;
	QStandardItemModel *model2 = NULL;

	QString m_filename;
	LR_1 *lr1;
};
