const express = require('express');
const router = express.Router();
const {
  getWishlistItemsHandler,
  addItemToWishlistHandler,
  removeItemFromWishlistHandler,
  removeWishlistHandler
} = require('../controllers/WishlistController');
const { authenticateToken } = require('../middleware/auth');

// Wishlist routes - all require authentication
router.use(authenticateToken);

router.get('/', getWishlistItemsHandler);
router.post('/', addItemToWishlistHandler);
router.delete('/', removeWishlistHandler);
router.delete('/:productId', removeItemFromWishlistHandler);

module.exports = router; 