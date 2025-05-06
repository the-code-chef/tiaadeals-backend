const express = require('express');
const router = express.Router();
const { signupHandler, loginHandler } = require('../controllers/AuthController');

// Auth routes
router.post('/signup', signupHandler);
router.post('/login', loginHandler);

module.exports = router; 