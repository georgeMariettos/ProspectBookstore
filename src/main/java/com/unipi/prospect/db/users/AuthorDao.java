package com.unipi.prospect.db.users;

import com.unipi.prospect.users.Author;

import java.sql.*;
import java.util.ArrayList;

public class AuthorDao implements UserDao<Author> {
    private final Connection conn;

    public AuthorDao() {
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:userData.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insert(Author author) {
        String sqlString = "INSERT INTO Users VALUES(?,?,?,?,?,?)";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, author.getUsername());
            pstmt.setString(2, author.getPassword());
            pstmt.setString(3, author.getName());
            pstmt.setString(4, author.getSurname());
            pstmt.setBoolean(5, author.isActive());
            pstmt.setString(6, "Author");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean update(Author author) {
        String sqlString = "UPDATE Users SET username = ?, password = ?, name = ?, surname = ?, active = ? WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, author.getUsername());
            pstmt.setString(2, author.getPassword());
            pstmt.setString(3, author.getName());
            pstmt.setString(4, author.getSurname());
            pstmt.setBoolean(5, author.isActive());
            pstmt.setString(6, author.getUsername());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean delete(Author author) {
        String sqlString = "DELETE FROM Users WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1,author.getUsername());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public ArrayList<Author> selectAll(){
        String sqlString = "SELECT * FROM users WHERE role = 'Author'";
        ArrayList<Author> authors = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            while (rs.next()){
                String username = rs.getString("username");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                boolean active = rs.getBoolean("active");
                authors.add(new Author(username, password, name, surname, active));
            }
            return authors;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Author selectByUsername(String username){
        String sqlString = "SELECT * FROM Users WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return  new Author(
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
