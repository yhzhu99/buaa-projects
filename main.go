package main

import (
	"fmt"
	"io/ioutil"
	"os"

	"c0-go/global"
	"c0-go/initialize"
	"c0-go/model"
)

func main() {
	initialize.InitMySQL()
	args := os.Args
	fmt.Println(args[1], args[2])
	file, err := os.Open(args[1])
	if err != nil {
		panic(err)
	}
	defer file.Close()
	content, err := ioutil.ReadAll(file)
	data := model.Data{Name: args[1], Detail: string(content)}
	global.DB.Create(&data)
	// fmt.Print(string(content))
}
