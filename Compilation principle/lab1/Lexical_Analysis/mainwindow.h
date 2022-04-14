#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QDebug>
#include <QStandardItem>
#include <QStandardItemModel>
#include <QList>
#include "Lexical.h"

QT_BEGIN_NAMESPACE
namespace Ui { class MainWindow; }
QT_END_NAMESPACE

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();
    void init();
    void setTableView();
    void showDFA();

private slots:
    void on_ok_re_clicked();

    void on_ok_str_clicked();

private:
    Ui::MainWindow *ui;
    Lexical *lexical=nullptr;
    QStandardItemModel *model=nullptr;
    QString re;
    QString str;

    QSet<QChar> alnumSet;
    QList<QChar> alnumList;
    QMap<QString, QMap<QChar, QList<QString>>> tableNFA;
    QMap<QString,QMap<QChar,QString>> tableDFA;
    QMap<QString, QMap<QChar, QString>> minDFA;
    QMap<QString, Node*> statusMap;
};
#endif // MAINWINDOW_H
