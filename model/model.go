package model

type Data struct {
	ID     uint64 `gorm:"primary_key;"`
	Name   string `gorm:"type:varchar(255)"`
	Detail string `gorm:"type:varchar(5000)"`
}
