package com.unipi.prospect.db.users;

import com.unipi.prospect.db.DBConnection;
import com.unipi.prospect.users.Admin;
import com.unipi.prospect.users.Author;
import com.unipi.prospect.users.Customer;
import com.unipi.prospect.users.User;

import java.sql.*;
import java.util.ArrayList;

public class UserDAO {
    private final Connection conn;

    public UserDAO() {
        conn = DBConnection.getConnection();
    }

    public User authenticate(String username, String password, String role) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null; // Invalid credentials
        }
        String sqlString = "SELECT * FROM Users WHERE username = ? AND password = ? AND role = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                boolean active = rs.getBoolean("active");
                if (role.equalsIgnoreCase("admin")) {
                    rs.close();
                    pstmt.close();
                    return new Admin(username, password, name, surname, active);
                } else if (role.equalsIgnoreCase("customer")) {
                    String address = selectAddressByUsername(username);
                    rs.close();
                    pstmt.close();
                    return new Customer(username, password, name, surname, active, address);
                } else if (role.equalsIgnoreCase("author")) {
                    rs.close();
                    pstmt.close();
                    return new Author(username, password, name, surname, active);
                } else {
                    rs.close();
                    pstmt.close();
                    throw new IllegalArgumentException("Invalid role: " + role);
                }
            }
            rs.close();
            pstmt.close();
            return null; // No user found with matching credentials
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean insert(User user) {
        String role = user.getClass().getSimpleName().toLowerCase();
        // first letter needs to be uppercase for the database
        role = role.substring(0, 1).toUpperCase() + role.substring(1);
        String sqlString = "INSERT INTO Users VALUES(?,?,?,?,?,?)";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getSurname());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setString(6, role);
            pstmt.executeUpdate();
            pstmt.close();
            if (user instanceof Customer){
                String sqlString2 = "INSERT INTO CustomerAddress VALUES(?,?)";
                PreparedStatement pstmt2 = conn.prepareStatement(sqlString2);
                pstmt2.setString(1, user.getUsername());
                pstmt2.setString(2, ((Customer) user).getAddress());
                pstmt2.executeUpdate();
                pstmt2.close();
                return true;
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(User user) {
        String sqlString = "UPDATE Users SET password = ?, name = ?, surname = ?, active = ? WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getSurname());
            pstmt.setBoolean(4, user.isActive());
            pstmt.setString(5, user.getUsername());
            pstmt.executeUpdate();

            if (user instanceof Customer) {
                String sqlString2 = "UPDATE CustomerAddress SET address = ? WHERE username = ?";
                pstmt = conn.prepareStatement(sqlString2);
                pstmt.setString(1, ((Customer) user).getAddress());
                pstmt.setString(2, user.getUsername());
                pstmt.executeUpdate();
                pstmt.close();
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(User user) {
        String sqlString = "DELETE FROM Users WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1,user.getUsername());
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<User> selectAll(String role){
        String sqlString = "SELECT * FROM users WHERE role = ?";
        ArrayList<User> users = new ArrayList<>();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                boolean active = rs.getBoolean("active");

                if (role.equalsIgnoreCase("Admin")) {
                    users.add(new Admin(username, password, name, surname, active));
                } else if (role.equalsIgnoreCase("Customer")) {
                    String sqlString2 = "SELECT * FROM CustomerAddress WHERE username = ?";
                    PreparedStatement pstmt2 = conn.prepareStatement(sqlString2);
                    pstmt2.setString(1, username);
                    ResultSet rs2 = pstmt2.executeQuery();
                    while (rs2.next()){
                        String address = rs2.getString("address");
                        users.add(new Customer(username, password, name, surname, active, address));
                    }
                } else if (role.equalsIgnoreCase("Author")) {
                    users.add(new Author(username, password, name, surname, active));
                } else {
                    throw new IllegalArgumentException("Invalid role: " + role);
                }
            }
            rs.close();
            pstmt.close();
            return users;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User selectByUsername(String username, String role){

        String sqlString = "SELECT * FROM Users WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            String password = "";
            String name = "";
            String surname = "";
            boolean active = false;
            if (rs.next()){
                password = rs.getString("password");
                name = rs.getString("name");
                surname = rs.getString("surname");
                active = rs.getBoolean("active");
            }
            rs.close();
            pstmt.close();
            if (role.equalsIgnoreCase("Customer")) {
                String sqlString2 = "SELECT * FROM CustomerAddress WHERE username = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(sqlString2);
                pstmt2.setString(1, username);
                ResultSet rs2 = pstmt2.executeQuery();
                String address = "";
                if (rs2.next()) {
                    address = rs2.getString("address");
                }
                rs2.close();
                pstmt2.close();
                return new Customer(username, password, name, surname, active, address);
            } else if (role.equalsIgnoreCase("Author")) {
                return new Author(
                        username,
                        password,
                        name,
                        surname,
                        active
                );
            } else if (role.equalsIgnoreCase("Admin")) {
                return new Admin(
                        username,
                        password,
                        name,
                        surname,
                        active
                );
            } else {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public int getCountOfUsers() throws SQLException {
        String sqlString = "SELECT COUNT(*) FROM users";
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            int count = 0;
            if (rs.next()){
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            return  count;
        }catch (SQLException e){
            throw new SQLException((e));
        }
    }
}
