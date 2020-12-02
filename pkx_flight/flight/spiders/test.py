import json
flight_info={}
data=json.loads(json.dumps(flight_info))

data['arrival']={'type':'到港','detail':[]}
content={'a':1,'b':2}
for i in range(2):
    data['arrival']['detail'].append(content)

data['depart']={'type':'出港','detail':[]}
content={'a':1,'b':2}
for i in range(2):
    data['depart']['detail'].append(content)

print(data)
flights=json.dumps(data,ensure_ascii=False)
print(flights)