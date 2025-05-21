const pool = require('../utils/db');

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
    const result = await pool.query('SELECT * FROM products');
    return res.status(200).json({ products: result.rows });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles gets a specific product by ID.
 * send GET Request at /api/products/:productId
 * */
const getProductHandler = async (req, res) => {
  const { productId } = req.params;
  try {
    const result = await pool.query(
      'SELECT * FROM products WHERE id = $1',
      [productId]
    );
    
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Product not found' });
    }
    
    return res.status(200).json({ product: result.rows[0] });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

/**
 * This handler handles searching products by name.
 * send GET Request at /api/products/search?query=searchTerm
 * */
const searchProductsHandler = async (req, res) => {
  const { query } = req.query;

  if (!query) {
    return res.status(400).json({ error: 'Search query is required' });
    }

  try {
    const result = await pool.query(
      `SELECT * FROM products 
       WHERE LOWER(name) LIKE LOWER($1) 
       LIMIT 10`,
      [`%${query}%`]
    );

    return res.status(200).json({ products: result.rows });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
};

module.exports = {
  getAllProductsHandler,
  getProductHandler,
  searchProductsHandler
};
