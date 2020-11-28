# -*- coding: utf-8 -*-
import scrapy
from Teacher.items import TeacherItem

class TestSpider(scrapy.Spider):
    name = 'test'
    allowed_domains = ['www.itcast.cn']
    start_urls = ['http://www.itcast.cn/channel/teacher.shtml']

    def parse(self, response):
        node_list = response.xpath("//div[@class='li_txt']")


        items=[]
        for node in node_list:

            item = TeacherItem()

            name = node.xpath("./h3/text()").extract()
            title = node.xpath("./h4/text()").extract()
            info = node.xpath("./p/text()").extract()
            
            item['name'] = name[0]
            item['title'] = title[0]
            item['info'] = info[0]
            items.append(item)
            
        return items
