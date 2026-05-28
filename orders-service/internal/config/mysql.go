package config

import (
	"database/sql"
	"fmt"
	"time"

	_ "github.com/go-sql-driver/mysql"
	"go.uber.org/zap"
)

func MustConnectMySQL(logger *zap.Logger, dsn string) (*sql.DB, error) {
	db, err := sql.Open("mysql", dsn)
	if err != nil {
		return nil, fmt.Errorf("opening mysql: %w", err)
	}

	db.SetMaxOpenConns(25)
	db.SetMaxIdleConns(5)
	db.SetConnMaxLifetime(30 * time.Second)
	db.SetConnMaxIdleTime(30 * time.Second)

	if err := db.Ping(); err != nil {
		return nil, fmt.Errorf("pinging mysql: %w", err)
	}

	logger.Info("connected to mysql")
	return db, nil
}
