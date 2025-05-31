package com.unipi.prospect.db.users;

import com.unipi.prospect.users.Customer;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDao implements UserDao<Customer> {
    private final Connection conn;

    public CustomerDao() {
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:userData.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insert(Customer customer) {
        String sqlString = "INSERT INTO Users VALUES(?,?,?,?,?,?)";
        String sqlString2 = "INSERT INTO CustomerAddress VALUES(?,?)";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, customer.getUsername());
            pstmt.setString(2, customer.getPassword());
            pstmt.setString(3, customer.getName());
            pstmt.setString(4, customer.getSurname());
            pstmt.setBoolean(5, customer.isActive());
            pstmt.setString(6, "Customer");
            pstmt.executeUpdate();
            pstmt.close();

            pstmt = conn.prepareStatement(sqlString2);
            pstmt.setString(1, customer.getUsername());
            pstmt.setString(2, customer.getAddress());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean update(Customer customer) {
        String sqlString = "UPDATE Users SET username = ?, password = ?, name = ?, surname = ?, active = ? WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, customer.getUsername());
            pstmt.setString(2, customer.getPassword());
            pstmt.setString(3, customer.getName());
            pstmt.setString(4, customer.getSurname());
            pstmt.setBoolean(5, customer.isActive());
            pstmt.setString(6, customer.getUsername());
            pstmt.executeUpdate();
            pstmt.close();
            updateAddress(customer);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void updateAddress(Customer customer) throws SQLException {
        String sqlString = "UPDATE CustomerAddress SET address = ? WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sqlString);
        pstmt.setString(1, customer.getAddress());
        pstmt.setString(2, customer.getUsername());
        pstmt.executeUpdate();
        pstmt.close();
    }

    @Override
    public boolean delete(Customer customer) {
        String sqlString = "DELETE FROM Users WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1,customer.getUsername());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public ArrayList<Customer> selectAll(){
        String sqlString = "SELECT * FROM users NATURAL JOIN CustomerAddress";
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()){
                String username = rs.getString("username");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                boolean active = rs.getBoolean("active");
                String address = rs.getString("address");
                customers.add(new Customer(username, password, name, surname, active, address));
            }
            return customers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer selectByUsername(String username) throws SQLException {
        String sqlString = "SELECT * FROM users NATURAL JOIN CustomerAddress WHERE username = ?";
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            if (rs.next()){
                return new Customer(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getBoolean("active"),
                        rs.getString("address")
                );
            }
            rs.close();
            stmt.close();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String selectAddressByUsername(String username) {
        String sqlString = "SELECT address FROM CustomerAddress WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            String address = "";
            if(rs.next()){
                address = rs.getString("address");
            }
            rs.close();
            pstmt.close();
            return address;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
