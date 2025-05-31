package com.unipi.prospect.db.commerce;

import com.unipi.prospect.commerce.Order;
import com.unipi.prospect.db.product.ItemDao;

import java.sql.*;
import java.util.ArrayList;

public class OrderDao {
    private final Connection conn;

    public OrderDao() {
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:userData.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insert(Order order) {
        String sqlString = "INSERT INTO Orders (created, status, total, address, username) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setDate(1, order.getCreated());
            psmt.setString(2, order.getStatus());
            psmt.setString(3, String.format("%.2f", order.getTotal()));
            psmt.setString(4, order.getAddress());
            psmt.setString(5, order.getUsername());
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(Order order) {
        String sqlString = "UPDATE Orders SET status = ?, address = ? WHERE orderId = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, order.getStatus());
            psmt.setString(2, order.getAddress());
            psmt.setInt(3, order.getId());
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private ArrayList<Order> selectAllByQuery(String query, String data) {
        ArrayList<Order> orders = new ArrayList<>();
        try{
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, data);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                Date created = rs.getDate("created");
                String status = rs.getString("status");
                float total = rs.getFloat("total");
                String address = rs.getString("address");
                String username = rs.getString("username");
                orders.add(new Order(orderId, created, status, address, username, null));
            }
            rs.close();
            psmt.close();
            for (Order order : orders) {
                order.setItems(new ItemDao().selectItemsByOrderID(order.getId()));
            }
            return orders;
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<Order> selectAllByUsername(String username) {
        String sqlString = "SELECT * FROM Orders WHERE username = ?";
        return selectAllByQuery(sqlString, username);
    }

    public ArrayList<Order> selectAllByStatus(String status) {
        String sqlString = "SELECT * FROM Orders WHERE status = ?";
        return selectAllByQuery(sqlString, status);
    }

    public Order selectByOrderID(int orderId) {
        String sqlString = "SELECT * FROM Orders WHERE orderId = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            ResultSet rs = psmt.executeQuery();
            Order order;
            if (rs.next()) {
                order = new Order(
                        rs.getInt("orderId"),
                        rs.getDate("created"),
                        rs.getString("status"),
                        rs.getString("address"),
                        rs.getString("username"),
                        null
                );
                order.setItems(new ItemDao().selectItemsByOrderID(orderId));
                return order;
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }
}
