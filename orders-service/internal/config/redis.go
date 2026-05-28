package config

import (
	"context"
	"fmt"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

func MustConnectRedis(logger *zap.Logger, url string, db int) (*redis.Client, error) {
	redisClient := redis.NewClient(&redis.Options{
		Addr: url,
		DB:   db,
	})

	if err := redisClient.Ping(context.Background()).Err(); err != nil {
		return nil, fmt.Errorf("redis ping: %w", err)
	}

	logger.Info("connected to redis")
	return redisClient, nil
}
