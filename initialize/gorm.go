package initialize

import (
	"c0/global"
	"c0/model"

	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

func InitMySQL() {
	dsn := "root:@buaa21@tcp(101.132.227.56:3306)/test?charset=utf8mb4&parseTime=True&loc=Local"
	var err error
	global.DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})
	if err != nil {
		panic(err)
	}
	global.DB.Set("gorm:table_options", "ENGINE=InnoDB").AutoMigrate(
		&model.Data{},
	)
}
