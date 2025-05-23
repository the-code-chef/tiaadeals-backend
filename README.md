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

1. Copy the example environment file:

```bash
cp .env.example .env
```

2. Update the `.env` file with your actual configuration values:

```env
# Server Configuration
PORT=3000
NODE_ENV=development

# Database Configuration
DB_USER=your_actual_db_username
DB_HOST=localhost
DB_NAME=your_actual_db_name
DB_PASSWORD=your_actual_db_password
DB_PORT=5432

# JWT Configuration
JWT_SECRET=your_actual_jwt_secret

# Frontend URL
FRONTEND_URL=http://localhost:3000
```

Note: Never commit the `.env` file to version control. The `.env.example` file serves as a template for required environment variables.

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

The server will start on `http://localhost:3000` (or the port specified in your .env file)

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

1. Create a production environment file:

```bash
cp .env.example .env.production
```

2. Update `.env.production` with production values:

```env
NODE_ENV=production
PORT=3000
FRONTEND_URL=https://tiaadeals.com
DB_HOST=your_production_db_host
DB_USER=your_production_db_user
DB_PASSWORD=your_production_db_password
DB_NAME=your_production_db_name
JWT_SECRET=your_production_jwt_secret
```

### 2. Deploy to Production

1. Set up your production server (e.g., EC2)
2. Clone the repository
3. Install dependencies: `npm install`
4. Set up the environment file
5. Start the application: `npm start`

## Environment Variables

The following environment variables are required:

| Variable     | Description              | Example                |
| ------------ | ------------------------ | ---------------------- |
| PORT         | Server port              | 3000                   |
| NODE_ENV     | Environment              | development/production |
| DB_USER      | Database username        | tiaadeals_user         |
| DB_HOST      | Database host            | localhost              |
| DB_NAME      | Database name            | tiaadeals              |
| DB_PASSWORD  | Database password        | your_password          |
| DB_PORT      | Database port            | 5432                   |
| JWT_SECRET   | Secret for JWT tokens    | your_secret_key        |
| FRONTEND_URL | Frontend application URL | http://localhost:3000  |

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
