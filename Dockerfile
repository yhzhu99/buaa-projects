FROM g++:10
WORKDIR /app/
COPY ./* ./
RUN g++ LexicalAnalysis.cpp -o program
RUN chmod +x program