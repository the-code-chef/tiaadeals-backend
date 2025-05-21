const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const morgan = require('morgan');
const logger = require('./utils/logger');
const authRoutes = require('./routes/authRoutes');
const cartRoutes = require('./routes/cartRoutes');
const wishlistRoutes = require('./routes/wishlistRoutes');
const productRoutes = require('./routes/productRoutes');
const categoryRoutes = require('./routes/categoryRoutes');
const { authenticateToken } = require('./middleware/auth');

// Load environment variables
dotenv.config();

// Debug: Log environment variables
console.log('Environment variables:', {
  PORT: process.env.PORT,
  NODE_ENV: process.env.NODE_ENV,
  DB_HOST: process.env.DB_HOST,
  DB_PORT: process.env.DB_PORT,
  DB_NAME: process.env.DB_NAME,
  DB_USER: process.env.DB_USER
});

const app = express();

// Create logs directory if it doesn't exist
const fs = require('fs');
const path = require('path');
if (!fs.existsSync('logs')) {
  fs.mkdirSync('logs');
}

// CORS configuration
const corsOptions = {
  origin: 'http://localhost:3000',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  credentials: true
};

// Basic middleware
app.use(cors(corsOptions));
app.use(express.json());
app.use(morgan('combined', { stream: logger.stream }));

// Request logging
app.use((req, res, next) => {
  logger.info('Incoming request:', {
    method: req.method,
    path: req.path,
    headers: req.headers,
    body: req.body
  });
  next();
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'ok', message: 'Server is running' });
});

// Mount routes
app.use('/api/auth', authRoutes);
app.use('/api/cart', authenticateToken, cartRoutes);
app.use('/api/wishlist', authenticateToken, wishlistRoutes);
app.use('/api/products', productRoutes);
app.use('/api/categories', categoryRoutes);

// 404 handler
app.use((req, res) => {
  logger.warn('Route not found:', { path: req.path });
  res.status(404).json({ error: 'Route not found' });
});

// Response logging
app.use((req, res, next) => {
  const originalSend = res.send;
  res.send = function (body) {
    logger.info('Outgoing response:', {
      statusCode: res.statusCode,
      responseBody: body
    });
    return originalSend.call(this, body);
  };
  next();
});

// Error handling
app.use((err, req, res, next) => {
  logger.error('Server error:', {
    error: err.message,
    stack: err.stack
  });
  res.status(500).json({ error: 'Something went wrong!' });
});

// Use port from environment variable or default to 8080
const PORT = process.env.PORT || 8080;

// Start server with error handling
const server = app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
  logger.info(`Server is running on port ${PORT}`);
}).on('error', (err) => {
  console.error('Server error:', err);
  if (err.code === 'EADDRINUSE') {
    logger.error(`Port ${PORT} is already in use. Please try a different port.`);
    process.exit(1);
  } else {
    logger.error('Server error:', err);
    process.exit(1);
  }
}); 