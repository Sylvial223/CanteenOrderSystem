package org.example.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private int tableId;
    private Timestamp orderTime;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItem> items;
    private String username; // 用于显示
    private String tableNumber; // 用于显示

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int orderId, int userId, int tableId, Timestamp orderTime, BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.tableId = tableId;
        this.orderTime = orderTime;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    @Override
    public String toString() {
        return String.format("订单号: %d, 金额: ￥%.2f, 状态: %s", orderId, totalAmount, status);
    }
} 