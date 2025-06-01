const { Pool } = require("pg");
const dotenv = require("dotenv");
const logger = require("./logger");

// Load environment variables if not already loaded
if (!process.env.DB_HOST) {
  dotenv.config();
}

// Validate required environment variables
const requiredEnvVars = [
  "DB_USER",
  "DB_HOST",
  "DB_NAME",
  "DB_PASSWORD",
  "DB_PORT",
];
const missingEnvVars = requiredEnvVars.filter((envVar) => !process.env[envVar]);

if (missingEnvVars.length > 0) {
  throw new Error(
    `Missing required environment variables: ${missingEnvVars.join(", ")}`
  );
}

// Log the database configuration (without sensitive data)
const dbConfig = {
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  port: process.env.DB_PORT,
};

logger.info("DB Connection Config:", dbConfig);

const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
  // Add connection timeout
  connectionTimeoutMillis: 5000,
  // Add idle timeout
  idleTimeoutMillis: 30000,
  // Add max connections
  max: 20,
});

// Test the connection
pool.on("connect", () => {
  logger.info("Connected to the database");
});

pool.on("error", (err) => {
  logger.error("Unexpected error on idle client", err);
  process.exit(-1);
});

// Add a function to test the connection
const testConnection = async () => {
  try {
    const client = await pool.connect();
    client.release();
    logger.info("Database connection test successful");
    return true;
  } catch (error) {
    logger.error("Database connection test failed:", error);
    return false;
  }
};

// Test the connection on startup
testConnection();

module.exports = pool;
