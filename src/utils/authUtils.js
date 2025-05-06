const jwt = require('jsonwebtoken');
const dayjs = require('dayjs');

/**
 * Middleware to require authentication and extract user info from JWT.
 * Usage: app.use('/protected', requiresAuth, ...);
 */
function requiresAuth(req, res, next) {
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({ errors: ['No token provided. Unauthorized access error.'] });
  }
  const token = authHeader.split(' ')[1];
  if (!token) {
    return res.status(401).json({ errors: ['Malformed token. Unauthorized access error.'] });
  }
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded; // Attach user info to request
    next();
  } catch (err) {
    return res.status(401).json({ errors: ['The token is invalid. Unauthorized access error.'] });
  }
}

/**
 * Utility to format the current date in ISO format.
 */
function formatDate() {
  return dayjs().format('YYYY-MM-DDTHH:mm:ssZ');
}

module.exports = {
  requiresAuth,
  formatDate
}; 