from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time
from datetime import datetime
import json

chrome_options = Options()
chrome_options.add_argument("--headless")
driver = webdriver.Chrome(
    executable_path='chromedriver',
    options=chrome_options)
url='https://report.amap.com/detail.do?city=810000'
driver.get(url)
time.sleep(3) # 等待页面加载一段时间
node_list = driver.find_elements_by_xpath("//table[@id='first_table']/tbody/tr")
res=[]
for node in node_list:
    li=node.text.split()
    content={
        '排名':li[0],
        '区域':li[1],
        '拥堵指数':li[2],
        '旅行速度':li[3]
    }
    res.append(content)
ti = datetime.now().strftime('%Y-%m-%d-%H-%M-%S')
filename = '[香港区域拥堵情况]'+ti
with open(filename+'.json',"w", encoding="utf-8") as f:
    f.write(json.dumps(res,indent=2, ensure_ascii=False))

driver.close()
