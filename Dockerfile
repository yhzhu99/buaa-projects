FROM golang:1.15
WORKDIR /usr/local/go/src/c0-go/
COPY main.go go.mod go.sum ./
COPY global ./global
COPY initialize ./initialize
COPY model ./model
RUN go env -w GO111MODULE=auto
RUN go env -w GOPROXY=https://goproxy.cn,https://goproxy.io,direct
RUN go build -o program main.go
RUN chmod +x program