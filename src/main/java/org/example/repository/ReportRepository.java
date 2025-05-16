package org.example.repository;

import org.example.util.DatabaseUtil;
import java.math.BigDecimal;
import java.sql.*;

public class ReportRepository {
    public static class ReportResult {
        public int orderCount;
        public BigDecimal totalAmount;
        public ReportResult(int orderCount, BigDecimal totalAmount) {
            this.orderCount = orderCount;
            this.totalAmount = totalAmount;
        }
    }

    // 日结账
    public ReportResult getDailyReport(Date date) {
        String sql = "SELECT COUNT(*) AS order_count, SUM(total_amount) AS total_amount " +
                "FROM orders WHERE status IN ('已支付', '已完成') AND DATE(order_time) = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ReportResult(rs.getInt("order_count"), rs.getBigDecimal("total_amount") == null ? BigDecimal.ZERO : rs.getBigDecimal("total_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ReportResult(0, BigDecimal.ZERO);
    }

    // 月结账
    public ReportResult getMonthlyReport(int year, int month) {
        String sql = "SELECT COUNT(*) AS order_count, SUM(total_amount) AS total_amount " +
                "FROM orders WHERE status IN ('已支付', '已完成') AND YEAR(order_time) = ? AND MONTH(order_time) = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ReportResult(rs.getInt("order_count"), rs.getBigDecimal("total_amount") == null ? BigDecimal.ZERO : rs.getBigDecimal("total_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ReportResult(0, BigDecimal.ZERO);
    }

    // 年结账
    public ReportResult getYearlyReport(int year) {
        String sql = "SELECT COUNT(*) AS order_count, SUM(total_amount) AS total_amount " +
                "FROM orders WHERE status IN ('已支付', '已完成') AND YEAR(order_time) = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ReportResult(rs.getInt("order_count"), rs.getBigDecimal("total_amount") == null ? BigDecimal.ZERO : rs.getBigDecimal("total_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ReportResult(0, BigDecimal.ZERO);
    }
} 