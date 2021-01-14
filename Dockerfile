FROM golang:1.15
WORKDIR /app/
COPY ./* ./
COPY ./* /usr/local/go/src/c0
# RUN go env -w GO111MODULE=off
RUN go env -w GOPROXY=https://goproxy.cn,https://goproxy.io,direct
RUN go mod tidy
RUN go build main.go
RUN chmod +x main