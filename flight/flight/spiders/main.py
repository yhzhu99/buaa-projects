import os
import time

if __name__ == '__main__':
    while True:
        os.system("scrapy crawl pkx")
        time.sleep(7200) # 每２小时执行一次(60*60*2)