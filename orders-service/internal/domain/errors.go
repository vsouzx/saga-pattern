package domain

import "fmt"

type AppError struct {
	Code    int
	Message string
	Err     error
}

func (e *AppError) Error() string {
	if e.Err != nil {
		return fmt.Sprintf("%s: %v", e.Message, e.Err)
	}
	return e.Message
}

func (e *AppError) Unwrap() error {
	return e.Err
}

func NewBadRequestError(message string) *AppError {
	return &AppError{
		Code:    400,
		Message: message,
	}
}

func NewConflictError(message string) *AppError {
	return &AppError{
		Code:    409,
		Message: message,
	}
}

func NewNotFoundError(message string) *AppError {
	return &AppError{
		Code:    404,
		Message: message,
	}
}
