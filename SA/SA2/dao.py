# -*- coding: utf-8 -*-

import pymysql


class dao(object):
    def __init__(self, host, port, user, passwd, db, charset='utf8'):
        self.conn = pymysql.connect(
            host=host,
            port=port,
            user=user,
            passwd=passwd,
            db=db,
            charset=charset
        )

    def get_cursor(self):
        return self.conn.cursor()

    def execute(self, sql):
        self.conn.ping(reconnect=True)
        cursor = self.get_cursor()
        try:
            cursor.execute(sql, None)
            self.conn.commit()
            result = cursor.fetchall()
        finally:
            cursor.close()
        return result
    def getByOne(self,name,value):
        sql="select * from sa where "+name+'=\''+value+'\''
        # print(sql)
        res=self.execute(sql)
        return res

    def getCount(self,name,value):
        sql="select count(*) num from sa where "+name+' like "%'+value+'%"'
        print(sql)
        return self.execute(sql)
    def close(self):
        try:
            self.conn.close()
        except:
            pass

    def __del__(self):
        self.close()

if __name__ == '__main__':
    dao = dao('database-1.cqszwzeju1ew.us-east-1.rds.amazonaws.com', 3306, 'root', '12345678', 'sa')
    print(dao.execute("SELECT * FROM sa LIMIT 10"))
