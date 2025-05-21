const { v4: uuid } = require("uuid");
const { formatDate } = require("../utils/authUtils");
const pool = require('../utils/db');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');

/**
 * This handler handles user signups.
 * send POST Request at /api/auth/signup
 * body contains {firstName, lastName, email, password}
 */
const signupHandler = async (req, res) => {
  // Accept both camelCase and snake_case
  const firstName = req.body.firstName || req.body.first_name;
  const lastName = req.body.lastName || req.body.last_name;
  const { email, password } = req.body;
  try {
    const userCheck = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
    if (userCheck.rows.length > 0) {
      return res.status(422).json({ errors: ['Email already exists.'] });
    }
    const hashedPassword = await bcrypt.hash(password, 10);
    const result = await pool.query(
      `INSERT INTO users (first_name, last_name, email, password)
       VALUES ($1, $2, $3, $4) RETURNING id, first_name, last_name, email, created_at, updated_at`,
      [firstName, lastName, email, hashedPassword]
    );
    const createdUser = result.rows[0];
    const token = jwt.sign(
      { id: createdUser.id, email: createdUser.email },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );
    res.status(201).json({ user: createdUser, token });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles user login.
 * send POST Request at /api/auth/login
 * body contains {email, password}
 */
const loginHandler = async (req, res) => {
  const { email, password } = req.body;
  try {
    const userResult = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
    if (userResult.rows.length === 0) {
      return res.status(404).json({ errors: ['Email not registered.'] });
    }
    const user = userResult.rows[0];
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ errors: ['Invalid credentials.'] });
    }
    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );
    delete user.password;
    res.status(200).json({ user, token });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

module.exports = {
  signupHandler,
  loginHandler
}; 