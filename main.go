package main

import (
	"c0-go/lexer"
	"fmt"
)

func main() {
	lex, err := lexer.NewLexerFile("test")
	if err != nil {
		panic(err)
	}
	fmt.Println(lex.Scan().IDValue())
}
