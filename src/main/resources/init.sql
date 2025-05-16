-- 创建数据库
CREATE DATABASE IF NOT EXISTS canteen;
USE canteen;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 菜系表
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL
);

-- 菜品表
CREATE TABLE IF NOT EXISTS dishes (
    dish_id INT AUTO_INCREMENT PRIMARY KEY,
    dish_name VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    image_url VARCHAR(255),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- 桌台表
CREATE TABLE IF NOT EXISTS tables (
    table_id INT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT '空闲'
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    table_id INT,
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2),
    status VARCHAR(20) DEFAULT '待处理',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (table_id) REFERENCES tables(table_id)
);

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    dish_id INT,
    quantity INT,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (dish_id) REFERENCES dishes(dish_id)
);

-- 插入测试数据
-- 插入管理员和测试用户
INSERT INTO users (username, password, role) VALUES 
('admin', '123456', 'admin'),
('user1', '111111', 'user');

-- 插入测试菜系
INSERT INTO categories (category_name) VALUES 
('川菜'),
('粤菜'),
('湘菜'),
('鲁菜');

-- 插入测试菜品
INSERT INTO dishes (dish_name, price, description, category_id) VALUES 
('宫保鸡丁', 28.00, '经典川菜', 1),
('麻婆豆腐', 22.00, '麻辣鲜香', 1),
('白切鸡', 38.00, '鲜嫩多汁', 2),
('剁椒鱼头', 58.00, '湘菜代表', 3);

-- 插入测试桌台
INSERT INTO tables (table_number, status) VALUES 
('A1', '空闲'),
('A2', '空闲'),
('B1', '空闲'),
('B2', '空闲'); 