package org.example.repository;

import org.example.entity.Order;
import org.example.entity.OrderItem;
import org.example.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username, t.table_number FROM orders o " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTableId(rs.getInt("table_id"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setUsername(rs.getString("username"));
                order.setTableNumber(rs.getString("table_number"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    public Order getOrderById(int orderId) {
        String sql = "SELECT o.*, u.username, t.table_number FROM orders o " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.order_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTableId(rs.getInt("table_id"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setUsername(rs.getString("username"));
                order.setTableNumber(rs.getString("table_number"));
                
                // 获取订单项
                order.setItems(getOrderItems(orderId));
                
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, d.dish_name, d.price FROM order_items oi " +
                    "LEFT JOIN dishes d ON oi.dish_id = d.dish_id " +
                    "WHERE oi.order_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(rs.getInt("item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setDishId(rs.getInt("dish_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setDishName(rs.getString("dish_name"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, orderId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username, t.table_number FROM orders o " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTableId(rs.getInt("table_id"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setUsername(rs.getString("username"));
                order.setTableNumber(rs.getString("table_number"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }

    public boolean createOrder(Order order) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 插入订单
            String orderSql = "INSERT INTO orders (user_id, table_id, total_amount, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getUserId());
                pstmt.setInt(2, order.getTableId());
                pstmt.setBigDecimal(3, order.getTotalAmount());
                pstmt.setString(4, order.getStatus());
                
                if (pstmt.executeUpdate() > 0) {
                    // 获取生成的订单ID
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        int orderId = rs.getInt(1);
                        
                        // 插入订单项
                        String itemSql = "INSERT INTO order_items (order_id, dish_id, quantity, price) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                            for (OrderItem item : order.getItems()) {
                                itemStmt.setInt(1, orderId);
                                itemStmt.setInt(2, item.getDishId());
                                itemStmt.setInt(3, item.getQuantity());
                                itemStmt.setBigDecimal(4, item.getPrice());
                                itemStmt.addBatch();
                            }
                            itemStmt.executeBatch();
                        }
                        
                        conn.commit();
                        return true;
                    }
                }
            }
            
            conn.rollback();
            return false;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 