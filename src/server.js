const express = require("express");
const dotenv = require("dotenv");
const morgan = require("morgan");
const path = require("path");
const fs = require("fs");

// Load environment variables
const envFile =
  process.env.NODE_ENV === "production" ? ".env.production" : ".env";
const envPath = path.resolve(process.cwd(), envFile);

console.log("Loading environment from:", envPath);
console.log("Current working directory:", process.cwd());

if (fs.existsSync(envPath)) {
  console.log("Environment file exists, loading...");
  dotenv.config({ path: envPath });
} else {
  console.error("Environment file not found:", envPath);
  process.exit(1);
}

// Verify required environment variables
const requiredEnvVars = [
  "DB_HOST",
  "DB_PORT",
  "DB_NAME",
  "DB_USER",
  "DB_PASSWORD",
  "JWT_SECRET",
];

const missingEnvVars = requiredEnvVars.filter(
  (varName) => !process.env[varName]
);
if (missingEnvVars.length > 0) {
  console.error("Missing required environment variables:", missingEnvVars);
  process.exit(1);
}

// Debug: Log environment variables (excluding sensitive data)
console.log("Environment variables loaded:", {
  NODE_ENV: process.env.NODE_ENV,
  PORT: process.env.PORT,
  DB_HOST: process.env.DB_HOST,
  DB_PORT: process.env.DB_PORT,
  DB_NAME: process.env.DB_NAME,
  DB_USER: process.env.DB_USER,
  LOG_LEVEL: process.env.LOG_LEVEL,
});

const logger = require("./utils/logger");
const authRoutes = require("./routes/authRoutes");
const cartRoutes = require("./routes/cartRoutes");
const wishlistRoutes = require("./routes/wishlistRoutes");
const productRoutes = require("./routes/productRoutes");
const categoryRoutes = require("./routes/categoryRoutes");
const { authenticateToken } = require("./middleware/auth");

const app = express();

// Create logs directory if it doesn't exist
const logsDir = process.env.LOG_DIR || path.join(process.cwd(), "logs");
if (!fs.existsSync(logsDir)) {
  fs.mkdirSync(logsDir, { recursive: true });
}

// Basic middleware
app.use(express.json());
app.use(morgan("combined", { stream: logger.stream }));

// Request logging
app.use((req, res, next) => {
  logger.info("Incoming request:", {
    method: req.method,
    path: req.path,
    headers: req.headers,
    body: req.body,
    origin: req.headers.origin,
  });
  next();
});

// Health check endpoint
app.get("/health", (req, res) => {
  res.status(200).json({ status: "ok", message: "Server is running" });
});

// Mount routes
app.use("/api/auth", authRoutes);
app.use("/api/cart", authenticateToken, cartRoutes);
app.use("/api/wishlist", authenticateToken, wishlistRoutes);
app.use("/api/products", productRoutes);
app.use("/api/categories", categoryRoutes);

// 404 handler
app.use((req, res) => {
  logger.warn("Route not found:", { path: req.path });
  res.status(404).json({ error: "Route not found" });
});

// Response logging
app.use((req, res, next) => {
  const originalSend = res.send;
  res.send = function (body) {
    logger.info("Outgoing response:", {
      statusCode: res.statusCode,
      responseBody: body,
    });
    return originalSend.call(this, body);
  };
  next();
});

// Determine if we're in production
const isProduction = process.env.NODE_ENV === "production";

// Start server based on environment
if (isProduction) {
  // Production: Use TCP port
  const PORT = process.env.PORT || 3000;
  const server = app
    .listen(PORT, () => {
      console.log(`Server is running on port: ${PORT}`);
      logger.info(`Server is running on port: ${PORT}`);
    })
    .on("error", (err) => {
      console.error("Server error:", err);
      logger.error("Server error:", err);
      process.exit(1);
    });

  // Set timeout to 5 minutes
  server.timeout = 300000;
} else {
  // Development: Use port
  const PORT = process.env.PORT || 3000;

  // Function to start server
  const startServer = (port) => {
    const server = app
      .listen(port, () => {
        console.log(`Server is running on port: ${port}`);
        logger.info(`Server is running on port: ${port}`);
      })
      .on("error", (err) => {
        if (err.code === "EADDRINUSE") {
          console.log(`Port ${port} is busy, trying ${port + 1}...`);
          logger.warn(`Port ${port} is busy, trying ${port + 1}...`);
          startServer(port + 1);
        } else {
          console.error("Server error:", err);
          logger.error("Server error:", err);
          process.exit(1);
        }
      });

    // Set timeout to 5 minutes
    server.timeout = 300000;
  };

  // Start server with initial port
  startServer(PORT);
}

// Handle uncaught exceptions
process.on("uncaughtException", (err) => {
  logger.error("Uncaught Exception:", err);
  setTimeout(() => {
    process.exit(1);
  }, 1000);
});

// Handle unhandled promise rejections
process.on("unhandledRejection", (reason, promise) => {
  logger.error("Unhandled Rejection at:", promise, "reason:", reason);
  setTimeout(() => {
    process.exit(1);
  }, 1000);
});
