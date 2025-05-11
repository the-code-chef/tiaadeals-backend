const express = require('express');
const router = express.Router();
const { signupHandler, loginHandler } = require('../controllers/AuthController');
const logger = require('../utils/logger');

// Log all requests to auth routes
router.use((req, res, next) => {
  logger.info('Auth route accessed:', {
    method: req.method,
    path: req.path,
    body: req.body
  });
  next();
});

// Auth routes
router.post('/signup', signupHandler);
router.post('/login', loginHandler);

// Handle OPTIONS requests for specific routes
router.options('/signup', (req, res) => {
  res.status(204).end();
});

router.options('/login', (req, res) => {
  res.status(204).end();
});

module.exports = router; 