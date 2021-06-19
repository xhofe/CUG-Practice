from flask import Flask,jsonify,request,make_response,json
from flask_cors import CORS
from SA2.dao import dao
import random

app = Flask(__name__, static_url_path='')
CORS(app, supports_credentials=True)
dao = dao('database-1.cqszwzeju1ew.us-east-1.rds.amazonaws.com', 3306, 'root', '12345678', 'sa')

@app.route('/getByDate',methods=['GET'])
def searchByDate():
    date=str(request.args["date"])
    print(date)
    reslut=dao.getByOne('date',date)
    # print(reslut)
    if len(reslut)==0:
        return jsonify({
            'status':'N'
        })
    reslut=reslut[0]
    response={
        'name':reslut[1],
        'open_price':reslut[4],
        'top_price':reslut[5],
        'low_price':reslut[6],
        'close_price':reslut[7],
        'rate':reslut[11],
        'avg_price':reslut[12],
    }
    return jsonify(response)

@app.route('/getCount')
def getCount():
    name=request.args['name']
    value=request.args['value']
    result=dao.getCount(name, value)[0]
    response={
        'num':result[0]
    }
    return jsonify(response)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)