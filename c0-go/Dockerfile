FROM golang:1.19
WORKDIR /usr/local/go/src/c0-go/
COPY main.go go.mod go.sum ./
COPY lexer ./lexer
COPY token ./token
COPY util ./util
RUN go env -w GO111MODULE=auto
RUN go env -w GOPROXY=https://goproxy.cn,https://goproxy.io,direct
RUN go build -o program main.go
RUN chmod +x program