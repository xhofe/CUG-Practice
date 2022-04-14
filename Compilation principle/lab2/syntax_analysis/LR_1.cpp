#include "LR_1.h"



LR_1::LR_1()
{
}

LR_1::LR_1(QString filename) {
	m_filename = filename;
}

LR_1::~LR_1()
{
}

bool LR_1::read_grammer()
{
	QFile file(m_filename);
	if (!file.open(QIODevice::ReadOnly))
	{
		qDebug() << "打开失败";
		QMessageBox::critical(NULL, "failed open file", "please choose a file about grammer");
		return false;
	}
	QTextStream input_stream(&file);
	QString lineStr;
	while (!input_stream.atEnd())
	{
		lineStr = input_stream.readLine();
		if (lineStr.left(2) == "//")//注释
		{
			continue;
		}
		else if (lineStr.left(1) == "N")//非终结符
		{
			QString str = lineStr.mid(3, lineStr.length() - 4);
			QStringList strs = str.split(" ");
			for (size_t i = 0; i < strs.size(); i++)
			{
				m_nonterminal.append(strs.at(i));
			}
		}
		else if (lineStr.left(1) == "E")//终结符
		{
			QString str = lineStr.mid(3, lineStr.length() - 4);
			QStringList strs = str.split(" ");
			for (size_t i = 0; i < strs.size(); i++)
			{
				QString tmp = strs.at(i);
				m_terminator.append(tmp);
			}
		}
		else if (lineStr.left(1) == "G")//文法
		{
			GrammerItem first = GrammerItem("S'", lineStr.at(2));
			m_grammerList.append(first);
			int num = lineStr.right(lineStr.length() - 5).toInt();
			QString tmp;
			for (size_t i = 0; i < num; i++)
			{
				tmp = input_stream.readLine();
				QStringList strs = tmp.split("->");
				GrammerItem item = GrammerItem(strs.at(0), strs.at(1));
				m_grammerList.append(item);
			}
		}
		else
		{
			qDebug() << "read error character";
			return false;
		}
	}
	return true;
}

bool LR_1::get_first()
{
	for each (QString nonterminal in m_nonterminal)
	{
		QSet<QString> first = get_first(nonterminal);
		m_firstSet.insert(nonterminal, first);
	}
	return true;
}

QSet<QString> LR_1::get_first(QString nonterminal)
{
	if (m_firstSet.contains(nonterminal))
	{
		return m_firstSet.value(nonterminal);
	}
	QSet<QString> first;
	QList<GrammerItem> items = get_grammer(nonterminal);
	for each (GrammerItem item in items)
	{
		//忽略epsilon
		int i = 0;
		QString right = item.right;
		while (i < right.length()&&right.at(i)==epsilon)
		{
			i++;
		}
		if (i==right.length()-1)
		{
			first.insert(epsilon);
			continue;
		}
		if (m_terminator.contains(right.at(i)))
		{
			first.insert(right.at(i));
			continue;
		}
		if (m_nonterminal.contains(right.at(i)))
		{
			first.intersects(get_first(right.at(i)));
			continue;
		}
	}
	return QSet<QString>();
}

bool LR_1::generate_item_set()
{
	QList<Item> itemStack;//项目集状态栈
	QList<GrammerItem> grammerStack;//项目集中的文法
	//初始化第一个未处理状态
	Item first;
	first.grammers.append(m_grammerList.at(0));
	first.grammers[0].looking_character.append("$");
	itemStack.append(first);
	Item item;
	int itemIndex = 0;
	while (itemIndex<itemStack.size())
	{
		qDebug() << itemIndex << itemStack.size();
		item = itemStack.at(itemIndex);
		grammerStack.clear();
		for each (GrammerItem var in item.grammers)
		{
			grammerStack.append(var);
		}
		int grammerIndex = 0;
		while (grammerIndex<grammerStack.size())//当前项目集未处理完
		{
			GrammerItem grammer_item = grammerStack.at(grammerIndex);
			//当前节点所在位置没有下一个字符了
			if (grammer_item.right.length()==grammer_item.flag)
			{
				grammerIndex++;
				continue;
			}
			//下一个为非终结符
			if (m_nonterminal.contains(grammer_item.right.at(grammer_item.flag)))
			{
				for (size_t i = 0; i < m_grammerList.length(); i++)
				{
					GrammerItem _grammeritem = m_grammerList.at(i);
					_grammeritem.flag = 0;//等价状态初始化
					if (_grammeritem.left==grammer_item.right.at(grammer_item.flag))
					{
						//求展望字符
						if (grammer_item.right.length()==grammer_item.flag+1)//||m_terminator.contains(_grammeritem.right.at(0))
						{
							_grammeritem.looking_character = grammer_item.looking_character;
						}
						else//当前非终结符后面是终结符
						{
							if (m_terminator.contains(grammer_item.right.at(grammer_item.flag+1)))
							{
								_grammeritem.looking_character.append(grammer_item.right.at(grammer_item.flag + 1));
							}
							else
							{
								//非终结符 TODO
								QSet<QString> first = m_firstSet.value(grammer_item.right.at(grammer_item.flag + 1));
								_grammeritem.looking_character.append(first.toList());
								if (first.contains(epsilon))
								{
									_grammeritem.looking_character.append(grammer_item.looking_character.at(0));
								}
							}
						}
						bool flush = false;
						bool hasappend = false;
						int sp = 0;
						for (size_t j = 0; j < grammerStack.size(); j++)
						{
							sp = j;
							if (grammerStack.at(j).left==_grammeritem.left&&grammerStack.at(j).right==_grammeritem.right&&grammerStack.at(j).flag==_grammeritem.flag)
							{
								hasappend = true;
								bool isexist = false;
								for (size_t k = 0; k < _grammeritem.looking_character.length(); k++)
								{
									isexist = false;
									for (size_t l = 0; l < grammerStack.at(j).looking_character.length(); l++)
									{
										if (_grammeritem.looking_character.at(k)==grammerStack.at(j).looking_character.at(l))
										{
											isexist = true;
										}
									}
									if (!isexist)
									{
										grammerStack[j].looking_character.append(_grammeritem.looking_character.at(k));
										flush = true;
									}
								}
								break;
							}
						}
						if (flush)
						{
							if (sp<grammerIndex)
							{
								grammerStack.append(grammerStack.at(sp));
								grammerStack.removeAt(sp);
								grammerIndex--;
							}
							else if (sp == grammerIndex) {
								grammerIndex--;
							}
						}
						if (!hasappend)
						{
							grammerStack.append(_grammeritem);
						}
					}
				}
			}
			grammerIndex++;
		}
		int res = hasItem(itemStack, grammerStack, itemIndex);
		if (res>0)
		{
			for (size_t i = 0; i < itemIndex; i++)
			{
				item = itemStack.at(i);
				QMapIterator<QString,int> iMap(item.go);
				while (iMap.hasNext())
				{
					iMap.next();
					if (iMap.value()==itemIndex)
					{
						itemStack[i].go[iMap.key()] = res;
					}
					else if (iMap.value()>itemIndex)
					{
						itemStack[i].go[iMap.key()] = itemStack[i].go[iMap.key()]- 1;
					}
				}
			}
			itemStack.removeAt(itemIndex);
			itemIndex--;
		}
		else
		{
			itemStack[itemIndex].grammers = grammerStack;
			for (size_t i = 0; i < grammerStack.size(); i++)
			{
				GrammerItem gram = grammerStack.at(i);
				if (gram.flag<gram.right.length())
				{
					QString s = gram.right.at(gram.flag);
					gram.flag++;
					if (itemStack.at(itemIndex).go.contains(s))
					{
						Item gogram = itemStack.at(itemStack.at(itemIndex).go.value(s));
						gogram.grammers.append(gram);
						itemStack[itemStack.at(itemIndex).go.value(s)] = gogram;
					}
					else
					{
						item.go.clear();
						item.grammers.clear();
						item.grammers.append(gram);
						int x = itemStack.size();
						itemStack.append(item);
						itemStack[itemIndex].go.insert(s, x);
					}
				}
			}
		}
		itemIndex++;
	}
	m_itemList = itemStack;
	return true;
}

int LR_1::hasItem(QList<Item> itemStack, QList<GrammerItem> grammeritemList, int num)
{
	for (size_t i = 0; i < num; i++)
	{
		QList<GrammerItem> nowgram = itemStack.at(i).grammers;
		bool isequal = true;
		if (grammeritemList.size()==nowgram.size())
		{
			for each (GrammerItem a in grammeritemList)
			{
				bool has = false;
				for each (GrammerItem b in nowgram)
				{
					if (a==b)
					{
						has = true;
						break;
					}
				}
				if (has==false)
				{
					isequal = false;
					break;
				}
			}
			if (isequal)
			{
				return i;
			}
		}
	}
	return 0;
}

bool LR_1::createTable()
{
	QMap<QString, TableItem> row;
	for (size_t i = 0; i < m_itemList.size(); i++)
	{
		row.clear();
		Item item = m_itemList.at(i);
		TableItem table_item = TableItem("", 0);
		if (item.go.size()==0)
		{
			for (size_t j = 0; j < m_grammerList.size(); j++)
			{
				if (m_grammerList.at(j).left==item.grammers[0].left&&m_grammerList.at(j).right==item.grammers[0].right)
				{
					table_item.sgr = "r";
					table_item.num = j;
					for each (QString lc in item.grammers[0].looking_character)
					{
						row.insert(lc, table_item);
					}
				}
			}
		}
		else
		{
			QMapIterator<QString,int> iMap(item.go);
			while (iMap.hasNext())
			{
				iMap.next();
				if (m_nonterminal.contains(iMap.key()))
				{
					table_item.sgr = "g";
				}
				else
				{
					table_item.sgr = "s";
				}
				table_item.num = iMap.value();
				row.insert(iMap.key(), table_item);
			}
		}
		m_table.append(row);
	}
	return true;
}

bool LR_1::verification(QString str)
{
	m_analysis_process.clear();
	m_analysis_process.push_back("status_stack symbol_stack action character");
	QStack<int> stateStack;
	QStack<QString> charStack;
	stateStack.push(0);
	charStack.push("$");
	str.append("$");
	int index=0;
	int top;
	while (true)
	{
		QString s = str.at(index);
		top = stateStack.top();
		if (!m_table.at(top).contains(s))
		{
			//qDebug() << "illegal:" << s;
			QMessageBox::information(NULL, "illegal", "may illegal:" + s);
			return false;
		}
		TableItem item = m_table.at(top).value(s);

		if (item.sgr=="s")
		{
			charStack.push(s);
			stateStack.push(item.num);
			index++;
		}
		else if (item.sgr=="r")
		{
			if (item.num==0)
			{
				if (index==str.length()-1)
				{
					//qDebug() << "legitimate";
					return true;
				}
				else
				{
					//qDebug() << "illegal:" << s;
					QMessageBox::information(NULL, "illegal", "may illegal:" + s);
					return false;
				}
			}
			int len = m_grammerList.at(item.num).right.length();
			for (size_t i = 0; i < len; i++)
			{
				charStack.pop();
				stateStack.pop();
			}
			charStack.push(m_grammerList.at(item.num).left);
			top = stateStack.top();
			item = m_table.at(top).value(m_grammerList.at(item.num).left);
			stateStack.push(item.num);
		}
		m_analysis_process.push_back(stackToStr(stateStack) + " " + stackToStr(charStack) + " " + item.sgr + " " + str.at(index));
	}
	//qDebug() << "illegal:end";
	QMessageBox::information(NULL, "illegal", "may illegal:string is too long");
	return false;
}

void LR_1::printInfo()
{
	int i = 0;
	for each (Item item in m_itemList)
	{
		qDebug() << i;
		item.print();
		i++;
	}
	for each (QString str in m_analysis_process)
	{
		qDebug() << str;
	}
}

QList<GrammerItem> LR_1::get_grammer(QString left)
{
	QList<GrammerItem> res;
	for each (GrammerItem var in m_grammerList)
	{
		if (var.left==left)
		{
			res.push_back(var);
		}
	}
	return res;
}

QString LR_1::stackToStr(QStack<QString> stack)
{
	QString str;
	for each (QString s in stack)
	{
		str.append(s);
	}
	return str;
}

QString LR_1::stackToStr(QStack<int> stack)
{
	QString str;
	for each (int s in stack)
	{
		str.append(QString::number(s));
	}
	return str;
}

bool LR_1::work()
{
	if (!read_grammer())
	{
		return false;
	}
	if (!get_first())
	{
		return false;
	}
	if (!generate_item_set())
	{
		return false;
	}
	if (!createTable())
	{
		return false;
	}
	return true;
}
