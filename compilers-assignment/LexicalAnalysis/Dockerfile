FROM gcc:12
WORKDIR /app/
COPY ./* ./
RUN gcc LexicalAnalysis.c -o program
RUN chmod +x program