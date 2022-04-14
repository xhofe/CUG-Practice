#pragma once
#include <QtCore/QString>
#include <QtCore/QMap>
#include <QtCore/QFile>
#include <QtCore/QDebug>
#include <QtCore/QStack>
#include <QtWidgets/QMessageBox>

struct TableItem //表格项
{
	QString sgr;
	int num;

	TableItem(QString _sgr, int _num) {
		sgr = _sgr;
		num = _num;
	}

	TableItem() {
		sgr = "";
		num = 0;
	}
};

struct GrammerItem //一个文法
{
	QString left;
	QString right;
	int flag;//.的位置
	QList<QString> looking_character;//展望字符
	GrammerItem(QString _left, QString _right,int _flag=0) {
		this->left = _left;
		this->right = _right;
		this->flag = _flag;
	}
	bool operator==(const GrammerItem item)const {
		if (left!=item.left)
		{
			return false;
		}
		if (right!=item.right)
		{
			return false;
		}
		if (flag!=item.flag)
		{
			return false;
		}
		QSet<QString> a=looking_character.toSet();
		QSet<QString> b=item.looking_character.toSet();
		return a == b;
	}
};

struct Item //一个项目
{
	QMap<QString, int> go; //遇到字符去的状态
	QList<GrammerItem> grammers; //一个项目集的文法

	void print() {
		for each (GrammerItem item in grammers)
		{
			qDebug() << item.left << "->" << item.right << " " << item.flag << " " << item.looking_character;
		}
	}
};

class LR_1
{
public:
	LR_1();
	LR_1(QString filename);
	~LR_1();

	bool read_grammer();//读文法
	bool get_first();
	QSet<QString> get_first(QString  nonterminal);
	bool generate_item_set();//构造项目集
	int hasItem(QList<Item> itemStack, QList<GrammerItem> grammeritemList, int num);
	bool createTable();
	bool verification(QString str);

	void printInfo();
	QList<GrammerItem> get_grammer(QString left);
	QString stackToStr(QStack<QString> stack);
	QString stackToStr(QStack<int> stack);

	bool work();
	QList<Item> get_itemList() {
		return m_itemList;
	}
	QList<QMap<QString, TableItem>> get_table() {
		return m_table;
	}
	QList<QString> get_terminator() {
		return m_terminator;
	}
	QList<QString> get_nonterminal() {
		return m_nonterminal;
	}
	QList<QString> get_analysis_process() {
		return m_analysis_process;
	}

private:
	QString m_filename;

	QList<GrammerItem> m_grammerList;//文法集合
	QList<QString> m_nonterminal;//非终结符集合
	QMap<QString, QSet<QString>> m_firstSet;//first集
	QList<QString> m_terminator;//终结符
	QList<Item> m_itemList;//项目集
	QList<QMap<QString, TableItem>> m_table;
	QList<QString> m_analysis_process;

	QChar epsilon = 'ε';//空字
};

