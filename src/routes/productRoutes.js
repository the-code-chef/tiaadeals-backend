const express = require('express');
const router = express.Router();
const {
  getAllProductsHandler,
  getProductHandler,
  searchProductsHandler
} = require('../controllers/ProductController');

// Product routes - all public
router.get('/', getAllProductsHandler);
router.get('/search', searchProductsHandler);
router.get('/:productId', getProductHandler);

module.exports = router; 