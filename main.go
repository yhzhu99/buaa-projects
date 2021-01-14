package main

import (
	"c0/token"
	"fmt"
	"io/ioutil"
	"os"
)

func main() {
	token.Test()
	args := os.Args
	fmt.Println(args[1])
	file, err := os.Open(args[1])
	if err != nil {
		panic(err)
	}
	defer file.Close()
	content, err := ioutil.ReadAll(file)
	fmt.Print(string(content))
}
