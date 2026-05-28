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
	KafkaBrokers                []string
	KafkaInventoryReservedTopic string
	KafkaPaymentAuthorizedTopic string
	KafkaPaymentDeniedTopic     string
	OutboxBatchSize             int
}

// Load carrega as configs a partir de variáveis de ambiente.
// Em produção, use algo como envconfig, viper ou .env.
func Load() *Config {
	return &Config{
		ServerPort:                  getEnv("SERVER_PORT", ":8083"),
		MySQLDSN:                    getEnv("MYSQL_DSN", "root:root@tcp(localhost:3308)/payments?parseTime=true"),
		KafkaBrokers:                strings.Split(getEnv("KAFKA_BROKERS", "localhost:29092"), ","),
		KafkaInventoryReservedTopic: getEnv("KAFKA_INVENTORY_RESERVED_TOPIC", "inventory.reserved"),
		KafkaPaymentAuthorizedTopic: getEnv("KAFKA_PAYMENT_AUTHORIZED_TOPIC", "payments.authorized"),
		KafkaPaymentDeniedTopic:     getEnv("KAFKA_PAYMENT_DENIED_TOPIC", "payments.denied"),
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
