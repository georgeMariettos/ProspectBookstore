package com.unipi.prospect.db.communication;

import com.unipi.prospect.communication.Ticket;

import java.sql.*;
import java.util.ArrayList;

public class TicketDao {
    private final Connection conn;

    public TicketDao() {
        try{
            conn = DriverManager.getConnection("jdbc:sqlite:userData.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insert(Ticket ticket){
        String sqlString = "INSERT INTO Tickets (comment, username, creationDate, status) VALUES(?,?,?,?)";
        try {
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, ticket.getComment());
            psmt.setDate(2, ticket.getCreationDate());
            psmt.setString(3, ticket.getStatus());
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(Ticket ticket){
        String sqlString = "UPDATE Tickets SET comment=?, status=? WHERE ticketID=?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setString(1, ticket.getComment());
            psmt.setString(2, ticket.getStatus());
            psmt.setInt(3, ticket.getId());
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<Ticket> selectAllTicketsByQuery(String query, String data){
        ArrayList<Ticket> tickets = new ArrayList<>();
        try{
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, data);
            ResultSet rs = psmt.executeQuery();
            while(rs.next()){
                int id = rs.getInt("ticketID");
                String comment = rs.getString("comment");
                String username = rs.getString("username");
                Date creationDate = rs.getDate("creationDate");
                String status = rs.getString("status");
                tickets.add(new Ticket(id, comment, username, creationDate, status));
            }
            return tickets;
        } catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<Ticket> selectAllTicketsByStatus(String status){
        String sqlString = "SELECT * FROM Tickets where status = ?";
        return selectAllTicketsByQuery(sqlString, status);
    }

    public  ArrayList<Ticket> selectAllTicketsByUserName(String username){
        String sqlString = "SELECT * FROM Tickets where username = ?";
        return selectAllTicketsByQuery(sqlString, username);
    }

    public Ticket selectTicketById(int id){
        String sqlString = "SELECT * FROM Tickets where ticketID = ?";
        try{
            PreparedStatement psmt = conn.prepareStatement(sqlString);
            psmt.setInt(1, id);
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
                int ticketID = rs.getInt("ticketID");
                String comment = rs.getString("comment");
                String username = rs.getString("username");
                Date creationDate = rs.getDate("creationDate");
                String status = rs.getString("status");
                return new Ticket(id, comment, username, creationDate, status);
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }
}
