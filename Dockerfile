FROM golang:1.15
WORKDIR /app/
COPY ./* ./
# RUN go env -w GO111MODULE=off
RUN go env -w GOPROXY=https://goproxy.cn,https://goproxy.io,direct
RUN go mod tidy
RUN go build main.go
RUN chmod +x main