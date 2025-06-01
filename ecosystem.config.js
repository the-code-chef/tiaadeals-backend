module.exports = {
  apps: [
    {
      name: "tiaadeals-backend",
      script: "src/server.js",
      instances: 1,
      autorestart: true,
      watch: false,
      max_memory_restart: "300M",
      env: {
        NODE_ENV: "production",
        PORT: 3000,
      },
      error_file: "logs/err.log",
      out_file: "logs/out.log",
      log_file: "logs/combined.log",
      time: true,
      exec_mode: "fork",
      exec_interpreter: "node",
      env_production: {
        NODE_ENV: "production",
      },
    },
  ],
};
