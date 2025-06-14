package com.unipi.prospect.db.commerce;

import com.unipi.prospect.commerce.Order;
import com.unipi.prospect.db.DBConnection;
import com.unipi.prospect.db.product.ItemDao;
import com.unipi.prospect.product.Item;

import java.sql.*;
import java.util.ArrayList;

public class OrderDao {
    private final Connection conn;

    public OrderDao() {
        conn = DBConnection.getConnection();
    }

    public boolean insert(Order order) {
        String sqlString = "INSERT INTO Orders (created, status, total, address, username) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement psmt = conn.prepareStatement(sqlString, Statement.RETURN_GENERATED_KEYS);
            psmt.setDate(1, order.getCreated());
            psmt.setString(2, order.getStatus());
            psmt.setString(3, String.format("%.2f", order.getTotal()));
            psmt.setString(4, order.getAddress());
            psmt.setString(5, order.getUsername());
            psmt.executeUpdate();
            int orderid = 0;
            ResultSet rs = psmt.getGeneratedKeys();
            if (rs.next()) {
                orderid = rs.getInt(1);
            }
            rs.close();
            psmt.close();
            for (Item item : order.getItems()) {
                new ItemDao().insert(item, orderid);
            }
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
            psmt.close();
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
            psmt.setInt(1, orderId);
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
                rs.close();
                psmt.close();
                return order;
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public int getCountOfOrdersByStatus(String status) throws SQLException {
        String sqlString = "SELECT count(*) FROM Orders WHERE status = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, status);
            ResultSet rs = psmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            psmt.close();
            return count;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
