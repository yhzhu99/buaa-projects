FROM golang:1.15
WORKDIR /app/
COPY ./* ./
RUN go env -w GO111MODULE=auto
RUN go env -w GOPROXY=https://goproxy.cn,https://goproxy.io,direct
# RUN go mod tidy
RUN go get -v github.com/TualatinX/c0-go
RUN go get -v gorm.io/driver/mysql
RUN go get -v gorm.io/gorm
RUN go build -o program main.go
RUN chmod +x program