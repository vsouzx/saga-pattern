package middleware

import (
	"errors"
	"net/http"
	"orders-service/internal/domain"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

func ErrorHandler(logger *zap.Logger) gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()

		if len(c.Errors) == 0 {
			return
		}

		err := c.Errors.Last().Err

		var appErr *domain.AppError
		if errors.As(err, &appErr) {
			logger.Warn(appErr.Message, zap.Int("status", appErr.Code), zap.Error(appErr.Err))
			c.JSON(appErr.Code, gin.H{
				"error": appErr.Message,
			})
		} else {
			// Erro desconhecido — trata como 500
			logger.Error("unexpected error", zap.Error(err))
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": "internal server error",
			})
		}
	}
}
