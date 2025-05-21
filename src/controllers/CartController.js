const pool = require('../utils/db');
const jwt = require('jsonwebtoken');

/**
 * All the routes related to Cart are present here.
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
 * This handler handles getting items to user's cart.
 * send GET Request at /api/user/cart
 * */
const getCartItemsHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
  }

  try {
    const result = await pool.query(
      'SELECT cart FROM users WHERE id = $1',
      [userId]
    );
    
    if (result.rows.length === 0) {
      return res.status(404).json({ errors: ['User not found'] });
    }

    return res.status(200).json({ cart: result.rows[0].cart || [] });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles adding items to user's cart.
 * send POST Request at /api/user/cart
 * body contains {product}
 * */
const addItemToCartHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
  }

  try {
    const { product } = req.body;
    const result = await pool.query(
      'SELECT cart FROM users WHERE id = $1',
      [userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ errors: ['User not found'] });
    }

    const currentCart = result.rows[0].cart || [];
    const updatedCart = [...currentCart, {
      ...product,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      qty: 1
    }];

    await pool.query(
      'UPDATE users SET cart = $1 WHERE id = $2',
      [JSON.stringify(updatedCart), userId]
    );

    return res.status(201).json({ cart: updatedCart });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles removing items from user's cart.
 * send DELETE Request at /api/user/cart/:productId
 * */
const removeItemFromCartHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
  }

  try {
    const { productId } = req.params;
    const result = await pool.query(
      'SELECT cart FROM users WHERE id = $1',
      [userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ errors: ['User not found'] });
    }

    const currentCart = result.rows[0].cart || [];
    const updatedCart = currentCart.filter(item => item._id !== productId);

    await pool.query(
      'UPDATE users SET cart = $1 WHERE id = $2',
      [JSON.stringify(updatedCart), userId]
    );

    return res.status(200).json({ cart: updatedCart });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles clearing the user's cart.
 * send DELETE Request at /api/user/cart
 * */
const removeCartHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
  }

  try {
    await pool.query(
      'UPDATE users SET cart = $1 WHERE id = $2',
      [JSON.stringify([]), userId]
    );

    return res.status(200).json({ cart: [] });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles updating cart item quantity.
 * send PATCH Request at /api/user/cart/:productId
 * body contains {action} (whose 'type' can be increment or decrement)
 * */
const updateCartItemHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ['Unauthorized access'] });
  }

  try {
    const { productId } = req.params;
    const { action } = req.body;
    
    const result = await pool.query(
      'SELECT cart FROM users WHERE id = $1',
      [userId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ errors: ['User not found'] });
    }

    let currentCart = result.rows[0].cart || [];
    
    if (action.type === 'increment') {
      currentCart = currentCart.map(product => {
        if (product._id === productId) {
          return {
            ...product,
            qty: product.qty + 1,
            updatedAt: new Date().toISOString()
          };
        }
        return product;
      });
    } else if (action.type === 'decrement') {
      currentCart = currentCart.map(product => {
        if (product._id === productId) {
          return {
            ...product,
            qty: Math.max(0, product.qty - 1),
            updatedAt: new Date().toISOString()
          };
        }
        return product;
      }).filter(product => product.qty > 0);
    }

    await pool.query(
      'UPDATE users SET cart = $1 WHERE id = $2',
      [JSON.stringify(currentCart), userId]
    );

    return res.status(200).json({ cart: currentCart });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

module.exports = {
  getCartItemsHandler,
  addItemToCartHandler,
  removeItemFromCartHandler,
  removeCartHandler,
  updateCartItemHandler
};
