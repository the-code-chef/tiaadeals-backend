const pool = require('../utils/db');
const jwt = require('jsonwebtoken');

/**
 * All the routes related to Wishlist are present here.
 * These are private routes.
 * Client needs to add "authorization" header with JWT token in it to access it.
 * */

const getUserIdFromToken = (req) => {
  try {
    const token = req.headers.authorization?.split(' ')[1];
    if (!token) return null;
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    return decoded.id;
  } catch (error) {
    return null;
  }
};

/**
 * This handler handles getting items from user's wishlist.
 * send GET Request at /api/user/wishlist
 * */
const getWishlistItemsHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
  }

  try {
    const result = await pool.query(
      'SELECT wishlist FROM users WHERE id = $1',
      [userId]
    );
    
    if (result.rows.length === 0) {
      return res.status(404).json({ errors: ['User not found'] });
    }

    return res.status(200).json({ wishlist: result.rows[0].wishlist || [] });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles adding items to user's wishlist.
 * send POST Request at /api/user/wishlist
 * body contains {product}
 * */
const addItemToWishlistHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
    if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
  }

  try {
    const { product } = req.body;
    const result = await pool.query(
      'SELECT wishlist FROM users WHERE id = $1',
      [userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ errors: ['User not found'] });
    }

    const currentWishlist = result.rows[0].wishlist || [];
    const updatedWishlist = [...currentWishlist, {
      ...product,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }];

    await pool.query(
      'UPDATE users SET wishlist = $1 WHERE id = $2',
      [JSON.stringify(updatedWishlist), userId]
    );

    return res.status(201).json({ wishlist: updatedWishlist });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles removing items from user's wishlist.
 * send DELETE Request at /api/user/wishlist/:productId
 * */
const removeItemFromWishlistHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
    if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
  }

  try {
    const { productId } = req.params;
    const result = await pool.query(
      'SELECT wishlist FROM users WHERE id = $1',
      [userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ errors: ['User not found'] });
    }

    const currentWishlist = result.rows[0].wishlist || [];
    const updatedWishlist = currentWishlist.filter(item => item._id !== productId);

    await pool.query(
      'UPDATE users SET wishlist = $1 WHERE id = $2',
      [JSON.stringify(updatedWishlist), userId]
    );

    return res.status(200).json({ wishlist: updatedWishlist });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles clearing the user's wishlist.
 * send DELETE Request at /api/user/wishlist
 * */
const removeWishlistHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
    if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
    }

  try {
    await pool.query(
      'UPDATE users SET wishlist = $1 WHERE id = $2',
      [JSON.stringify([]), userId]
    );

    return res.status(200).json({ wishlist: [] });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

module.exports = {
  getWishlistItemsHandler,
  addItemToWishlistHandler,
  removeItemFromWishlistHandler,
  removeWishlistHandler
};
