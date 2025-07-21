# Docker Setup for TiaaDeals Backend

This document provides instructions for running the TiaaDeals Spring Boot backend using Docker and Docker Compose.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- At least 4GB of available RAM
- At least 10GB of available disk space

## Quick Start

### Development Environment

1. **Start the development environment:**

   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

2. **Access the services:**
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Adminer (Database): http://localhost:8081
   - Redis Commander: http://localhost:8082

3. **View logs:**

   ```bash
   # All services
   docker-compose -f docker-compose.dev.yml logs -f

   # Specific service
   docker-compose -f docker-compose.dev.yml logs -f backend
   ```

4. **Stop the development environment:**
   ```bash
   docker-compose -f docker-compose.dev.yml down
   ```

### Production Environment

1. **Set up environment variables:**

   ```bash
   # Create .env file for production
   cp .env.example .env
   # Edit .env with your production values
   ```

2. **Start the production environment:**

   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```

3. **Access the services:**
   - Backend API: http://localhost:8080
   - Nginx Proxy: http://localhost:80

## Docker Files Overview

### Dockerfile

- **Purpose**: Production-ready multi-stage build
- **Features**:
  - Optimized for production
  - Non-root user for security
  - Health checks
  - Minimal runtime image

### Dockerfile.dev

- **Purpose**: Development environment with hot reload
- **Features**:
  - Maven with JDK 17
  - Debug port exposed (5005)
  - Hot reload support
  - Development tools included

### docker-compose.yml

- **Purpose**: Standard environment setup
- **Services**: Backend, PostgreSQL, Redis, Nginx

### docker-compose.dev.yml

- **Purpose**: Development environment
- **Services**: Backend, PostgreSQL, Redis, Adminer, Redis Commander
- **Features**: Volume mounts for hot reload, debug port, development tools

### docker-compose.prod.yml

- **Purpose**: Production environment
- **Services**: Backend, PostgreSQL, Redis, Nginx
- **Features**: Resource limits, production optimizations, SSL support

## Environment Variables

### Required for Production

```bash
# Database
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_super_secure_jwt_secret_key

# Redis (optional)
REDIS_PASSWORD=your_redis_password

# CORS
CORS_ALLOWED_ORIGINS=https://yourdomain.com
```

### Optional

```bash
# JWT Expiration (in milliseconds)
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Server Configuration
SERVER_PORT=8080
```

## Development Workflow

### 1. Initial Setup

```bash
# Clone the repository
git clone <repository-url>
cd tiaadeals-backend

# Start development environment
docker-compose -f docker-compose.dev.yml up -d

# Wait for services to be healthy
docker-compose -f docker-compose.dev.yml ps
```

### 2. Code Changes

- The development setup includes volume mounts, so code changes are reflected immediately
- Use your IDE to connect to the debug port (5005) for debugging
- Check logs for any issues: `docker-compose -f docker-compose.dev.yml logs -f backend`

### 3. Database Management

- Access Adminer at http://localhost:8081
- Server: postgres
- Username: postgres
- Password: password
- Database: tiaadeals

### 4. Redis Management

- Access Redis Commander at http://localhost:8082
- No authentication required in development

## Production Deployment

### 1. Environment Setup

```bash
# Create production environment file
cp .env.example .env.prod

# Edit with production values
nano .env.prod
```

### 2. SSL Certificate Setup (Optional)

```bash
# Create SSL directory
mkdir -p nginx/ssl

# Add your SSL certificates
cp your-cert.pem nginx/ssl/cert.pem
cp your-key.pem nginx/ssl/key.pem

# Uncomment SSL configuration in nginx/nginx.conf
```

### 3. Deploy

```bash
# Build and start production services
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

# Check service status
docker-compose -f docker-compose.prod.yml ps

# Monitor logs
docker-compose -f docker-compose.prod.yml logs -f
```

## Health Checks

### Manual Health Check

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Nginx health
curl http://localhost/health

# Database health
docker-compose exec postgres pg_isready -U postgres -d tiaadeals

# Redis health
docker-compose exec redis redis-cli ping
```

### Automated Health Checks

All services include health checks that run automatically:

- **Backend**: Every 30s (dev) / 60s (prod)
- **PostgreSQL**: Every 10s (dev) / 30s (prod)
- **Redis**: Every 10s (dev) / 30s (prod)

## Troubleshooting

### Common Issues

1. **Port conflicts:**

   ```bash
   # Check what's using the ports
   lsof -i :8080
   lsof -i :5432
   lsof -i :6379
   ```

2. **Database connection issues:**

   ```bash
   # Check database logs
   docker-compose logs postgres

   # Test database connection
   docker-compose exec postgres psql -U postgres -d tiaadeals
   ```

3. **Application startup issues:**

   ```bash
   # Check application logs
   docker-compose logs backend

   # Check if all dependencies are healthy
   docker-compose ps
   ```

4. **Memory issues:**

   ```bash
   # Check container resource usage
   docker stats

   # Increase memory limits in docker-compose.prod.yml if needed
   ```

### Reset Everything

```bash
# Stop all containers
docker-compose -f docker-compose.dev.yml down
docker-compose -f docker-compose.prod.yml down

# Remove volumes (WARNING: This will delete all data)
docker volume rm tiaadeals-backend_postgres_data_dev
docker volume rm tiaadeals-backend_redis_data_dev

# Remove images
docker rmi tiaadeals-backend_backend

# Start fresh
docker-compose -f docker-compose.dev.yml up -d --build
```

## Performance Optimization

### Development

- Use volume mounts for hot reload
- Enable debug mode for detailed logging
- Use Adminer and Redis Commander for debugging

### Production

- Set appropriate JVM memory limits
- Enable gzip compression in nginx
- Use SSL/TLS encryption
- Implement rate limiting
- Monitor resource usage

## Security Considerations

### Development

- Use default passwords (not for production)
- Enable CORS for all origins
- Verbose logging for debugging

### Production

- Use strong, unique passwords
- Restrict CORS origins
- Enable SSL/TLS
- Implement rate limiting
- Use non-root containers
- Regular security updates

## Monitoring and Logging

### Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend

# View logs with timestamps
docker-compose logs -f -t backend
```

### Metrics

- Backend metrics: http://localhost:8080/actuator/metrics
- Health endpoint: http://localhost:8080/actuator/health
- Info endpoint: http://localhost:8080/actuator/info

## Backup and Recovery

### Database Backup

```bash
# Create backup
docker-compose exec postgres pg_dump -U postgres tiaadeals > backup.sql

# Restore backup
docker-compose exec -T postgres psql -U postgres tiaadeals < backup.sql
```

### Volume Backup

```bash
# Backup volumes
docker run --rm -v tiaadeals-backend_postgres_data_dev:/data -v $(pwd):/backup alpine tar czf /backup/postgres_backup.tar.gz -C /data .

# Restore volumes
docker run --rm -v tiaadeals-backend_postgres_data_dev:/data -v $(pwd):/backup alpine tar xzf /backup/postgres_backup.tar.gz -C /data
```

## Support

For issues related to Docker setup:

1. Check the troubleshooting section above
2. Review Docker and Docker Compose logs
3. Verify environment variables and configuration
4. Check system resources (CPU, memory, disk space)
