import json
import codecs
from models.calculator import handle


def test_generate(filename: str):
    with codecs.open(filename, 'r', 'utf-8') as fr:
        data = json.load(fr)
        for value in data.values():
            _, msg, _ = handle(value['args'])
            # print(msg)
            value['result'] = msg
        with codecs.open(filename[:-5] + '_test.json', 'w', 'utf-8') as fw:
            json.dump(data, fw, ensure_ascii=False, indent=2)


def generate_test(filename: str):
    with codecs.open(filename, 'r', 'utf-8') as fr:
        data = json.load(fr)
        data_test = {}
        id = 0
        for class_ in data['class_']:
            if class_ != "EconomyClass" and class_ != "FirstClass" and class_ != "BusinessClass":
                args = {"class_": class_, "start": data['start'][0], "area": data['area'][1], "fare": data['fare'][0],
                        "passenger": data['passenger'][0], "baggages": data['baggages'][0]}
                data_test['test' + str(id)] = {"args": args}
                _, msg, _ = handle(args)
                data_test['test' + str(id)]['result'] = msg
                id += 1
                continue
            for area in data['area']:
                if area == 'domestic':
                    for start in data['start']:
                        for fare in data['fare']:
                            for passenger in data['passenger']:
                                for baggages in data['baggages']:
                                    args = {"class_": class_, "start": start, "area": area, "fare": fare,
                                            "passenger": passenger, "baggages": baggages}
                                    data_test['test' + str(id)] = {"args": args}
                                    _, msg, _ = handle(args)
                                    data_test['test' + str(id)]['result'] = msg
                                    id += 1
                else:
                    start = 'cny'
                for fare in data['fare']:
                    for passenger in data['passenger']:
                        for baggages in data['baggages']:
                            args = {"class_": class_, "start": start, "area": area, "fare": fare,
                                    "passenger": passenger, "baggages": baggages}
                            data_test['test' + str(id)] = {"args": args}
                            _, msg, _ = handle(args)
                            data_test['test' + str(id)]['result'] = msg
                            id += 1
        with codecs.open(filename[:-5] + '_test.json', 'w', 'utf-8') as fw:
            json.dump(data_test, fw, ensure_ascii=False, indent=2)


def test_generate2(filename: str):
    with codecs.open(filename, 'r', 'utf-8') as fr:
        data = json.load(fr)
        data_test = {}
        id = 70
        for baggages in data['baggages']:
            args = {"class_": data['class_'], "start": data['start'], "area": data['area'], "fare": data['fare'],
                    "passenger": data['passenger'], "baggages": baggages}
            data_test['test' + str(id)] = {"args": args}
            _, msg, _ = handle(args)
            data_test['test' + str(id)]['result'] = msg
            id += 1
        with codecs.open(filename[:-5] + '_test.json', 'w', 'utf-8') as fw:
            json.dump(data_test, fw, ensure_ascii=False, indent=2)


if __name__ == '__main__':
    test_generate('./test_files/test_whitebox.json')
    # test_generate('./test_files/source_1.json')
    # generate_test('./test_files/all_choose.json')
    # test_generate2('./test_files/baggages.json')
