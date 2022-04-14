#include "syntax_analysis.h"

syntax_analysis::syntax_analysis(QWidget *parent)
	: QMainWindow(parent)
{
	ui.setupUi(this);
	init();
}

bool syntax_analysis::init()
{
	//QString s= "N={A B C}";
	//qDebug() << s.mid(3,s.length()-4);

	//LR_1 lr1("gramme.txt");
	//lr1.read_grammer();
	//lr1.get_first();
	//lr1.generate_item_set();
	//lr1.createTable();
	//lr1.verification("[i,i]");

	//lr1.printInfo();
	connect(this->ui.choose_file, SIGNAL(clicked()), this, SLOT(_choose_file()));
	connect(this->ui.ok, SIGNAL(clicked()), this, SLOT(_ok()));
	connect(this->ui.verification, SIGNAL(clicked()), this, SLOT(_verification()));
	lr1 = NULL;
	return false;
}

void syntax_analysis::_choose_file()
{
	m_filename = QFileDialog::getOpenFileName(this, tr("choose file"), ".", tr("Text(*.txt)"));
	this->ui.file->setText(m_filename);
}

void syntax_analysis::_ok()
{
	if (lr1!=NULL)
	{
		delete lr1;
		lr1 = NULL;
	}
	if (model1!=NULL)
	{
		delete model1;
		model1 = NULL;
	}
	if (model2!=NULL)
	{
		delete model2;
		model2 = NULL;
	}
	lr1 = new LR_1(m_filename);
	if (!lr1->work())
	{
		return;
	}
	
	QList<Item> itemSet = lr1->get_itemList();
	QList<QMap<QString, TableItem>> table = lr1->get_table();
	model1 = new QStandardItemModel;
	ui.itemSet->setModel(model1);
	model1->setHorizontalHeaderItem(0, new QStandardItem("num"));
	model1->setHorizontalHeaderItem(1, new QStandardItem("item"));
	model1->setHorizontalHeaderItem(2, new QStandardItem("looking"));
	ui.itemSet->setColumnWidth(0, 50);
	ui.itemSet->setColumnWidth(1, 400);
	ui.itemSet->setColumnWidth(0, 50);
	int row = 0;
	for each (Item item in itemSet)
	{
		model1->setItem(row, 0, new QStandardItem(QString::number(row)));
		QList<GrammerItem> grammers = item.grammers;
		ui.itemSet->setRowHeight(row, grammers.length() * 25);
		QString _grammer, _looking;
		for each (GrammerItem gram in grammers)
		{
			QString right = gram.right;
			right.insert(gram.flag, '.');
			_grammer.append(gram.left + "->" + right + '\n');
			QString _s;
			for each (QString var in gram.looking_character)
			{
				_s.append(var + ' ');
			}
			_looking.append(_s + '\n');
		}
		_grammer = _grammer.left(_grammer.length() - 1);
		_looking = _looking.left(_looking.length() - 1);
		model1->setItem(row, 1, new QStandardItem(_grammer));
		model1->setItem(row, 2, new QStandardItem(_looking));
		row++;
	}
	model2 = new QStandardItemModel;
	QList<QString> ter = lr1->get_terminator();
	QList<QString> non = lr1->get_nonterminal();
	int col = 0;
	QMap<QString, int> col_map;
	model2->setHorizontalHeaderItem(col++, new QStandardItem("num"));
	for each (QString var in ter)
	{
		col_map.insert(var, col);
		model2->setHorizontalHeaderItem(col++, new QStandardItem(var));
	}
	col_map.insert("$", col);
	model2->setHorizontalHeaderItem(col++, new QStandardItem("$"));
	for each (QString var in non)
	{
		col_map.insert(var, col);
		model2->setHorizontalHeaderItem(col++, new QStandardItem(var));
	}
	ui.table->setModel(model2);
	for (size_t i = 0; i < col; i++)
	{
		ui.table->setColumnWidth(i, 100);
	}
	row = 0;
	for each (QMap<QString, TableItem> map in table)
	{
		model2->setItem(row, 0, new QStandardItem(QString::number(row)));
		QMapIterator<QString, TableItem> iMap(map);
		while (iMap.hasNext())
		{
			iMap.next();
			model2->setItem(row, col_map.value(iMap.key()), new QStandardItem(iMap.value().sgr + QString::number(iMap.value().num)));
		}
		row++;
	}
}

void syntax_analysis::_verification()
{
	QString str = ui.str->text();
	if (lr1==NULL)
	{
		QMessageBox::critical(this, "error", "please open grammer file first");
		return;
	}
	if (!lr1->verification(str))
	{
		ui.vertable->setModel(NULL);
		//QMessageBox::critical(this, "error", "verification illegal");
		return;
	}
	QList<QString> process = lr1->get_analysis_process();
	QStandardItemModel *model3 = new QStandardItemModel;
	this->ui.vertable->setModel(model3);
	QStringList slist = process.at(0).split(" ");
	for (size_t i = 0; i < slist.length(); i++)
	{
		model3->setHorizontalHeaderItem(i, new QStandardItem(slist.at(i)));
	}
	for (size_t i = 1; i < process.length(); i++)
	{
		slist = process.at(i).split(" ");
		for (size_t j = 0; j < slist.length(); j++)
		{
			model3->setItem(i-1, j, new QStandardItem(slist.at(j)));
		}
	}
	QMessageBox::information(this, "ok", "verification legitimate.");
}
