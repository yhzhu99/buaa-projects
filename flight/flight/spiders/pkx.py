import scrapy
from flight.items import FlightItem
import json


class PkxSpider(scrapy.Spider):
    name = 'pkx'
    allowed_domains = ['data.carnoc.com']
    start_urls = ['http://data.carnoc.com/corp/airport/pkx__airportflight.html']

    def parse(self, response):
        
        flight_info = {}
        data = json.loads(json.dumps(flight_info))
        data['arrival']={'type':'进港','detail':[]}

        node_list = response.xpath("//div[@id='icefable1']/li")
        for node in node_list:
            flight_number = node.xpath("./span[1]/text()").extract()
            from_city = node.xpath("./span[2]/text()").extract()
            pick_up_building = node.xpath("./span[3]/text()").extract()
            expect_time = node.xpath("./span[4]/text()").extract()
            actual_time = node.xpath("./span[5]/text()").extract()
            state = node.xpath("./span[6]/text()").extract()

            content={
                'flight_number' : flight_number[0],
                'from_city' : from_city[0],
                'pick_up_building' : pick_up_building[0],
                'expect_time' : expect_time[0],
                'actual_time' : actual_time[0],
                'state' : state[0]
            }
            data['arrival']['detail'].append(content)

            
        data['depart']={'type':'出港','detail':[]}
    
        node_list = response.xpath("//div[@id='icefable2']/li")
        for node in node_list:

            flight_number = node.xpath("./span[1]/text()").extract()
            to_city = node.xpath("./span[2]/text()").extract()
            check_in_building = node.xpath("./span[3]/text()").extract()
            expect_time = node.xpath("./span[4]/text()").extract()
            actual_time = node.xpath("./span[5]/text()").extract()
            state = node.xpath("./span[6]/text()").extract()

            content={
                'flight_number' : flight_number[0],
                'from_city' : from_city[0],
                'check_in_building' : pick_up_building[0],
                'expect_time' : expect_time[0],
                'actual_time' : actual_time[0],
                'state' : state[0]
            }
            data['depart']['detail'].append(content)
        
        item = FlightItem()
        item['flight_info'] = data
        yield(item)