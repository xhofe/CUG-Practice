#pragma once
#include <QtCore/QString>
#include <QtCore/QMap>
#include <QtCore/QFile>
#include <QtCore/QDebug>
#include <QtCore/QStack>
#include <QtWidgets/QMessageBox>

struct TableItem //�����
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

struct GrammerItem //һ���ķ�
{
	QString left;
	QString right;
	int flag;//.��λ��
	QList<QString> looking_character;//չ���ַ�
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

struct Item //һ����Ŀ
{
	QMap<QString, int> go; //�����ַ�ȥ��״̬
	QList<GrammerItem> grammers; //һ����Ŀ�����ķ�

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

	bool read_grammer();//���ķ�
	bool get_first();
	QSet<QString> get_first(QString  nonterminal);
	bool generate_item_set();//������Ŀ��
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

	QList<GrammerItem> m_grammerList;//�ķ�����
	QList<QString> m_nonterminal;//���ս������
	QMap<QString, QSet<QString>> m_firstSet;//first��
	QList<QString> m_terminator;//�ս��
	QList<Item> m_itemList;//��Ŀ��
	QList<QMap<QString, TableItem>> m_table;
	QList<QString> m_analysis_process;

	QChar epsilon = '��';//����
};

