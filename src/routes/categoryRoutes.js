const express = require('express');
const router = express.Router();
const {
  getAllCategoriesHandler,
  getCategoryHandler
} = require('../controllers/CategoryController');

// Category routes - all public
router.get('/', getAllCategoriesHandler);
router.get('/:categoryId', getCategoryHandler);

module.exports = router; 