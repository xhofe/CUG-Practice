#include "Lexical.h"

QChar Node::flag='A';
QMap<QString,Node*> Node::nodeMap=QMap<QString,Node*>();
Lexical::Lexical()
{

}

Lexical::Lexical(QString re)
{
    this->m_re=re;
}
Lexical::~Lexical()
{
    Node::flag = 'A';
    QMapIterator<QString, Node*> iMap(statusMap);
    while (iMap.hasNext())
    {
        iMap.next();
        delete iMap.value();
    }
    statusMap.clear();
    QMapIterator<QString, Node*> jMap(Node::nodeMap);
    while (jMap.hasNext())
    {
        jMap.next();
        delete jMap.value();
    }
    Node::nodeMap.clear();
    alnumSet.clear();
    alnumList.clear();
    statusMap.clear();
    tableNFA.clear();
    tableDFA.clear();
    tableDFAbackUp.clear();
    minDFA.clear();
}
void Lexical::setRe(QString re){
    this->m_re=re;
}
bool Lexical::addPlus(){
    QChar ch;
    for(int i=0;i<m_re.length();i++){
        ch=m_re.at(i);
        m_re_plus.append(ch);
        if(isArg(ch)){
            alnumSet.insert(ch);
            if(i<m_re.length()-1){
                ch=m_re.at(i+1);
                if(isArg(ch)||ch=='('){
                    m_re_plus.append(plus);
                }
            }
        }
        else if(ch==')'){
            if(i<m_re.length()-1){
                ch=m_re.at(i+1);
                if(isArg(ch)||ch=='('){
                    m_re_plus.append(plus);
                }
            }
        }
        else if(ch=='*'){
            if(i<m_re.length()-1){
                ch=m_re.at(i+1);
                if(isArg(ch)||ch=='('){
                    m_re_plus.append(plus);
                }
            }
        }
    }
    alnumList = alnumSet.toList();
    return true;
}
bool Lexical::generateAfterRe(){
    if(m_re_plus.length()==0){
        this->m_afterRe="";
        return true;
    }
    QStack<QChar> symbol;//符号栈
    QChar ch;
    for(int i=0;i<m_re_plus.length();i++){
        ch=this->m_re_plus.at(i);
        if(isArg(ch)){
            this->m_afterRe.append(ch);
        }
        else{
            if(ch==')'){
                bool flag=false;//判断括号是否匹配
                while (!symbol.isEmpty()) {
                    ch=symbol.pop();
                    if(ch=='('){
                        flag=true;
                        break;
                    }
                    this->m_afterRe.append(ch);
                }
                if(!flag){
                    qDebug()<<"Brackets do not match";
                    return false;
                }
            }
            else if(ch==plus){
                QChar top=symbol.isEmpty()? NULL:symbol.top();
                if(symbol.isEmpty()||top=='('){
                    symbol.push(ch);
                }
                else {
                    QChar cur=ch;
                    while (!symbol.isEmpty()) {
                        ch=symbol.pop();
                        if(ch=='('){
                            symbol.push(ch);
                            break;
                        }
                        m_afterRe.append(ch);
                    }
                    symbol.push(cur);
                }
            }
            else if(ch=='|'){
                QChar top=symbol.isEmpty()? NULL:symbol.top();
                if(symbol.isEmpty()||top=='('||top==plus){
                    symbol.push(ch);
                }
                else if(top=='|'){
                    QChar cur=ch;
                    while (!symbol.isEmpty()) {
                        ch=symbol.pop();
                        if(ch=='('){
                            symbol.push(ch);
                            break;
                        }
                        m_afterRe.append(ch);
                    }
                    symbol.push(cur);
                }
            }
            else if(ch=='*'){
                QChar top=symbol.isEmpty()? NULL:symbol.top();
                if(symbol.isEmpty()||top=='('||top==plus||top=='|'){
                    symbol.push(ch);
                }
                else if(top=='*'){
                    QChar cur=ch;
                    while (!symbol.isEmpty()) {
                        ch=symbol.pop();
                        if(ch=='('){
                            symbol.push(ch);
                            break;
                        }
                        m_afterRe.append(ch);
                    }
                    symbol.push(cur);
                }
            }
            else if(ch=='('){
                symbol.push(ch);
            }
            else{
                qDebug()<<"Unsupported regular expression.";
                return false;
            }
        }
    }
    while (!symbol.isEmpty()) {
        ch=symbol.pop();
        m_afterRe.append(ch);
    }
    return true;
}

bool Lexical::generateNFA()
{
    QStack<Graph> stack;
    QChar ch;
    for(int i=0;i<this->m_afterRe.length();i++){
        ch=m_afterRe.at(i);
        if(isArg(ch)){
            stack.push(Graph(ch));
        }else if(ch==plus){
            Graph graph1=stack.pop();
            Graph graph2=stack.pop();
            stack.push(series(graph2,graph1));
        }else if(ch=='|'){
            Graph graph1=stack.pop();
            Graph graph2=stack.pop();
            stack.push(parallel(graph1,graph2));
        }else if(ch=='*'){
            Graph graph1=stack.pop();
            stack.push(repeat(graph1));
        }else{
            qDebug()<<"Error";
            return false;
        }
    }
    graphNFA=stack.pop();
    QMapIterator<QString,Node*> iMap(Node::nodeMap);
    while (iMap.hasNext()) {
        iMap.next();
        Node *node=iMap.value();
        QMap<QChar,QList<QString>> _map;
        for(int i=0;i<node->conditions.length();i++){
            if (!_map.contains(node->conditions.at(i)))
            {
                QList<QString> _list;
                _list.append(node->nexts.at(i)->str);
                _map.insert(node->conditions.at(i), _list);
            }
            else
            {
                QList<QString> _list = _map.value(node->conditions.at(i));
                _list.append(node->nexts.at(i)->str);
                _map.insert(node->conditions.at(i), _list);
            }
        }
        tableNFA.insert(node->str,_map);
    }
    return true;
}

bool Lexical::generateDFA()
{
    QList<QString> unList;//未跳转状态
    QSet<QString> resSet;//存放寻找吓一跳的结果
    QList<QChar> alnumList=alnumSet.toList();

//    bool isFlag=false;
    Node *start=graphNFA.start;
//    graphNFA.resetTraversing();
//    qDebug()<<start->str;
    unList.push_back(getNextByCondition(start,epsilon));
    qDebug()<<unList.back();
    while (!unList.empty()) {
        QString str=unList.back();
        unList.pop_back();
        if(statusMap.contains(str)){
            continue;
        }
        Node *newNode=new Node(str);
        //Node::addMap(newNode);
//        if(!isFlag){
//            graphDFA.start=newNode;
//        }
        QString next_str;
        for(int i=0;i<alnumList.length();i++){
            resSet.clear();
            next_str.clear();
            for(int j=0;j<str.length();j++){
                graphNFA.resetTraversing();
                Node *tmp = Node::nodeMap.value(QString(str.at(j)));
                getNextByCondition(resSet,tmp,alnumList.at(i));
            }
            next_str=getStrBySet(resSet);
            if(next_str.length()!=0){
                unList.push_back(next_str);
                newNode->conditions.push_back(alnumList.at(i));
                newNode->next_strs.push_back(next_str);
            }
        }
        if (str.contains(graphNFA.start->str))
        {
            newNode->isStart = true;
        }
        if (str.contains(graphNFA.end->str))
        {
            newNode->isEnd = true;
        }
        statusMap.insert(str,newNode);
    }
    QMapIterator<QString,Node*> iMap(statusMap);
//    qDebug()<<statusMap.size();
    while (iMap.hasNext()) {
        iMap.next();
        Node *node=iMap.value();
        node->isStart=node->str.contains(graphNFA.start->str);
        node->isEnd=node->str.contains(graphNFA.end->str);
        QMap<QChar,QString> _map;
        for(int i=0;i<node->next_strs.length();i++){
            _map.insert(node->conditions.at(i),node->next_strs.at(i));
            node->nexts.push_back(statusMap.value(node->next_strs.at(i)));
        }
        tableDFA.insert(node->str,_map);
    }
    return true;
}

bool Lexical::replaceDFA()
{
    QChar _flag = 'A';
    QMap<QString, QChar> _map;
    QMapIterator<QString, QMap<QChar, QString>> iMap(tableDFA);
    QMap<QString, QMap<QChar, QString>> newTableDFA;
    while (iMap.hasNext())
    {
        iMap.next();
        QMapIterator<QChar, QString> jMap(iMap.value());
        QMap<QChar, QString> newMap;
        QMap<QChar, QString> newMapBackUP;
        QString _str = iMap.key();
        if (!_map.contains(_str))
        {
            _str = _flag;
            _flag = QChar(_flag.toLatin1() + 1);
            _map.insert(iMap.key(), _str.at(0));
        }
        else
        {
            _str = _map.value(_str);
        }
        while (jMap.hasNext())
        {
            jMap.next();
            QString tmp = jMap.value();
            if (!_map.contains(tmp))
            {
                tmp = _flag;
                _flag = QChar(_flag.toLatin1() + 1);
                _map.insert(jMap.value(), tmp.at(0));
            }
            else
            {
                tmp = _map.value(tmp);
            }
            newMap.insert(jMap.key(), tmp);
            newMapBackUP.insert(jMap.key(), tmp);
        }
        newTableDFA.insert(_str, newMap);
        tableDFAbackUp.insert(_str, newMapBackUP);
    }
    tableDFA = newTableDFA;
    QMap<QString, Node*> newStatusMap;
    QMapIterator< QString, Node*> kMap(statusMap);
    while (kMap.hasNext())
    {
        kMap.next();
        Node *_node = kMap.value();
        _node->str = _map.value(kMap.key());
        newStatusMap.insert(_map.value(kMap.key()), _node);
    }
    statusMap = newStatusMap;
    return true;
}

bool Lexical::minimizeDFA()
{
    QMapIterator<QString, Node*> iMap(statusMap);
    QSet<QString> unEnd;
    QSet<QString> end;
    while (iMap.hasNext())
    {
        iMap.next();
        if (iMap.value()->isEnd)
        {
            end.insert(iMap.key());
        }
        else
        {
            unEnd.insert(iMap.key());
        }
    }
    mergeSame(end);
    mergeSame(unEnd);
    minDFA = tableDFA;
    tableDFA = tableDFAbackUp;
    return true;
}

bool Lexical::deal()
{
    this->addPlus();
    this->generateAfterRe();
    this->generateNFA();
    this->generateDFA();
    this->replaceDFA();
    this->minimizeDFA();
    return true;
}

bool Lexical::mergeSame(QSet<QString> set)
{
    QList<QString> list = set.toList();
    QSet<QString> _set;//不用再遍历(已经被删除)
    for (int i=0;i<list.length();i++)
    {
        if (_set.contains(list.at(i)))
        {
            continue;
        }
        QMap<QChar, QString> _map1 = tableDFAbackUp.value(list.at(i));
        for (int j=i+1;j<list.length();j++)
        {
            if (_set.contains(list.at(j)))
            {
                continue;
            }
            QMap<QChar, QString> _map2 = tableDFAbackUp.value(list.at(j));
            if (_map1==_map2)//如果相等
            {
                QMapIterator<QString, Node*> iMap(statusMap);
                while (iMap.hasNext())
                {
                    iMap.next();
                    QString __tmp = iMap.key();
//					Node* ___tmp = iMap.value();
                    for (int k = 0; k < iMap.value()->next_strs.length(); k++)
                    {
                        if (iMap.value()->nexts.at(k)->str==list.at(j))
                        {
                            if (tableDFA.contains(iMap.key()))
                            {
                                QMap<QChar, QString> _map3 = tableDFA.value(iMap.key());
                                tableDFA.remove(iMap.key());
                                _map3.insert(iMap.value()->conditions.at(k), list.at(i));
                                tableDFA.insert(iMap.key(), _map3);
                            }
                        }
                    }
                }
                if (statusMap.value(list.at(j))->isStart)
                {
                    Node *_node = statusMap.value(list.at(i));
                    _node->isStart = true;
                    statusMap.remove(list.at(i));
                    statusMap.insert(list.at(i), _node);
                }
                tableDFA.remove(list.at(j));
                _set.insert(list.at(j));
            }
        }
        _set.insert(list.at(i));
    }
    return true;
}

//QString Lexical::helpConditions(QString _str)
//{
//	QString res;
//	for (int i=0;i<alnumList.length();i++)
//	{
//		QMap<QChar, QString> tmp = tableDFAbackUp.value(_str);
//		if (tmp.contains(alnumList.at(i)))
//		{
//			res.append(tmp.value(alnumList.at(i)));
//		}
//	}
//	return res;
//}

bool Lexical::isArg(QChar ch){
    return isalnum(ch.toLatin1())||ch==' ';
}

QString Lexical::getStrBySet(QSet<QString> &set)
{
    QString res;
    QList<QString> setList=set.toList();
    for(int i=0;i<setList.length();i++){
        res.append(setList.at(i));
    }
    return res;
}

void Lexical::getNextByCondition(QSet<QString> &resSet, Node *node, QChar ch)
{
    if(ch==epsilon){
        resSet.insert(node->str);
//        qDebug()<<resSet.size();
        for(int i=0;i<node->nexts.length();i++){
            if(node->conditions.at(i)==ch){
                resSet.insert(node->nexts.at(i)->str);
                node->isTraversing=true;
                if(!node->nexts.at(i)->isTraversing){
                    getNextByCondition(resSet,node->nexts.at(i),ch);
                }
            }
        }
    }else{
        for(int i=0;i<node->nexts.length();i++){
            if(node->conditions.at(i)==ch){
                resSet.insert(node->nexts.at(i)->str);
                node->isTraversing=true;
                if(!node->nexts.at(i)->isTraversing){
                    getNextByCondition(resSet,node->nexts.at(i),epsilon);
                }
            }
        }
    }
}

QString Lexical::getNextByCondition(Node *node, QChar ch)
{
    QSet<QString> resSet;
    graphNFA.resetTraversing();
    this->getNextByCondition(resSet,node,ch);
//    qDebug()<<resSet.size();
    return getStrBySet(resSet);
}



bool Lexical::verification(QString str)
{
    return false;
}

Graph Lexical::series(Graph graph1, Graph graph2)
{
    graph1.end->isEnd=false;
    graph2.start->isStart=false;
    graph1.end->setNext(epsilon,graph2.start);
    graph1.end=graph2.end;
    return graph1;
}

Graph Lexical::parallel(Graph graph1, Graph graph2)
{
    Graph res;
    res.start->setNext(epsilon,graph1.start);
    res.start->setNext(epsilon,graph2.start);
    graph1.end->setNext(epsilon,res.end);
    graph2.end->setNext(epsilon,res.end);
    return res;
}

Graph Lexical::repeat(Graph graph1)
{
    Graph res(epsilon);
    graph1.end->setNext(epsilon,graph1.start);
    res.start->setNext(epsilon,graph1.start);
    graph1.end->setNext(epsilon,res.end);
    return res;
}

QString Lexical::getAfterRe()
{
    return this->m_afterRe;
}

Graph Lexical::getGraph()
{
    return graphNFA;
}

QSet<QChar> Lexical::getAlnumSet()
{
    return alnumSet;
}

QMap<QString, QMap<QChar, QList<QString>>> Lexical::getTableNFA()
{
    return tableNFA;
}

QMap<QString, QMap<QChar, QString> > Lexical::getTableDFA()
{
    return tableDFA;
}

QMap<QString, QMap<QChar, QString>> Lexical::getMiniDFA()
{
    return minDFA;
}

QMap<QString, Node*> Lexical::getStatusMap()
{
    return statusMap;
}
