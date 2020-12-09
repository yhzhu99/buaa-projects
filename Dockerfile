# gradle 好大
FROM gradle:jdk14
WORKDIR /app
COPY build.gradle gradle settings.gradle c0-java.iml ac1-simulate-anneal.o0 ac2-prime.o0 ac3-pi-and-e.o0 /app/
COPY src /app/src
RUN gradle fatjar --no-daemon
