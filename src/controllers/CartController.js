const pool = require("../utils/db");
const jwt = require("jsonwebtoken");

/**
 * All the routes related to Cart are present here.
 * These are private routes.
 * Client needs to add "authorization" header with JWT token in it to access it.
 * */

const getUserIdFromToken = (req) => {
  try {
    const token = req.headers.authorization?.split(" ")[1];
    if (!token) return null;
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    return decoded.id;
  } catch (error) {
    return null;
  }
};

/**
 * This handler handles getting items from user's cart.
 * send GET Request at /api/cart
 * */
const getCartItemsHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    // Get or create cart for user
    let cartResult = await pool.query(
      "SELECT id FROM cart WHERE user_id = $1",
      [userId]
    );

    let cartId;
    if (cartResult.rows.length === 0) {
      // Create new cart for user
      const newCartResult = await pool.query(
        "INSERT INTO cart (user_id) VALUES ($1) RETURNING id",
        [userId]
      );
      cartId = newCartResult.rows[0].id;
    } else {
      cartId = cartResult.rows[0].id;
    }

    // Get cart items with product details
    const itemsResult = await pool.query(
      `SELECT ci.*, p.name, p.price, p.original_price, p.image, p.company
       FROM cart_items ci
       JOIN products p ON ci.product_id = p.id
       WHERE ci.cart_id = $1`,
      [cartId]
    );

    return res.status(200).json({
      success: true,
      data: {
        cart: itemsResult.rows,
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to fetch cart items",
        details: error.message,
      },
    });
  }
};

/**
 * This handler handles adding items to user's cart.
 * send POST Request at /api/cart
 * body contains {productId, quantity, selectedColor}
 * */
const addItemToCartHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    const { productId, quantity = 1, selectedColor } = req.body;

    if (!productId) {
      return res.status(400).json({
        success: false,
        error: {
          code: "MISSING_PRODUCT_ID",
          message: "Product ID is required",
        },
      });
    }

    // Get or create cart for user
    let cartResult = await pool.query(
      "SELECT id FROM cart WHERE user_id = $1",
      [userId]
    );

    let cartId;
    if (cartResult.rows.length === 0) {
      // Create new cart for user
      const newCartResult = await pool.query(
        "INSERT INTO cart (user_id) VALUES ($1) RETURNING id",
        [userId]
      );
      cartId = newCartResult.rows[0].id;
    } else {
      cartId = cartResult.rows[0].id;
    }

    // Check if item already exists in cart
    const existingItem = await pool.query(
      "SELECT * FROM cart_items WHERE cart_id = $1 AND product_id = $2 AND selected_color = $3",
      [cartId, productId, selectedColor]
    );

    if (existingItem.rows.length > 0) {
      // Update quantity if item exists
      await pool.query(
        "UPDATE cart_items SET quantity = quantity + $1, updated_at = CURRENT_TIMESTAMP WHERE id = $2",
        [quantity, existingItem.rows[0].id]
      );
    } else {
      // Add new item
      await pool.query(
        "INSERT INTO cart_items (cart_id, product_id, quantity, selected_color) VALUES ($1, $2, $3, $4)",
        [cartId, productId, quantity, selectedColor]
      );
    }

    // Get updated cart items
    const updatedItems = await pool.query(
      `SELECT ci.*, p.name, p.price, p.original_price, p.image, p.company
       FROM cart_items ci
       JOIN products p ON ci.product_id = p.id
       WHERE ci.cart_id = $1`,
      [cartId]
    );

    return res.status(201).json({
      success: true,
      data: {
        cart: updatedItems.rows,
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to add item to cart",
        details: error.message,
      },
    });
  }
};

/**
 * This handler handles removing items from user's cart.
 * send DELETE Request at /api/cart/:productId
 * */
const removeItemFromCartHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    const { productId } = req.params;
    const { selectedColor } = req.query;

    // Get user's cart
    const cartResult = await pool.query(
      "SELECT id FROM cart WHERE user_id = $1",
      [userId]
    );

    if (cartResult.rows.length === 0) {
      return res.status(404).json({
        success: false,
        error: {
          code: "CART_NOT_FOUND",
          message: "Cart not found",
        },
      });
    }

    const cartId = cartResult.rows[0].id;

    // Delete the item
    await pool.query(
      "DELETE FROM cart_items WHERE cart_id = $1 AND product_id = $2 AND selected_color = $3",
      [cartId, productId, selectedColor]
    );

    // Get updated cart items
    const updatedItems = await pool.query(
      `SELECT ci.*, p.name, p.price, p.original_price, p.image, p.company
       FROM cart_items ci
       JOIN products p ON ci.product_id = p.id
       WHERE ci.cart_id = $1`,
      [cartId]
    );

    return res.status(200).json({
      success: true,
      data: {
        cart: updatedItems.rows,
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to remove item from cart",
        details: error.message,
      },
    });
  }
};

/**
 * This handler handles clearing the user's cart.
 * send DELETE Request at /api/cart
 * */
const removeCartHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    // Get user's cart
    const cartResult = await pool.query(
      "SELECT id FROM cart WHERE user_id = $1",
      [userId]
    );

    if (cartResult.rows.length === 0) {
      return res.status(404).json({
        success: false,
        error: {
          code: "CART_NOT_FOUND",
          message: "Cart not found",
        },
      });
    }

    const cartId = cartResult.rows[0].id;

    // Delete all items from cart
    await pool.query("DELETE FROM cart_items WHERE cart_id = $1", [cartId]);

    return res.status(200).json({
      success: true,
      data: {
        cart: [],
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to clear cart",
        details: error.message,
      },
    });
  }
};

/**
 * This handler handles updating cart item quantity.
 * send PATCH Request at /api/cart/:productId
 * body contains {quantity}
 * */
const updateCartItemHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    const { productId } = req.params;
    const { quantity, selectedColor } = req.body;

    if (quantity < 0) {
      return res.status(400).json({
        success: false,
        error: {
          code: "INVALID_QUANTITY",
          message: "Quantity cannot be negative",
        },
      });
    }

    // Get user's cart
    const cartResult = await pool.query(
      "SELECT id FROM cart WHERE user_id = $1",
      [userId]
    );

    if (cartResult.rows.length === 0) {
      return res.status(404).json({
        success: false,
        error: {
          code: "CART_NOT_FOUND",
          message: "Cart not found",
        },
      });
    }

    const cartId = cartResult.rows[0].id;

    if (quantity === 0) {
      // Remove item if quantity is 0
      await pool.query(
        "DELETE FROM cart_items WHERE cart_id = $1 AND product_id = $2 AND selected_color = $3",
        [cartId, productId, selectedColor]
      );
    } else {
      // Update quantity
      await pool.query(
        "UPDATE cart_items SET quantity = $1, updated_at = CURRENT_TIMESTAMP WHERE cart_id = $2 AND product_id = $3 AND selected_color = $4",
        [quantity, cartId, productId, selectedColor]
      );
    }

    // Get updated cart items
    const updatedItems = await pool.query(
      `SELECT ci.*, p.name, p.price, p.original_price, p.image, p.company
       FROM cart_items ci
       JOIN products p ON ci.product_id = p.id
       WHERE ci.cart_id = $1`,
      [cartId]
    );

    return res.status(200).json({
      success: true,
      data: {
        cart: updatedItems.rows,
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to update cart item",
        details: error.message,
      },
    });
  }
};

module.exports = {
  getCartItemsHandler,
  addItemToCartHandler,
  removeItemFromCartHandler,
  removeCartHandler,
  updateCartItemHandler,
};
