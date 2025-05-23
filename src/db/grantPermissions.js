const pool = require("../utils/db");

async function grantPermissions() {
  try {
    // Grant all privileges on all tables to the user
    await pool.query(`
      GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO tiaadeals_user;
      GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO tiaadeals_user;
      GRANT ALL PRIVILEGES ON SCHEMA public TO tiaadeals_user;
    `);

    console.log("Successfully granted permissions to tiaadeals_user");
    process.exit(0);
  } catch (error) {
    console.error("Error granting permissions:", error);
    process.exit(1);
  }
}

grantPermissions();
