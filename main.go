package main

import (
	"c0-go/lexer"
	"fmt"
	"os"
)

func main() {
	args := os.Args
	lex, err := lexer.NewLexerFile(args[1])
	if err != nil {
		panic(err)
	}
	fmt.Println(lex.Scan().IDValue())
	panic("nb")
}
