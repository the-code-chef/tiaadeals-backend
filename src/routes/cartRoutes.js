const express = require('express');
const router = express.Router();
const { 
  getCartItemsHandler,
  addItemToCartHandler,
  removeItemFromCartHandler,
  removeCartHandler,
  updateCartItemHandler
} = require('../controllers/CartController');
const { authenticateToken } = require('../middleware/auth');

// Cart routes - all require authentication
router.use(authenticateToken);

router.get('/', getCartItemsHandler);
router.post('/', addItemToCartHandler);
router.delete('/', removeCartHandler);
router.delete('/:productId', removeItemFromCartHandler);
router.patch('/:productId', updateCartItemHandler);

module.exports = router; 