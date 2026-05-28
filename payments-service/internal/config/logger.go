package config

import (
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
)

func InitLogger() (*zap.Logger, error) {
	cfg := zap.NewProductionConfig()
	cfg.EncoderConfig.TimeKey = "timestamp"
	cfg.EncoderConfig.EncodeTime = zapcore.TimeEncoderOfLayout("2006-01-02 15:04:05.000")

	logger, err := cfg.Build(zap.AddStacktrace(zap.DPanicLevel))
	if err != nil {
		return nil, err
	}

	return logger, nil
}
