# scrapy-demo

> PKX Airport Flight Info (Computer Networks Course Assignment)

## 安装环境

- 安装Scrapy

```bash
pip install Scrapy
```

## 初始化项目

```bash
scrapy startproject flight
cd flight # Path: pkx_flight/pkx_flight
scrapy genspider pkx "http://data.carnoc.com/corp/airport/pkx__airportflight.html"
```

