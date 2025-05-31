package com.unipi.prospect.db.users;

import com.unipi.prospect.users.Admin;

import java.sql.*;
import java.util.ArrayList;

public class AdminDao implements UserDao<Admin>{
    private final Connection conn;

    public AdminDao() {
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:userData.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insert(Admin admin) {
        String sqlString = "INSERT INTO Users VALUES(?,?,?,?,?,?)";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getPassword());
            pstmt.setString(3, admin.getName());
            pstmt.setString(4, admin.getSurname());
            pstmt.setBoolean(5, admin.isActive());
            pstmt.setString(6, "Admin");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean update(Admin admin) {
        String sqlString = "UPDATE Users SET username = ?, password = ?, name = ?, surname = ?, active = ? WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, admin.getUsername());
            pstmt.setString(2, admin.getPassword());
            pstmt.setString(3, admin.getName());
            pstmt.setString(4, admin.getSurname());
            pstmt.setBoolean(5, admin.isActive());
            pstmt.setString(6, admin.getUsername());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean delete(Admin admin) {
        String sqlString = "DELETE FROM Users WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1,admin.getUsername());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public ArrayList<Admin> selectAll(){
        String sqlString = "SELECT * FROM users WHERE role = 'Admin'";
        ArrayList<Admin> admins = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()){
                String username = rs.getString("username");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                boolean active = rs.getBoolean("active");
                admins.add(new Admin(username, password, name, surname, active));
            }
            return admins;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Admin selectByUsername(String username){
        String sqlString = "SELECT * FROM Users WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return  new Admin(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getBoolean("active"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
