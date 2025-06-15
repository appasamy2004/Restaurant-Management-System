package com.restaurant.dao;

import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public int createOrder(Order order, List<OrderItem> items) {
        String orderSql = "INSERT INTO orders (customer_name, total_amount) VALUES (?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
        int orderId = -1;

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert order
            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setString(1, order.getCustomerName());
                orderStmt.setDouble(2, order.getTotalAmount());

                int affectedRows = orderStmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return -1;
                }

                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        return -1;
                    }
                }
            }

            // Insert order items
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                for (OrderItem item : items) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.getItemId());
                    itemStmt.setInt(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, mi.name, mi.description FROM order_items oi " +
                "JOIN menu_items mi ON oi.item_id = mi.id " +
                "WHERE oi.order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setItemId(rs.getInt("item_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));

                    MenuItem menuItem = new MenuItem();
                    menuItem.setName(rs.getString("name"));
                    menuItem.setDescription(rs.getString("description"));
                    item.setMenuItem(menuItem);

                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}