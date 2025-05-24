const pool = require("../utils/db");
const jwt = require("jsonwebtoken");

/**
 * All the routes related to Wishlist are present here.
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
 * This handler handles getting items from user's wishlist.
 * send GET Request at /api/wishlist
 * */
const getWishlistItemsHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    // Get or create wishlist for user
    let wishlistResult = await pool.query(
      "SELECT id FROM wishlist WHERE user_id = $1",
      [userId]
    );

    let wishlistId;
    if (wishlistResult.rows.length === 0) {
      // Create new wishlist for user
      const newWishlistResult = await pool.query(
        "INSERT INTO wishlist (user_id) VALUES ($1) RETURNING id",
        [userId]
      );
      wishlistId = newWishlistResult.rows[0].id;
    } else {
      wishlistId = wishlistResult.rows[0].id;
    }

    // Get wishlist items with product details
    const itemsResult = await pool.query(
      `SELECT wi.*, p.name, p.price, p.original_price, p.image, p.company
       FROM wishlist_items wi
       JOIN products p ON wi.product_id = p.id
       WHERE wi.wishlist_id = $1`,
      [wishlistId]
    );

    return res.status(200).json({
      success: true,
      data: {
        wishlist: itemsResult.rows,
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to fetch wishlist items",
        details: error.message,
      },
    });
  }
};

/**
 * This handler handles adding items to user's wishlist.
 * send POST Request at /api/wishlist
 * body contains {productId, selectedColor}
 * */
const addItemToWishlistHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    const { productId, selectedColor } = req.body;

    if (!productId) {
      return res.status(400).json({
        success: false,
        error: {
          code: "MISSING_PRODUCT_ID",
          message: "Product ID is required",
        },
      });
    }

    // Get or create wishlist for user
    let wishlistResult = await pool.query(
      "SELECT id FROM wishlist WHERE user_id = $1",
      [userId]
    );

    let wishlistId;
    if (wishlistResult.rows.length === 0) {
      // Create new wishlist for user
      const newWishlistResult = await pool.query(
        "INSERT INTO wishlist (user_id) VALUES ($1) RETURNING id",
        [userId]
      );
      wishlistId = newWishlistResult.rows[0].id;
    } else {
      wishlistId = wishlistResult.rows[0].id;
    }

    // Check if item already exists in wishlist
    const existingItem = await pool.query(
      "SELECT * FROM wishlist_items WHERE wishlist_id = $1 AND product_id = $2 AND selected_color = $3",
      [wishlistId, productId, selectedColor]
    );

    if (existingItem.rows.length === 0) {
      // Add new item
      await pool.query(
        "INSERT INTO wishlist_items (wishlist_id, product_id, selected_color) VALUES ($1, $2, $3)",
        [wishlistId, productId, selectedColor]
      );
    }

    // Get updated wishlist items
    const updatedItems = await pool.query(
      `SELECT wi.*, p.name, p.price, p.original_price, p.image, p.company
       FROM wishlist_items wi
       JOIN products p ON wi.product_id = p.id
       WHERE wi.wishlist_id = $1`,
      [wishlistId]
    );

    return res.status(201).json({
      success: true,
      data: {
        wishlist: updatedItems.rows,
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to add item to wishlist",
        details: error.message,
      },
    });
  }
};

/**
 * This handler handles removing items from user's wishlist.
 * send DELETE Request at /api/wishlist/:productId
 * */
const removeItemFromWishlistHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    const { productId } = req.params;
    const { selectedColor } = req.query;

    // Get user's wishlist
    const wishlistResult = await pool.query(
      "SELECT id FROM wishlist WHERE user_id = $1",
      [userId]
    );

    if (wishlistResult.rows.length === 0) {
      return res.status(404).json({
        success: false,
        error: {
          code: "WISHLIST_NOT_FOUND",
          message: "Wishlist not found",
        },
      });
    }

    const wishlistId = wishlistResult.rows[0].id;

    // Delete the item
    await pool.query(
      "DELETE FROM wishlist_items WHERE wishlist_id = $1 AND product_id = $2 AND selected_color = $3",
      [wishlistId, productId, selectedColor]
    );

    // Get updated wishlist items
    const updatedItems = await pool.query(
      `SELECT wi.*, p.name, p.price, p.original_price, p.image, p.company
       FROM wishlist_items wi
       JOIN products p ON wi.product_id = p.id
       WHERE wi.wishlist_id = $1`,
      [wishlistId]
    );

    return res.status(200).json({
      success: true,
      data: {
        wishlist: updatedItems.rows,
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to remove item from wishlist",
        details: error.message,
      },
    });
  }
};

/**
 * This handler handles clearing the user's wishlist.
 * send DELETE Request at /api/wishlist
 * */
const removeWishlistHandler = async (req, res) => {
  const userId = getUserIdFromToken(req);
  if (!userId) {
    return res.status(401).json({ errors: ["Unauthorized access"] });
  }

  try {
    // Get user's wishlist
    const wishlistResult = await pool.query(
      "SELECT id FROM wishlist WHERE user_id = $1",
      [userId]
    );

    if (wishlistResult.rows.length === 0) {
      return res.status(404).json({
        success: false,
        error: {
          code: "WISHLIST_NOT_FOUND",
          message: "Wishlist not found",
        },
      });
    }

    const wishlistId = wishlistResult.rows[0].id;

    // Delete all items from wishlist
    await pool.query("DELETE FROM wishlist_items WHERE wishlist_id = $1", [
      wishlistId,
    ]);

    return res.status(200).json({
      success: true,
      data: {
        wishlist: [],
      },
    });
  } catch (error) {
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to clear wishlist",
        details: error.message,
      },
    });
  }
};

module.exports = {
  getWishlistItemsHandler,
  addItemToWishlistHandler,
  removeItemFromWishlistHandler,
  removeWishlistHandler,
};
