package com.unipi.prospect.db.users;

import com.unipi.prospect.db.DBConnection;
import com.unipi.prospect.users.Admin;
import com.unipi.prospect.users.Author;
import com.unipi.prospect.users.Customer;
import com.unipi.prospect.users.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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
        String validate = "SELECT password, salt FROM Users WHERE username = ? AND role = ?";
        byte[] salt;
        String storedHash;
        try {
            PreparedStatement stmt = conn.prepareStatement(validate);
            stmt.setString(1, username);
            stmt.setString(2, role);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    storedHash = rs.getString("password");
                    salt = rs.getBytes("salt");
                    rs.close();
                } else {
                    rs.close();
                    return null; // User not found
                }
            }


            String hashedPassword = hashPassword(password, salt);
            if (!hashedPassword.equals(storedHash)) {
                return null; // Password does not match
            }

        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String sqlString = "SELECT * FROM Users WHERE username = ? AND role = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, username);
            pstmt.setString(2, role);
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
        byte[] salt;
        try {
            salt = generateSalt();
            String hashedPassword = hashPassword(user.getPassword(), salt);
            user.setPassword(hashedPassword);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            System.err.println("Error generating password hash: " + e.getMessage());
            e.printStackTrace();
            return false; // User addition failed
        }
        String role = user.getClass().getSimpleName().toLowerCase();
        // first letter needs to be uppercase for the database
        role = role.substring(0, 1).toUpperCase() + role.substring(1);
        String sqlString = "INSERT INTO Users VALUES(?,?,?,?,?,?, ?)";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getSurname());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setString(6, role);
            pstmt.setBytes(7, salt);
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
        byte[] salt;
        try {
            salt = generateSalt();
            String hashedPassword = hashPassword(user.getPassword(), salt);
            user.setPassword(hashedPassword);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            System.err.println("Error generating password hash: " + e.getMessage());
            e.printStackTrace();
            return false; // User addition failed
        }
        String sqlString = "UPDATE Users SET password = ?, name = ?, surname = ?, active = ?, salt = ? WHERE username = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlString);
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getSurname());
            pstmt.setBoolean(4, user.isActive());
            pstmt.setString(5, user.getUsername());
            pstmt.setBytes(6, salt);
            pstmt.executeUpdate();
            pstmt.close();

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
                    rs2.close();
                    pstmt2.close();
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
            boolean found = false;
            User user = null;
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
                found = true;
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
                if (found) {
                    user = new Customer(username, password, name, surname, active, address);
                }
            } else if (role.equalsIgnoreCase("Author")) {
                if (found){
                    user = new Author(
                            username,
                            password,
                            name,
                            surname,
                            active
                    );
                }
            } else if (role.equalsIgnoreCase("Admin")) {
                if (found){
                    user = new Admin(
                            username,
                            password,
                            name,
                            surname,
                            active
                    );
                }
            } else {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
            return user;
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


    private byte[] generateSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        return bytesToHex(md.digest(password.getBytes()));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Get all the users and update their password by hashing it with the salt
    public void updateAllUsersPasswords() {
        String sql = "SELECT username, password FROM Users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                byte[] salt = generateSalt();

                String hashedPassword = hashPassword(password, salt);

                String updateSql = "UPDATE users SET password = ?, salt = ? WHERE username = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, hashedPassword);
                    updateStmt.setBytes(2, salt);
                    updateStmt.setString(3, username);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
}
