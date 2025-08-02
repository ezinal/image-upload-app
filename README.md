# Gardrops Image Processing Task

A microservices-based image processing application built with Spring Boot that provides image upload, processing, and management capabilities.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Redis (for session storage)

## Setup and Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd gardrops-image-task
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Start Redis
```bash
# Install Redis if not already installed
# Ubuntu/Debian
sudo apt-get install redis-server

# macOS
brew install redis

# Start Redis
redis-server
```

### 4. Start the Services

#### Start Image Processing Service
```bash
cd image-processing-service
mvn spring-boot:run
```

#### Start Image Upload Service
```bash
cd image-upload-service
mvn spring-boot:run
```

Both services will start on their respective ports (8080 and 8081).

## Technical Implementation Details

### Notable Implementation Points

1. **Session-based File Organization**: Images are saved in session-specific folders for better organization and isolation
2. **Parent POM Structure**: Root `mvn clean install` builds all services
3. **Request Filters**: Used for origin filtering and rate limiting, keeping concerns separated
4. **Separation of Concerns**: Image deletion moved to processing service for better file management
5. **Interface-based Design**: ImageResizer interface allows for easy library swapping

### Improvement Opportunities

1. **Image Processing Library**: Current Java 2D implementation can be replaced with more efficient libraries (e.g., ImageMagick, OpenCV)
2. **Security Enhancement**: Spring Security integration for more robust CORS and authentication
3. **Cloud Storage**: AWS S3 or similar for scalable file storage
4. **Monitoring**: Add metrics and health checks
5. **Containerization**: Docker support for easier deployment

## Troubleshooting

### Common Issues

1. **Redis Connection Error**: Ensure Redis is running on localhost:6379
2. **Port Conflicts**: Check if ports 8080/8081 are available
3. **File Permissions**: Ensure write permissions for `/tmp/uploads` directory
