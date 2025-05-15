FROM golang:1.24-alpine AS builder

WORKDIR /app

# Copy go.mod and go.sum files
COPY go.mod go.sum ./

# Download dependencies
RUN go mod download

# Copy the source code
COPY . .

# Build the application
RUN CGO_ENABLED=0 GOOS=linux go build -o xander-drm ./api

# Use a minimal alpine image for the final image
FROM alpine:latest

# Install ca-certificates for HTTPS
RUN apk --no-cache add ca-certificates

WORKDIR /app

# Create directory for configuration
RUN mkdir -p /var/lib/xander-drm

# Copy the binary from the builder stage
COPY --from=builder /app/xander-drm .

# Expose the default port
EXPOSE 8080

# Set environment variables with defaults
ENV XDRM_PATH=/var/lib/xander-drm
ENV XDRM_BIND=:8080

# Run the application
CMD ["./xander-drm"]