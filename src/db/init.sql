-- Drop existing tables if they exist
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    category_image TEXT,
    description TEXT
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2) NOT NULL,
    image TEXT,
    colors JSONB,
    company VARCHAR(255),
    description TEXT,
    category_id UUID REFERENCES categories(id),
    is_shipping_available BOOLEAN DEFAULT false,
    stock INTEGER DEFAULT 0,
    review_count INTEGER DEFAULT 0,
    stars DECIMAL(2,1) DEFAULT 0,
    featured BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert categories
INSERT INTO categories (id, category_name, category_image, description) VALUES
('35abdf47-0dae-40fc-b201-a981e9daa3d4', 'laptop', 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908106/redmi-book-15_ksizgp.jpg', ''),
('fab4d8a9-84cd-49bb-9479-ff73e5bcf0fc', 'tv', 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683918874/oneplus-55U1S_pl3nko.jpg', ''),
('a9c05f11-bb6a-4501-9390-3201ed9f9448', 'smartwatch', 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683911006/apple-watch-ultra_ony1kc.jpg', ''),
('a71bd701-eca8-41a8-a385-e1ec91a03697', 'earphone', 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683955385/oneplus-nord-buds_b9yphw.jpg', ''),
('16080c75-5573-4626-9b89-37c670907c02', 'mobile', 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683957585/oneplus-nord-CE-3-lite_weksou.jpg', '');

-- Insert products
INSERT INTO products (id, name, price, original_price, image, colors, company, description, category_id, is_shipping_available, stock, review_count, stars, featured) VALUES
('9eb0c25b-447c-4723-9ce4-639527debb68', 'mi book 15', 31990, 51999, 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908106/redmi-book-15_ksizgp.jpg', 
'[{"color": "#0000ff", "colorQuantity": 10}, {"color": "#00ff00", "colorQuantity": 6}, {"color": "#ff0000", "colorQuantity": 9}]',
'redmi', 'For this model, screen size is 39.62 cm and hard disk size is 256 GB. CPU Model is i3. RAM Memory Installed Size is 8 GB. Operating System is Windows 10 Home. Special Feature includes Anti Glare Screen, Light Weight, Thin. Graphics Card is Integrated',
'35abdf47-0dae-40fc-b201-a981e9daa3d4', true, 25, 418, 3.7, false),

('eb7db2dd-231a-47f5-b803-4415c2150efa', 'mi notebook pro', 54499, 74999, 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908295/mi-notebook-pro_hi4vih.jpg',
'[{"color": "#00ff00", "colorQuantity": 8}, {"color": "#000", "colorQuantity": 2}]',
'redmi', 'For this model, screen size is 14 Inches and hard disk size is 512 GB. CPU Model is i5. RAM Memory Installed Size is 16 GB. Operating System is Windows 11. Special Feature includes Fingerprint Reader, Backlit Keyboard. Graphics Card is Integrated',
'35abdf47-0dae-40fc-b201-a981e9daa3d4', false, 10, 1805, 4.3, false),

('618811f6-3670-45ce-add5-83cc9e616c37', 'mi 5A', 13499, 24999, 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683908781/mi-5a_spazxt.jpg',
'[{"color": "#0000ff", "colorQuantity": 9}, {"color": "#00ff00", "colorQuantity": 2}, {"color": "#ff0000", "colorQuantity": 7}, {"color": "#ffb900", "colorQuantity": 9}]',
'redmi', 'For this model, screen size is 32 Inches. Product Dimensions is (19D x 71.5W x 47H) cm. Operating System is Windows 11. Mounting Hardware includes 1 LED TV, 2 Table Stand Base, 1 User Manual, 1 Remote Control, 4 screws, 2 x AAA Batteries. Resolution is 720p with the refresh rate is 60Hz.',
'fab4d8a9-84cd-49bb-9479-ff73e5bcf0fc', true, 27, 35573, 4.2, false),

('75a9fabb-2f56-4e29-9f56-63e936e99f39', 'mi horizon', 21399, 29999, 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909215/mi-horizon_n4anpx.jpg',
'[{"color": "#000", "colorQuantity": 4}]',
'redmi', 'For this model, screen size is 40 Inches. Product Dimensions is (8.7D x 89.2W x 55.9H) cm. Operating System is Windows 11. Mounting Hardware includes 1 LED TV, 2 Table Stand Base, 1 User Manual, 1 Remote Control, 4 screws. Resolution is 1080p with the refresh rate is 60Hz.',
'fab4d8a9-84cd-49bb-9479-ff73e5bcf0fc', false, 4, 35573, 4.1, false),

('5396c054-df35-4011-91e4-df853ea7c57d', 'mi sonicBass', 1299, 1599, 'https://res.cloudinary.com/dtbd1y4en/image/upload/v1683909289/mi-sonicBass_zgkhiw.jpg',
'[{"color": "#0000ff", "colorQuantity": 5}, {"color": "#000", "colorQuantity": 7}, {"color": "#ffb900", "colorQuantity": 3}]',
'redmi', 'This is a "In Ear" product and wireless and equipped with 9.2 mm dynamic drivers. Features include Dual-Mic Noise Cancellation, Dual Pairing Multi-Point Connection with Flexi Arc and Skin-friendly Design.',
'a71bd701-eca8-41a8-a385-e1ec91a03697', true, 15, 8238, 2.5, false);

-- Continue with more products... 