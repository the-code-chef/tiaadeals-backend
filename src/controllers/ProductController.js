const pool = require("../utils/db");
const logger = require("../utils/logger");

/**
 * All the routes related to Product are present here.
 * These are Publicly accessible routes.
 * */

/**
 * This handler handles gets all products in the db.
 * send GET Request at /api/products
 * */
const getAllProductsHandler = async (req, res) => {
  try {
    const result = await pool.query(`
      SELECT p.*, c.category_name 
      FROM products p
      LEFT JOIN categories c ON p.category_id = c.id
      ORDER BY p.created_at DESC
    `);
    return res.status(200).json({
      success: true,
      data: {
        products: result.rows,
      },
    });
  } catch (error) {
    logger.error("Error fetching all products:", error);
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to fetch products",
        details: {
          suggestion: "Please try again in a few minutes",
        },
      },
    });
  }
};

/**
 * This handler handles gets a specific product by ID.
 * send GET Request at /api/products/:productId
 * */
const getProductHandler = async (req, res) => {
  const { productId } = req.params;

  // Validate productId
  if (!productId || productId === "undefined") {
    return res.status(400).json({
      success: false,
      error: {
        code: "INVALID_PRODUCT_ID",
        message: "Product ID is required",
        details: {
          suggestion: "Please provide a valid product ID",
        },
      },
    });
  }

  // Validate UUID format
  const uuidRegex =
    /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
  if (!uuidRegex.test(productId)) {
    return res.status(400).json({
      success: false,
      error: {
        code: "INVALID_PRODUCT_ID",
        message: "Invalid product ID format",
        details: {
          suggestion: "Please provide a valid product ID",
        },
      },
    });
  }

  try {
    // Get product with category details
    const productResult = await pool.query(
      `SELECT p.*, c.category_name, c.category_image as category_image
       FROM products p
       LEFT JOIN categories c ON p.category_id = c.id
       WHERE p.id = $1`,
      [productId]
    );

    if (productResult.rows.length === 0) {
      return res.status(404).json({
        success: false,
        error: {
          code: "PRODUCT_NOT_FOUND",
          message: "Product not found",
          details: {
            suggestion: "Please check the product ID and try again",
          },
        },
      });
    }

    const product = productResult.rows[0];

    // Get related products (same category, excluding current product)
    const relatedProductsResult = await pool.query(
      `SELECT p.*, c.category_name
       FROM products p
       LEFT JOIN categories c ON p.category_id = c.id
       WHERE p.category_id = $1 AND p.id != $2
       ORDER BY p.created_at DESC
       LIMIT 4`,
      [product.category_id, productId]
    );

    // Calculate discount percentage
    const discountPercentage = Math.round(
      ((product.original_price - product.price) / product.original_price) * 100
    );

    // Format the response
    const formattedProduct = {
      ...product,
      discount_percentage: discountPercentage,
      related_products: relatedProductsResult.rows,
    };

    return res.status(200).json({
      success: true,
      data: {
        product: formattedProduct,
      },
    });
  } catch (error) {
    logger.error("Error fetching product:", error);
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to fetch product",
        details: {
          suggestion: "Please try again in a few minutes",
        },
      },
    });
  }
};

/**
 * This handler handles searching products by name.
 * send GET Request at /api/products/search?query=searchTerm
 * */
const searchProductsHandler = async (req, res) => {
  const { query } = req.query;

  if (!query) {
    return res.status(400).json({
      success: false,
      error: {
        code: "MISSING_QUERY",
        message: "Search query is required",
        details: {
          suggestion: "Please provide a search term",
        },
      },
    });
  }

  try {
    const result = await pool.query(
      `SELECT p.*, c.category_name 
       FROM products p
       LEFT JOIN categories c ON p.category_id = c.id
       WHERE LOWER(p.name) LIKE LOWER($1) 
       ORDER BY p.created_at DESC
       LIMIT 10`,
      [`%${query}%`]
    );

    return res.status(200).json({
      success: true,
      data: {
        products: result.rows,
      },
    });
  } catch (error) {
    logger.error("Error searching products:", error);
    return res.status(500).json({
      success: false,
      error: {
        code: "INTERNAL_SERVER_ERROR",
        message: "Failed to search products",
        details: {
          suggestion: "Please try again in a few minutes",
        },
      },
    });
  }
};

module.exports = {
  getAllProductsHandler,
  getProductHandler,
  searchProductsHandler,
};
