from flask import Flask,jsonify,request,make_response,json
from flask_cors import CORS
import random,json,requests


def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        pass
    try:
        import unicodedata
        unicodedata.numeric(s)
        return True
    except (TypeError, ValueError):
        pass
    return False

url='http://localhost:5000/'
app = Flask(__name__, static_url_path='')
CORS(app, supports_credentials=True)

@app.route('/getAvgByDate')
def getAvgByDate():
    date=request.args['date']
    data={
        'date':date
    }
    r=requests.get(url+'getByDate',params=data)
    data=json.loads(r.text,encoding='utf-8')
    print(data)
    if 'status' in data:
        return data
    response={
        'avg':data['avg_price']
    }
    return jsonify(response)

@app.route('/getOneByDate')
def geOneByDate():
    date=request.args['date']
    data={
        'date':date
    }
    r=requests.get(url+'getByDate',params=data)
    data=json.loads(r.text,encoding='utf-8')
    print(data)
    if 'status' in data:
        return data
    return jsonify(data)
@app.route('/getAvgByMonth')
def getAvgByMonth():
    year=request.args['year']
    month=request.args['month']
    response=[]
    for i in range(1,31):
        date=year+'-'+month+'-'
        if i<10:
            date+='0'
        date+=str(i)
        data={
            'date':date
        }
        print(data)
        r = requests.get(url + 'getByDate', params=data)
        data = json.loads(r.text, encoding='utf-8')
        if 'status' in data:
            continue
        if data['avg_prive']=='N/A':
            continue
        response.append({
            'date':i,
            'avg': data['avg_price']
        })
    return jsonify(response)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)