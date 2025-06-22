package com.unipi.prospect.db.product;

import com.unipi.prospect.db.DBConnection;
import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.users.User;

import java.sql.*;
import java.util.ArrayList;

public class BookDao {
    private final Connection conn;

    public BookDao() {
        conn = DBConnection.getConnection();
    }

    public boolean insert(Book book) {
        String sqlString = "INSERT INTO Books VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try{
            if (book.getAuthorUsername() == null || book.getAuthorUsername().isEmpty()) {
                return false; // Author username must not be null or empty
            }
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, book.getIsbn());
            psmt.setString(2, book.getTitle());
            psmt.setString(3, book.getAuthorUsername());
            psmt.setString(4, book.getImageUrl());
            psmt.setString(5, book.getDescription());
            psmt.setString(6, book.getPublisher());
            psmt.setString(7, book.getPublishedDate());
            psmt.setString(8, Integer.toString(book.getPageCount()));
            psmt.setString(9, book.getGenre());
            psmt.setString(10, book.getPreviewLink());
            psmt.setString(11, String.format("%.2f",book.getPrice()));
            psmt.setString(12, Integer.toString(book.getStock()));
            psmt.executeUpdate();
            psmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(Book book) {
        String sqlString = "UPDATE Books SET title = ?, authorUsername = ?, imageUrl = ?, description = ?, publisher = ?, publishedDate = ?, pageCount = ?, genre = ?, previewLink = ?, price = ?, stock = ? WHERE isbn = ?";
        try{
            if (book.getAuthorUsername() == null || book.getAuthorUsername().isEmpty()) {
                return false; // Author username must not be null or empty
            }
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, book.getTitle());
            psmt.setString(2, book.getAuthorUsername());
            psmt.setString(3, book.getImageUrl());
            psmt.setString(4, book.getDescription());
            psmt.setString(5, book.getPublisher());
            psmt.setString(6, book.getPublishedDate());
            psmt.setString(7, Integer.toString(book.getPageCount()));
            psmt.setString(8, book.getGenre());
            psmt.setString(9, book.getPreviewLink());
            psmt.setString(10, String.format("%.2f",book.getPrice()));
            psmt.setString(11, Integer.toString(book.getStock()));
            psmt.setString(12, book.getIsbn());
            psmt.executeUpdate();
            psmt.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public int getBookStock(String isbn) {
        String sqlString = "SELECT stock FROM Books WHERE isbn = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, isbn);
            ResultSet rs = psmt.executeQuery();
            int stock = 0;
            if(rs.next()) {
                stock = rs.getInt("stock");
            }
            psmt.close();
            rs.close();
            return stock;
        } catch (SQLException e) {
            return -1; // Error occurred
        }
    }

    public ArrayList<Book> selectAll() {
        String sqlString = "SELECT * FROM Books";
        return selectByQuery(sqlString);
    }

    public ArrayList<Book> selectByAuthorUsername(String username) {
        String sqlString = "SELECT * FROM Books WHERE authorUsername = ?";
        return selectByQuery(sqlString, username);
    }

    private ArrayList<Book> selectByQuery(String query) {
        return selectByQuery(query, null);
    }

    private ArrayList<Book> selectByQuery(String query, String parameter) {
        ArrayList<Book> books = new ArrayList<>();
        try{
            PreparedStatement psmt = conn.prepareStatement(query);
            if (parameter != null) {
                psmt.setString(1, parameter);
            }
            ResultSet rs = psmt.executeQuery();
            while(rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String authorUsername = rs.getString("authorUsername");
                String imageUrl = rs.getString("imageUrl");
                String description = rs.getString("description");
                String publisher = rs.getString("publisher");
                String publishedDate = rs.getString("publishedDate");
                int pageCount = rs.getInt("pageCount");
                String genre = rs.getString("genre");
                String previewLink = rs.getString("previewLink");
                float price = rs.getFloat("price");
                int stock = rs.getInt("stock");
                books.add(new Book(isbn, title, authorUsername, imageUrl, description, publisher, publishedDate, pageCount, genre, previewLink, price, stock));
            }
            rs.close();
            psmt.close();
            return books;
        } catch (SQLException e) {
            return null;
        }
    }

    public Book selectByIsbn(String isbn) {
        String sqlString = "SELECT * FROM Books WHERE isbn = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, isbn);
            ResultSet rs = psmt.executeQuery();
            Book book = null;
            if(rs.next()) {
                book = new Book(
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("authorUsername"),
                        rs.getString("imageUrl"),
                        rs.getString("description"),
                        rs.getString("publisher"),
                        rs.getString("publishedDate"),
                        rs.getInt("pageCount"),
                        rs.getString("genre"),
                        rs.getString("previewLink"),
                        rs.getFloat("price"),
                        rs.getInt("stock")
                );
            }
            psmt.close();
            rs.close();
            return book;
        } catch (SQLException e) {
            return null;
        }
    }

    public int getCountOfBooks() throws SQLException {
        String sqlString = "SELECT COUNT(*) FROM Books";
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            int count = 0;
            if (rs.next()){
               count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            return count;
        }catch (SQLException e){
            throw  new SQLException(e);
        }
    }

    public ArrayList<Book> getTopBooks() throws SQLException {
        String sqlString = "SELECT isbn, SUM(OrderItems.quantity) as sales from OrderItems join Books on OrderItems.bookIsbn = Books.isbn group by isbn order by sales limit 10";
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            ArrayList<String> isbns = new ArrayList<String>();
            while (rs.next()){
                isbns.add(rs.getString("isbn"));
            }
            rs.close();
            stmt.close();
            ArrayList<Book> books = new ArrayList<Book>();
            for(String isbn : isbns){
                books.add(selectByIsbn(isbn));
            }
            return books;
        }catch (SQLException e){
            throw  new SQLException(e);
        }
    }

    public int getBookSales(String isbn) throws SQLException {
        String sqlString = "SELECT SUM(OrderItems.quantity) as sales from OrderItems join Books on OrderItems.bookIsbn = Books.isbn where isbn = ?";
        try{
            PreparedStatement stmt = conn.prepareStatement(sqlString);
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            int sales = 0;
            if (rs.next()){
                sales = rs.getInt("sales");
            }
            rs.close();
            stmt.close();
            return sales;
        }catch (SQLException e){
            throw  new SQLException(e);
        }
    }
}