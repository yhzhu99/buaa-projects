# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html


# useful for handling different item types with a single interface
from itemadapter import ItemAdapter
from scrapy.exceptions import DropItem
from datetime import datetime
import json


class FlightPipeline:
    def __init__(self):
        cur_time = datetime.now().strftime('%Y-%m-%d-%H-%M-%S')
        filename = '北京大兴机场进出港情况'+cur_time+'.json' # 设置写入文件名
        self.file = open(filename, 'w', encoding='utf-8') # 打开文件，准备写入
    # 处理获取得到的item数据
    def process_item(self, item, spider):
        line = json.dumps(dict(item), ensure_ascii=False) # json序列化item list
        self.file.write(line) # 把json写入至文件
        return item

    def open_spider(self, spider):
        pass

    def close_spider(self, spider):
        pass