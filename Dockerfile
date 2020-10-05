FROM gcc:10
WORKDIR /app/
COPY ./* ./
RUN gcc LexicalAnalysis.c -o program
RUN chmod +x program