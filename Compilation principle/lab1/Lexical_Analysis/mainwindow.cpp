#include "mainwindow.h"
#include "ui_mainwindow.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    init();
    //(0*10*1)*0*
}

MainWindow::~MainWindow()
{
    delete ui;
    if (model != nullptr) {
        delete model;
    }
    if (lexical != nullptr) {
        delete lexical;
    }
}

void MainWindow::init(){

//    Lexical l("01*1*(0|1)*|12");
//    l.addPlus();
//    l.generateAfterRe();
//    qDebug()<<l.getAfterRe();
//    l.generateNFA();
//    l.getGraph().print(l.getGraph().start);
//    QSet<QString> set;
    //QMap<QString, QMap<QChar, QString>>s;
    //QMap<QChar, QString> a;
    //QMap<QChar, QString> b;
    //qDebug() << (a == b);
    //a.insert('a', "a");
    //b.insert('a', "a");
    //qDebug() << (a == b);
    ui->label_2->setVisible(false);
    ui->le_str->setVisible(false);
    ui->ok_str->setVisible(false);
}

void MainWindow::setTableView()
{
    if (model != nullptr) {
        delete model;
    }
    this->model=new QStandardItemModel;
    this->model->setHorizontalHeaderItem(0,new QStandardItem("Node"));
    alnumList=alnumSet.toList();
//    alnumList.push_back('`');
    int len=alnumList.size();
    this->model->setHorizontalHeaderItem(1, new QStandardItem("Flag"));
    for (int i=0;i<len;i++) {
        this->model->setHorizontalHeaderItem(i+2,new QStandardItem(alnumList.at(i)));
    }
    this->ui->tableView->setModel(model);
    this->ui->tableView->setColumnWidth(0,100);
    this->ui->tableView->setColumnWidth(1, 150);
    for (int i=0;i<len;i++) {
        this->ui->tableView->setColumnWidth(i+2,100);
    }
}

void MainWindow::showDFA()
{
    QMapIterator<QString,QMap<QChar,QString>> iMap(minDFA);
    qDebug()<<tableDFA.size();
    int row=0;
    while (iMap.hasNext()) {
        iMap.next();
        QString str=iMap.key();
//        qDebug()<<str;
        this->model->setItem(row,0,new QStandardItem(str));
        QMap<QChar,QString> map=iMap.value();
        QString flag;
        if (statusMap.value(str)->isStart)
        {
            flag.append(" start");
        }
        if (statusMap.value(str)->isEnd)
        {
            flag.append(" end");
        }
        this->model->setItem(row, 1, new QStandardItem(flag));
        for (int j=0;j<alnumList.size();j++) {
            if(map.contains(alnumList.at(j))){
                this->model->setItem(row,j+2,new QStandardItem(map.value(alnumList.at(j))));
            }else{
                this->model->setItem(row,j+2,new QStandardItem("NULL"));
            }
        }
        row++;
    }
}

void MainWindow::on_ok_re_clicked()
{
    if (lexical!=nullptr){
        delete lexical;
    }
    re=this->ui->le_re->text();
    if (re.length()==0)
    {
        return;
    }
    lexical=new Lexical(re);
    lexical->deal();
    lexical->getGraph().print(lexical->getGraph().start);
    this->alnumSet=lexical->getAlnumSet();
    setTableView();
    tableNFA=lexical->getTableNFA();
    tableDFA=lexical->getTableDFA();
    minDFA = lexical->getMiniDFA();
    statusMap = lexical->getStatusMap();
    showDFA();
}

void MainWindow::on_ok_str_clicked()
{
    str=this->ui->le_str->text();
}
