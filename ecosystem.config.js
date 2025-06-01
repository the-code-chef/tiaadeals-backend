module.exports = {
  apps: [
    {
      name: "tiaadeals-backend",
      script: "/var/www/tiaadeals-backend/src/server.js",
      instances: 1,
      autorestart: true,
      watch: false,
      max_memory_restart: "300M",
      env: {
        NODE_ENV: "production",
      },
      error_file: "/var/www/tiaadeals-backend/logs/err.log",
      out_file: "/var/www/tiaadeals-backend/logs/out.log",
      log_file: "/var/www/tiaadeals-backend/logs/combined.log",
      time: true,
      exec_mode: "fork",
      exec_interpreter: "node",
      env_production: {
        NODE_ENV: "production",
      },
    },
  ],
};
