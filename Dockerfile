FROM golang:1.15
WORKDIR /app/
COPY ./* ./
# RUN go env -w GO111MODULE=auto
# RUN go env -w GOPROXY=https://goproxy.cn,https://goproxy.io,direct
RUN go build -o program main.go
RUN chmod +x program