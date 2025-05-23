const { Pool } = require("pg");
const fs = require("fs");
const path = require("path");
const dotenv = require("dotenv");
const os = require("os");

// Load environment variables
dotenv.config();

// Get system username
const systemUser = os.userInfo().username;

// Create a pool with system user
const superuserPool = new Pool({
  user: systemUser,
  host: process.env.DB_HOST || "localhost",
  database: "postgres",
  password: process.env.POSTGRES_PASSWORD,
  port: process.env.DB_PORT || 5432,
});

async function setupDatabase() {
  let client;
  let tiaadealsClient;
  let tiaadealsPool;

  try {
    // Get a client from the pool
    client = await superuserPool.connect();

    // Drop database and user (outside transaction)
    await client.query("DROP DATABASE IF EXISTS tiaadeals");
    await client.query("DROP USER IF EXISTS tiaadeals_user");

    // Create user and database (outside transaction)
    await client.query(
      `CREATE USER tiaadeals_user WITH PASSWORD '${process.env.DB_PASSWORD}'`
    );
    await client.query("CREATE DATABASE tiaadeals OWNER tiaadeals_user");

    console.log("Database and user created successfully");

    // Release the superuser client
    client.release();
    client = null;

    // Connect to the new database
    tiaadealsPool = new Pool({
      user: systemUser,
      host: process.env.DB_HOST || "localhost",
      database: "tiaadeals",
      password: process.env.POSTGRES_PASSWORD,
      port: process.env.DB_PORT || 5432,
    });

    tiaadealsClient = await tiaadealsPool.connect();

    // Read and execute the initialization SQL
    const sqlFile = path.join(__dirname, "init.sql");
    const sql = fs.readFileSync(sqlFile, "utf8");
    await tiaadealsClient.query(sql);

    // Grant permissions
    await tiaadealsClient.query(`
      GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO tiaadeals_user;
      GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO tiaadeals_user;
      GRANT ALL PRIVILEGES ON SCHEMA public TO tiaadeals_user;
      ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO tiaadeals_user;
      ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO tiaadeals_user;
    `);

    console.log("Database setup completed successfully");
  } catch (error) {
    console.error("Error setting up database:", error);
    throw error;
  } finally {
    // Clean up connections
    if (client) {
      client.release();
    }
    if (tiaadealsClient) {
      tiaadealsClient.release();
    }
    if (tiaadealsPool) {
      await tiaadealsPool.end();
    }
    await superuserPool.end();
  }
}

// Run the setup
setupDatabase()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error("Setup failed:", error);
    process.exit(1);
  });
