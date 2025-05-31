package com.unipi.prospect.db.communication;

import com.unipi.prospect.communication.Comment;

import java.sql.*;
import java.util.ArrayList;

public class CommentDao {
    private final Connection conn;

    public CommentDao() {
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:userData.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insert(Comment c){
        String sqlString = "INSERT INTO Comments (username, dateCreated, content, bookIsbn, replyTo, rating) VALUES(?,?,?,?,?,?)";
        try {
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, c.getUsername());
            psmt.setDate(2, c.getDateCreated());
            psmt.setString(3, c.getContent());
            psmt.setString(4, c.getBookIsbn());
            psmt.setInt(5, c.getReplyTo());
            psmt.setInt(6, c.getRating());
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(Comment c){
        String sqlString = "UPDATE Comments SET username = ?, content = ?, rating =? WHERE commentID = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, c.getUsername());
            psmt.setString(2,c.getContent());
            psmt.setInt(3, c.getRating());
            psmt.setInt(4, c.getId());
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private ArrayList<Comment> selectByQuery(String query, String data, int id){
        ArrayList<Comment> comments = new ArrayList<>();
        try{
            PreparedStatement psmt = conn.prepareStatement(query);
            if(data != null){
                psmt.setString(1, data);
            }else if(id>=0){
                psmt.setInt(1, id);
            }else{
                throw new RuntimeException("if data is null id must be more than -1\nif id is less than 0 data must be not null");
            }
            ResultSet rs = psmt.executeQuery();
            while(rs.next()) {
                int commentID = rs.getInt("commentID");
                String username = rs.getString("username");
                Date dateCreated = rs.getDate("dateCreated");
                String content = rs.getString("content");
                String bookIsbn = rs.getString("bookIsbn");
                int replyTo = rs.getInt("replyTo");
                int rating = rs.getInt("rating");
                comments.add(new Comment(commentID, username, dateCreated, content, bookIsbn, replyTo, rating));
            }
            return comments;
        } catch (SQLException e) {
            return null;
        }
    }

    private ArrayList<Comment> selectByQuery(String query, String data){
        return selectByQuery(query, data, -1);
    }

    private ArrayList<Comment> selectByQuery(String query, int id){
        return selectByQuery(query, null, id);
    }

    public ArrayList<Comment> selectByBookIsbn(String isbn){
        String sqlString = "SELECT * FROM Comments WHERE bookIsbn = ?";
        return selectByQuery(sqlString, isbn);
    }

    public ArrayList<Comment> selectByUsername(String username){
        String sqlString = "SELECT * FROM Comments WHERE username = ?";
        return selectByQuery(sqlString, username);
    }

    public ArrayList<Comment> selectByReplyToID(int id){
        String sqlString = "SELECT * FROM Comments WHERE replyTo = ?";
        return selectByQuery(sqlString, id);
    }

    public Comment selectByID(int id){
        String sqlString = "SELECT * FROM Comments WHERE commentID = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setInt(1, id);
            ResultSet rs = psmt.executeQuery();
            if(rs.next()) {
                int commentID = rs.getInt("commentID");
                String username = rs.getString("username");
                Date dateCreated = rs.getDate("dateCreated");
                String content = rs.getString("content");
                String bookIsbn = rs.getString("bookIsbn");
                int replyTo = rs.getInt("replyTo");
                int rating = rs.getInt("rating");
                return new Comment(commentID, username, dateCreated, content, bookIsbn, replyTo, rating);
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean delete(Comment c){
        String sqlString = "DELETE FROM Comments WHERE commentID = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setInt(1, c.getId());
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


}
