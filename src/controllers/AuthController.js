const { v4: uuid } = require("uuid");
const { formatDate } = require("../utils/authUtils");
const pool = require("../utils/db");
const jwt = require("jsonwebtoken");
const bcrypt = require("bcrypt");
const logger = require("../utils/logger");

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

  // Input validation
  if (!firstName || !lastName || !email || !password) {
    return res.status(400).json({
      success: false,
      error: {
        code: "MISSING_FIELDS",
        message: "Please fill in all required fields.",
        details: {
          firstName: !firstName ? "First name is required" : null,
          lastName: !lastName ? "Last name is required" : null,
          email: !email ? "Email is required" : null,
          password: !password ? "Password is required" : null,
        },
      },
    });
  }

  // Email format validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return res.status(400).json({
      success: false,
      error: {
        code: "INVALID_EMAIL",
        message: "Please enter a valid email address.",
        details: {
          email: "The email format is incorrect",
        },
      },
    });
  }

  // Password strength validation
  if (password.length < 6) {
    return res.status(400).json({
      success: false,
      error: {
        code: "WEAK_PASSWORD",
        message: "Please choose a stronger password.",
        details: {
          password: "Password must be at least 6 characters long",
        },
      },
    });
  }

  try {
    const userCheck = await pool.query("SELECT * FROM users WHERE email = $1", [
      email,
    ]);
    if (userCheck.rows.length > 0) {
      return res.status(409).json({
        success: false,
        error: {
          code: "EMAIL_EXISTS",
          message: "This email is already registered.",
          details: {
            email:
              "You can try logging in instead, or use a different email address",
          },
          action: "LOGIN",
        },
      });
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
      { expiresIn: "7d" }
    );
    res.status(201).json({
      success: true,
      data: {
        user: createdUser,
        token,
      },
      message: "Account created successfully! Welcome to TiaaDeals.",
    });
  } catch (error) {
    logger.error("Signup error:", error);
    res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "We're having trouble creating your account right now.",
        details: {
          suggestion: "Please try again in a few minutes",
        },
      },
    });
  }
};

/**
 * This handler handles user login.
 * send POST Request at /api/auth/login
 * body contains {email, password}
 */
const loginHandler = async (req, res) => {
  const { email, password } = req.body;

  // Input validation
  if (!email || !password) {
    return res.status(400).json({
      success: false,
      error: {
        code: "MISSING_FIELDS",
        message: "Please enter both email and password.",
        details: {
          email: !email ? "Email is required" : null,
          password: !password ? "Password is required" : null,
        },
      },
    });
  }

  try {
    const userResult = await pool.query(
      "SELECT * FROM users WHERE email = $1",
      [email]
    );

    if (userResult.rows.length === 0) {
      return res.status(401).json({
        success: false,
        error: {
          code: "INVALID_CREDENTIALS",
          message: "We couldn't find an account with this email.",
          details: {
            suggestion: "Please check your email or create a new account",
          },
          action: "SIGNUP",
        },
      });
    }

    const user = userResult.rows[0];
    const isMatch = await bcrypt.compare(password, user.password);

    if (!isMatch) {
      return res.status(401).json({
        success: false,
        error: {
          code: "INVALID_CREDENTIALS",
          message: "The password you entered is incorrect.",
          details: {
            suggestion: "Please check your password and try again",
          },
        },
      });
    }

    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET,
      { expiresIn: "7d" }
    );

    // Remove sensitive data before sending response
    delete user.password;

    res.status(200).json({
      success: true,
      data: {
        user,
        token,
      },
      message: `Welcome back, ${user.first_name}!`,
    });
  } catch (error) {
    logger.error("Login error:", error);
    res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "We're having trouble signing you in right now.",
        details: {
          suggestion: "Please try again in a few minutes",
        },
      },
    });
  }
};

module.exports = {
  signupHandler,
  loginHandler,
};
