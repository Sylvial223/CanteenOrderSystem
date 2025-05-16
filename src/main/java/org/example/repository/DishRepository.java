package org.example.repository;

import org.example.entity.Dish;
import org.example.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishRepository {
    
    public List<Dish> getAllDishes() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT d.*, c.category_name FROM dishes d " +
                    "LEFT JOIN categories c ON d.category_id = c.category_id";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Dish dish = new Dish();
                dish.setDishId(rs.getInt("dish_id"));
                dish.setDishName(rs.getString("dish_name"));
                dish.setPrice(rs.getBigDecimal("price"));
                dish.setDescription(rs.getString("description"));
                dish.setCategoryId(rs.getInt("category_id"));
                dish.setCategoryName(rs.getString("category_name"));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return dishes;
    }
    
    public Dish getDishById(int dishId) {
        String sql = "SELECT d.*, c.category_name FROM dishes d " +
                    "LEFT JOIN categories c ON d.category_id = c.category_id " +
                    "WHERE d.dish_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dishId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Dish dish = new Dish();
                dish.setDishId(rs.getInt("dish_id"));
                dish.setDishName(rs.getString("dish_name"));
                dish.setPrice(rs.getBigDecimal("price"));
                dish.setDescription(rs.getString("description"));
                dish.setCategoryId(rs.getInt("category_id"));
                dish.setCategoryName(rs.getString("category_name"));
                return dish;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean addDish(Dish dish) {
        String sql = "INSERT INTO dishes (dish_name, price, description, category_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dish.getDishName());
            pstmt.setBigDecimal(2, dish.getPrice());
            pstmt.setString(3, dish.getDescription());
            pstmt.setInt(4, dish.getCategoryId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateDish(Dish dish) {
        String sql = "UPDATE dishes SET dish_name = ?, price = ?, description = ?, category_id = ? WHERE dish_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dish.getDishName());
            pstmt.setBigDecimal(2, dish.getPrice());
            pstmt.setString(3, dish.getDescription());
            pstmt.setInt(4, dish.getCategoryId());
            pstmt.setInt(5, dish.getDishId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteDish(int dishId) {
        String sql = "DELETE FROM dishes WHERE dish_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dishId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 