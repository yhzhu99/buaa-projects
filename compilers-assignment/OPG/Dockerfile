FROM gcc:12
WORKDIR /app/
COPY ./* ./
RUN gcc OPG.c -o program
RUN chmod +x program