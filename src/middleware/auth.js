const jwt = require('jsonwebtoken');

const authenticateToken = (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
      return res.status(401).json({ errors: ['No token provided'] });
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
      if (err) {
        return res.status(403).json({ errors: ['Invalid or expired token'] });
      }
      req.user = user;
      next();
    });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

module.exports = {
  authenticateToken
}; 