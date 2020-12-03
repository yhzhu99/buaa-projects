from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time

chrome_options = Options()
chrome_options.add_argument("--headless")
driver = webdriver.Chrome(
    executable_path='chromedriver', 
    options=chrome_options)
url='https://report.amap.com/diagnosis/index.do'
driver.get(url)
time.sleep(10) # 等待页面加载一段时间
node_list = driver.find_elements_by_xpath("//table/tbody/tr")
for node in node_list: 
    print(node.text)
driver.close()

# 尚未解决：每次只能爬到页面上能显示到的部分信息（16条），而在滑动窗口中尚未显示的部分不能爬取。


























