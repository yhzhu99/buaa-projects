# gradle 好大
FROM gradle:jdk14
WORKDIR /app
COPY build.gradle gradle settings.gradle miniplc0-java.iml /app/
COPY src /app/src
RUN gradle fatjar --no-daemon
