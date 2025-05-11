# TiaaDeals Backend

Backend service for the TiaaDeals e-commerce application built with Node.js, Express, and PostgreSQL.

## Prerequisites

- Node.js (v14 or higher)
- PostgreSQL (v12 or higher)
- npm or yarn package manager

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd tiaadeals-backend
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Environment Setup

Create a `.env` file in the root directory with the following variables:

```env
# Server Configuration
PORT=5000

# Database Configuration
DB_USER=your_postgres_username
DB_HOST=localhost
DB_NAME=tiaadeals
DB_PASSWORD=your_postgres_password
DB_PORT=5432

# JWT Configuration
JWT_SECRET=your_secure_jwt_secret_key
```

### 4. Database Setup

1. Create a PostgreSQL database:
```bash
createdb tiaadeals
```

2. Verify database connection:
```bash
node src/utils/testDb.js
```

### 5. Running the Application

Development mode (with auto-reload):
```bash
npm run dev
```

Production mode:
```bash
npm start
```

The server will start on `http://localhost:5000` (or the port specified in your .env file)

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/verify` - Verify JWT token

## Project Structure

```
src/
├── controllers/     # Route controllers
├── routes/         # API routes
├── utils/          # Utility functions
├── middleware/     # Custom middleware
├── db/            # Database related files
└── server.js      # Application entry point
```

## Dependencies

### Production
- express: Web framework
- pg: PostgreSQL client
- bcrypt: Password hashing
- jsonwebtoken: JWT authentication
- cors: Cross-origin resource sharing
- dotenv: Environment variables
- uuid: Unique ID generation
- dayjs: Date manipulation

### Development
- nodemon: Auto-reload during development

## Deployment

### 1. Prepare for Production

1. Set up environment variables on your production server
2. Ensure all dependencies are installed:
```bash
npm install --production
```

### 2. Using PM2 (Recommended)

1. Install PM2 globally:
```bash
npm install -g pm2
```

2. Start the application:
```bash
pm2 start src/server.js --name tiaadeals-backend
```

3. Other useful PM2 commands:
```bash
pm2 list                    # List all processes
pm2 logs tiaadeals-backend  # View logs
pm2 restart tiaadeals-backend # Restart application
```

### 3. Using Systemd (Alternative)

1. Create a systemd service file:
```bash
sudo nano /etc/systemd/system/tiaadeals-backend.service
```

2. Add the following configuration:
```ini
[Unit]
Description=TiaaDeals Backend Service
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/path/to/tiaadeals-backend
ExecStart=/usr/bin/node src/server.js
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

3. Enable and start the service:
```bash
sudo systemctl enable tiaadeals-backend
sudo systemctl start tiaadeals-backend
```

## Security Considerations

1. Always use HTTPS in production
2. Keep your JWT_SECRET secure and complex
3. Regularly update dependencies
4. Use environment variables for sensitive data
5. Implement rate limiting for API endpoints
6. Set up proper CORS configuration

## Monitoring

1. Use PM2 monitoring:
```bash
pm2 monit
```

2. Set up logging:
- Application logs are stored in the `logs` directory
- Use PM2 logs for process monitoring

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

ISC 