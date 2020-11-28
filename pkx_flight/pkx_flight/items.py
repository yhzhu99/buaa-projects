# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy


class PkxFlightItem(scrapy.Item):
    # define the fields for your item here like:

    #分隔符
    seperate = scrapy.Field()
    #类型
    type = scrapy.Field()
    #排名
    rank = scrapy.Field()
    #区域
    area = scrapy.Field()
    #拥堵指数
    point = scrapy.Field()
    #旅行速度
    speed = scrapy.Field()
    #航班号
    number = scrapy.Field()
    #始发地、目的地
    city = scrapy.Field()
    #停机楼
    terminal = scrapy.Field()
    #预计
    expected = scrapy.Field()
    #实际
    actual = scrapy.Field()
    #状态
    state = scrapy.Field()
    #现在时间
    nowtime = scrapy.Field()