from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time

chrome_options = Options()
chrome_options.add_argument("--headless")
driver = webdriver.Chrome(
    executable_path='chromedriver',
    options=chrome_options)
for i in range(88): # 总页数为88
    url0='http://www.cdairport.com/flightInfor.aspx'
    url=url0+'?page='+str(i+1)
    driver.get(url)
    time.sleep(1) # 等待页面加载一段时间
    node_list = driver.find_elements_by_xpath("//table/tbody/tr")
    for node in node_list:
        print(node.text)
driver.close()