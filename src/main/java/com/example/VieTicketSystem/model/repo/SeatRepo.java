package com.example.VieTicketSystem.model.repo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.VieTicketSystem.model.entity.Seat;
import org.springframework.stereotype.Repository;

@Repository
public class SeatRepo {

    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Seat WHERE seat_id = ?";
    private static final String SELECT_BY_EVENT_ID_SQL = """
            SELECT * FROM Seat
            JOIN VieTicket1.Row R ON R.row_id = Seat.row_id
            JOIN VieTicket1.Area A ON R.area_id = A.area_id
            WHERE event_id = 5""";
    private static final String SELECT_BY_ROW_ID_SQL = "SELECT * FROM Seat WHERE row_id = ?";
    private final RowRepo rowRepo;

    public SeatRepo(RowRepo rowRepo) {
        this.rowRepo = rowRepo;
    }

    public Seat findById(int id) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection connection = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Seat seat = null;
        if (rs.next()) {
            seat = new Seat();
            seat.setSeatId(rs.getInt("seat_id"));
            seat.setNumber(rs.getString("number"));
            seat.setTicketPrice(rs.getFloat("ticket_price"));
            seat.setTaken(rs.getBoolean("is_taken"));
            seat.setRow(rowRepo.findById(rs.getInt("row_id")));
        }
        rs.close();
        ps.close();
        connection.close();
        return seat;
    }

    public void addSeat(String seatNumber, String ticketPrice, int rowId)
            throws ClassNotFoundException, SQLException {

        Class.forName(Baseconnection.nameClass);
        Connection connection = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Seat (number, ticket_price, is_taken, row_id) VALUES (?, ?, ?, ?)");

        ps.setString(1, seatNumber);
        ps.setFloat(2, Float.parseFloat(ticketPrice));
        ps.setBoolean(3, false);
        ps.setInt(4, rowId);
        ps.executeUpdate();
        ps.close();
    }

    public List<Seat> findByEventId(int eventId) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection connection = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_EVENT_ID_SQL);
        ResultSet rs = ps.executeQuery();
        List<Seat> seats = new ArrayList<>();
        while (rs.next()) {
            Seat seat = new Seat();
            seat.setSeatId(rs.getInt("seat_id"));
            seat.setNumber(rs.getString("number"));
            seat.setTicketPrice(rs.getFloat("ticket_price"));
            seat.setTaken(rs.getBoolean("is_taken"));
            seat.setRow(rowRepo.findById(rs.getInt("row_id")));
            seats.add(seat);
        }
        rs.close();
        ps.close();
        connection.close();
        return seats;
    }

    public List<Seat> findByAreaId(int areaId) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection connection = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_ROW_ID_SQL);
        ResultSet rs = ps.executeQuery();
        List<Seat> seats = new ArrayList<>();
        while (rs.next()) {
            Seat seat = new Seat();
            seat.setSeatId(rs.getInt("seat_id"));
            seat.setNumber(rs.getString("number"));
            seat.setTicketPrice(rs.getFloat("ticket_price"));
            seat.setTaken(rs.getBoolean("is_taken"));
            seat.setRow(rowRepo.findById(rs.getInt("row_id")));
            seats.add(seat);
        }
        rs.close();
        ps.close();
        connection.close();
        return seats;
    }
}