package config

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

type Config struct {
	ServerPort                  string
	MySQLDSN                    string
	RedisAddr                   string
	RedisPass                   string
	RedisDB                     int
	KafkaBrokers                []string
	KafkaOrdersCreatedTopic     string
	KafkaOrdersConfirmedTopic   string
	KafkaInventoryTopic         string
	KafkaInventoryReleasedTopic string
	KafkaPaymentsTopic          string
	OutboxBatchSize             int
}

// Load carrega as configs a partir de variáveis de ambiente.
// Em produção, use algo como envconfig, viper ou .env.
func Load() *Config {
	return &Config{
		ServerPort:                  getEnv("SERVER_PORT", ":8081"),
		MySQLDSN:                    getEnv("MYSQL_DSN", "root:root@tcp(localhost:3307)/orders?parseTime=true"),
		RedisAddr:                   getEnv("REDIS_ADDR", "localhost:6379"),
		RedisPass:                   getEnv("REDIS_PASS", ""),
		RedisDB:                     0,
		KafkaBrokers:                strings.Split(getEnv("KAFKA_BROKERS", "localhost:29092"), ","),
		KafkaOrdersCreatedTopic:     getEnv("KAFKA_ORDERS_CREATED_TOPIC", "orders.created"),
		KafkaOrdersConfirmedTopic:   getEnv("KAFKA_ORDERS_CONFIRMED_TOPIC", "orders.confirmed"),
		KafkaInventoryTopic:         getEnv("KAFKA_INVENTORY_TOPIC", "inventory.insufficient-stock"),
		KafkaInventoryReleasedTopic: getEnv("KAFKA_INVENTORY_RELEASED_TOPIC", "inventory.released"),
		KafkaPaymentsTopic:          getEnv("KAFKA_PAYMENTS_TOPIC", "payments.authorized"),
		OutboxBatchSize:             getEnvToInt("OUTBOX_BATCH_SIZE", 10),
	}
}

func getEnv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

func getEnvToInt(key string, fallback int) int {
	if v := os.Getenv(key); v != "" {
		vint, err := strconv.Atoi(v)
		if err != nil {
			panic(fmt.Sprintf("invalid ENV VAR %s: %v", key, err))
		}
		return vint
	}
	return fallback
}
