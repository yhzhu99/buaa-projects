FROM gcc:10
WORKDIR /app/
COPY ./* ./
RUN gcc OPG.c -o program
RUN chmod +x program