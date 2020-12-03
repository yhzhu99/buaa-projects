import requests
import time
import re
import datetime
import json
import os

se = requests.session()

post_url = "https://report.amap.com/ajax/roadRank.do?roadType=0&timeType=0&cityCode=810000"
ti = datetime.datetime.now().strftime('%Y-%m-%d')
filename = '[香港道路情况]'+ti+'.json'
data = se.get(post_url).text.replace("'", '"').replace('/ ', '/')
with open(filename,"w", encoding="utf-8") as f:
    f.write(data)
print("fetch data success! ",ti)