package middleware

import (
	"fmt"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

func IdempotencyMiddleware(logger *zap.Logger, redisClient *redis.Client) gin.HandlerFunc {
	return func(c *gin.Context) {
		key := c.GetHeader("Idempotency-Key")

		if key == "" {
			c.AbortWithStatusJSON(400, gin.H{"error": "Idempotency-Key header is required"})
			return
		}

		redisKey := fmt.Sprintf("idempotency:%s", key)
		set, err := redisClient.SetNX(c, redisKey, "processing", 24*time.Hour).Result()

		if err != nil {
			logger.Error("error on idempotency middleware", zap.Error(err))
			c.Set("idempotencyKey", key)
			c.Next()
			return
		}

		if !set {
			c.AbortWithStatusJSON(409, gin.H{"error": "duplicate request: this Idempotency-Key was already used"})
			return
		}

		c.Set("idempotencyKey", key)
		c.Next()
	}
}
