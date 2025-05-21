module.exports = {
  apps: [{
    name: 'tiaadeals-backend',
    script: 'src/server.js',
    instances: 1,
    autorestart: true,
    watch: false,
    max_memory_restart: '1G',
    env: {
      NODE_ENV: 'production',
      PORT: 3000,
      FRONTEND_URL: 'https://api.tiaadeals.com', // Update with your actual domain
      DB_HOST: process.env.DB_HOST,
      DB_USER: process.env.DB_USER,
      DB_PASSWORD: process.env.DB_PASSWORD,
      DB_NAME: process.env.DB_NAME,
      JWT_SECRET: process.env.JWT_SECRET
    }
  }]
}; 