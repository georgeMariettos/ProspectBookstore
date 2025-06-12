package com.unipi.prospect.db.product;

import com.unipi.prospect.db.DBConnection;
import com.unipi.prospect.product.Item;

import java.sql.*;
import java.util.ArrayList;

public class ItemDao {
    private final Connection conn;

    public ItemDao() {
        conn = DBConnection.getConnection();
    }

    public boolean insert(Item item, int orderId) {
        String sqlString = "INSERT INTO OrderItems VALUES(?,?,?,?)";
        try {
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setInt(1, orderId);
            psmt.setString(2, item.getIsbn());
            psmt.setInt(3, item.getQuantity());
            psmt.setString(4, String.format("%.2f", item.getPrice()));
            psmt.executeUpdate();
            psmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(Item item, int orderId) {
        String sqlString = "UPDATE OrderItems SET orderId = ?, bookIsbn = ?, quantity = ?, price = ? WHERE orderId = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setInt(1, orderId);
            psmt.setString(2, item.getIsbn());
            psmt.setInt(3, item.getQuantity());
            psmt.setString(4, String.format("%.2f", item.getPrice()));
            psmt.setInt(5, orderId);
            psmt.executeUpdate();
            psmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<Item> selectItemsByOrderID(int orderID) {
        String sqlString = "SELECT * FROM OrderItems WHERE orderId = ?";
        ArrayList<Item> items = new ArrayList<>();
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setInt(1, orderID);
            ResultSet rs = psmt.executeQuery();
            while(rs.next()) {
                String isbn = rs.getString("bookIsbn");
                int quantity = rs.getInt("quantity");
                float price = rs.getFloat("price");
                items.add(new Item(isbn, quantity, price));
            }
            rs.close();
            psmt.close();
            return items;
        } catch (SQLException e) {
            return null;
        }
    }
}
