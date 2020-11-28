FROM gcc:10
WORKDIR /app/
COPY ./* ./
RUN gcc c0.c -o program
RUN chmod +x program