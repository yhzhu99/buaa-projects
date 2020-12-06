FROM java:15
WORKDIR /app/
COPY ./* ./
RUN javac -encoding utf-8 App.java

