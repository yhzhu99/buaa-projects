# Define here the models for your scraped items
#
# See documentation in:
# https://docs.scrapy.org/en/latest/topics/items.html

import scrapy
class FlightItem(scrapy.Item):
    flight_info = scrapy.Field() # 所有的航班信息最后都会以json的形式封装在flight_info中