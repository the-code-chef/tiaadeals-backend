const pool = require("../utils/db");
const logger = require("../utils/logger");

/**
 * All the routes related to Category are present here.
 * These are Publicly accessible routes.
 * */

/**
 * This handler handles gets all categories in the db.
 * send GET Request at /api/categories
 * */
const getAllCategoriesHandler = async (req, res) => {
  try {
    logger.info("Fetching all categories");
    const result = await pool.query(
      "SELECT * FROM categories ORDER BY category_name"
    );
    logger.info(`Successfully fetched ${result.rows.length} categories`);
    return res.status(200).json({ categories: result.rows });
  } catch (error) {
    logger.error("Error fetching categories:", error);
    return res.status(500).json({
      error: "Failed to fetch categories",
      details: error.message,
    });
  }
};

/**
 * This handler handles gets a specific category by ID.
 * send GET Request at /api/categories/:categoryId
 * */
const getCategoryHandler = async (req, res) => {
  const { categoryId } = req.params;
  try {
    logger.info(`Fetching category with ID: ${categoryId}`);
    const result = await pool.query("SELECT * FROM categories WHERE id = $1", [
      categoryId,
    ]);

    if (result.rows.length === 0) {
      logger.warn(`Category not found with ID: ${categoryId}`);
      return res.status(404).json({ error: "Category not found" });
    }

    logger.info(`Successfully fetched category: ${categoryId}`);
    return res.status(200).json({ category: result.rows[0] });
  } catch (error) {
    logger.error(`Error fetching category ${categoryId}:`, error);
    return res.status(500).json({
      error: "Failed to fetch category",
      details: error.message,
    });
  }
};

module.exports = {
  getAllCategoriesHandler,
  getCategoryHandler,
};
