const pool = require('../utils/db');

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
    const result = await pool.query('SELECT * FROM categories ORDER BY category_name');
    return res.status(200).json({ categories: result.rows });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles gets a specific category by ID.
 * send GET Request at /api/categories/:categoryId
 * */
const getCategoryHandler = async (req, res) => {
  const { categoryId } = req.params;
  try {
    const result = await pool.query(
      'SELECT * FROM categories WHERE id = $1',
      [categoryId]
    );
    
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Category not found' });
    }
    
    return res.status(200).json({ category: result.rows[0] });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

module.exports = {
  getAllCategoriesHandler,
  getCategoryHandler
};
