from matplotlib import pyplot as plt
import tkinter as tk
from tkinter import ttk
import requests
import json
import numpy as np
import tkinter.messagebox
import tkinter.font as tkFont
def center_window(root, width, height):
    screenwidth = root.winfo_screenwidth()
    screenheight = root.winfo_screenheight()
    size = '%dx%d+%d+%d' % (width, height, (screenwidth - width)/2, (screenheight - height)/2)
    #print(size)
    root.geometry(size)
host='http://54.224.98.206:5001'
class Search(tk.Tk):
    def __init__(self):
        super().__init__()
        center_window(self, 570, 425)
        self.title('Search')
        self.setupUI()

    def ok(self):
        url=host+"/getOneByDate"
        data={
            'date':self.comboYear.get()+'-'+self.comboMonth.get()+'-'+self.comboDay.get()
        }
        r=requests.get(url,params=data)
        try:
            r.raise_for_status()
            print(r.text)
            data=json.loads(r.text,encoding='utf-8')
            if 'status' in data:
                self.text.insert('end', "查询不到这一天\n")
                return
            self.text.delete('1.0','end')
            s='{\n'
            s=s+'\t名称:'+data['name']+'\n'
            s=s+'\t开盘价:'+data['open_price']+'\n'
            s=s+'\t最高价:'+data['top_price']+'\n'
            s=s+'\t最低价:'+data['low_price']+'\n'
            s=s+'\t收盘价:'+data['close_price']+'\n'
            s=s+'\t涨跌幅:'+data['rate']+'\n'
            s=s+'\t平均价格:'+data['avg_price']+'\n'
            s=s+'}'
            self.text.insert('end',s+'\n')
        except Exception as e:
            print(e)
            self.text.insert('end',"请检查服务器连接\n")

    def setupUI(self):
        years = []
        for i in range(1999, 2016):
            years.append(str(i))
        self.comboYear = ttk.Combobox(self, values=years)
        self.comboYear.pack()
        months = []
        for i in range(1, 13):
            if i < 10:
                months.append('0' + str(i))
            else:
                months.append(str(i))
        self.comboMonth = ttk.Combobox(self, values=months)
        self.comboMonth.pack()
        days=[]
        for i in range(1,31):
            if i<10:
                days.append('0'+str(i))
            else:
                days.append(str(i))
        self.comboDay=ttk.Combobox(self,values=days)
        self.comboDay.pack()
        self.comboYear.current(0)
        self.comboMonth.current(10)
        self.comboDay.current(10)
        ok_btn=tk.Button(self,text='确定',command=self.ok).pack()
        ft=tkFont.Font(family='楷体',size=30)
        self.text=tk.Text(self,font=ft)
        self.text.pack()
class Show(tk.Tk):
    def __init__(self):
        super().__init__()
        center_window(self, 570, 425)
        self.title('Show')
        self.setupUI()

    def setupUI(self):
        labelTop = tk.Label(self,
                            text="Choose month and month")
        labelTop.grid(column=0, row=0)
        years=[]
        for i in range(1999,2016):
            years.append(str(i))
        self.comboYear = ttk.Combobox(self,values=years)
        self.comboYear.grid(column=0, row=1)
        months=[]
        for i in range(1,13):
            if i<10:
                months.append('0'+str(i))
            else:
                months.append(str(i))
        self.comboMonth=ttk.Combobox(self,values=months)
        self.comboMonth.grid(column=1,row=1)
        self.comboYear.current(0)
        self.comboMonth.current(10)
        btn_ok=tk.Button(self,text='生成折线图',command=self.ok)
        btn_ok.grid(column=2,row=1)
        # comboYear.current(1)
        # print(comboYear.current(), comboYear.get())
    def ok(self):
        year=self.comboYear.get()
        month=self.comboMonth.get()
        url=host+'/getAvgByMonth'
        data={
            'year':year,
            'month':month
        }
        r = requests.get(url, params=data)
        try:
            r.raise_for_status()
            data = json.loads(r.text, encoding='utf-8')
            if len(data)==0:
                tk.messagebox.showwarning('Warning','No content for the month.')
                return
            print(data)
            days=[]
            values=[]
            for item in data:
                days.append(item['date'])
                values.append(float(item['avg']))
            x=np.array(days)
            y=np.array(values)
            plt.figure()
            plt.plot(x,y)
            plt.xlabel('day')
            plt.ylabel('avg price')
            plt.show()
        except:
            tk.messagebox.showerror('error','请检查服务器')
app = tk.Tk()
center_window(app,200,100)
def search():
    search_w=Search()
    search_w.mainloop()

def show():
    show_w=Show()
    show_w.mainloop()
btn_search=tk.Button(app,text="Search",command=search,bg='FloralWhite')
btn_search.pack()
btn_show=tk.Button(app,text="Show",command=show,bg='FloralWhite')
btn_show.pack()
if __name__ == '__main__':
    app.mainloop()