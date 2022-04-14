#ifndef LEXICAL_H
#define LEXICAL_H
#include<QChar>
#include<QString>
#include<QMap>
#include<QList>
#include<QStack>
#include<QDebug>
#include<QSet>

struct Node{
    static QChar flag;
    static QMap<QString,Node*> nodeMap;
    static void addMap(Node* _node) {
        nodeMap.insert(_node->str, _node);
    }

    QString str;//节点的标记
    QList<QString> next_strs;//下一节点的标记--DFA
    QList<Node*> nexts;//链接的节点
    QList<QChar> conditions;//转换条件
    bool isStart=false;//是否开始节点
    bool isEnd=false;//是否结束节点
    bool isTraversing=false;
    Node(QString _str){
        this->str=_str;
        addMap();
    }
    Node(QString _str,bool _isStart,bool _isEnd){
        this->str=_str;
        isStart=_isStart;
        isEnd=_isEnd;
        addMap();
    }
    Node(bool _isStart,bool _isEnd){
        str=Node::flag;
        Node::flag=QChar(Node::flag.toLatin1()+1);
        isStart=_isStart;
        isEnd=_isEnd;
        addMap();
    }
    void addMap(){
        //Node::nodeMap.insert(str,this);
    }
    //设置下一状态
    void setNext(QChar ch,Node *next){
        conditions.push_back(ch);
        nexts.push_back(next);
        next->isStart=false;
        isEnd=false;
    }
};

struct Graph{
    bool NDFA;
    Node *start;//开始节点
    Node *end;//结束节点

    Graph(){
        start=new Node(true,false);
        end=new Node(false,true);
        Node::addMap(start);
        Node::addMap(end);
    }
    Graph(QChar ch){
        if(ch=='~'){
            return;
        }
        start=new Node(true,false);
        end=new Node(false,true);
        start->setNext(ch,end);
        Node::addMap(start);
        Node::addMap(end);
    }
    void print(Node *node){
        for(int i=0;i<node->nexts.length();i++){
            node->isTraversing=true;
            qDebug()<<node->str<<"-"<<node->conditions.at(i)<<"->"<<node->nexts.at(i)->str;
            if(!node->nexts.at(i)->isTraversing){
                print(node->nexts.at(i));
            }
        }
    }
    void resetTraversing(){
        QList<Node*> nodeList=Node::nodeMap.values();
        for(int i=0;i<nodeList.length();i++){
            nodeList.at(i)->isTraversing=false;
        }
    }
};

class Lexical
{
public:
    //构造
    Lexical();
    Lexical(QString re);
    ~Lexical();
    //处理
    void setRe(QString re);//设置正则表达式
    bool generateAfterRe();//中缀转后缀
    bool addPlus();//添加连接符
    bool generateNFA();
    bool generateDFA();
    bool replaceDFA();
    bool minimizeDFA();
    bool deal();
    //辅助
    bool mergeSame(QSet<QString> set);
    //QString helpConditions(QString _str);
    bool isArg(QChar ch);//判断是否为字母或数字
    QString getStrBySet(QSet<QString> &set);

    void getNextByCondition(QSet<QString> &resSet, Node* node, QChar ch);
    QString getNextByCondition(Node* node,QChar ch);
    //验证
    bool verification(QString str);
    //计算
    Graph series(Graph graph1,Graph graph2);
    Graph parallel(Graph graph1,Graph graph2);
    Graph repeat(Graph graphNFA);
    //输出
    QString getAfterRe();
    Graph getGraph();
    QSet<QChar> getAlnumSet();
    QMap<QString, QMap<QChar, QList<QString>>> getTableNFA();
    QMap<QString,QMap<QChar,QString>> getTableDFA();
    QMap<QString, QMap<QChar, QString>> getMiniDFA();
    QMap<QString, Node*> getStatusMap();

protected:
    const QChar epsilon='`';
    const QChar plus='^';

    QString m_re;
    QString m_re_plus;
    QString m_afterRe;

    Graph graphNFA=Graph('~');
    //Graph graphDFA=Graph('~');
    //Graph graphMinDFA=Graph('~');
    QSet<QChar> alnumSet;//字符集合
    QList<QChar> alnumList;//字符列表
    QMap<QString, Node*> statusMap;//以跳转状态
    QMap<QString,QMap<QChar,QList<QString>>> tableNFA;
    QMap<QString,QMap<QChar,QString>> tableDFA;
    QMap<QString, QMap<QChar, QString>> tableDFAbackUp;
    QMap<QString, QMap<QChar, QString>> minDFA;
};
#endif // LEXICAL_H
