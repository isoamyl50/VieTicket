package com.example.VieTicketSystem.model.repo;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.example.VieTicketSystem.model.entity.Area;
import com.example.VieTicketSystem.model.entity.Event;
import com.example.VieTicketSystem.model.entity.EventStatistics;
import com.example.VieTicketSystem.model.entity.SeatMap;

@Repository
public class EventRepo {

    static {
        try {
            Class.forName(Baseconnection.nameClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL driver", e);
        }
    }

    @Autowired
    OrganizerRepo organizerRepo = new OrganizerRepo();

    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Event WHERE event_id = ?";

    public Event findById(int id) throws Exception {
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                Event event = null;
                if (rs.next()) {
                    event = new Event();
                    event.setEventId(rs.getInt("event_id"));
                    event.setName(rs.getString("name"));
                    event.setDescription(rs.getString("description"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp startTimestamp = rs.getTimestamp("start_date");
                    if (startTimestamp != null) {
                        event.setStartDate(startTimestamp.toLocalDateTime());
                    }

                    event.setLocation(rs.getString("location"));
                    event.setType(rs.getString("type"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp ticketSaleTimestamp = rs.getTimestamp("ticket_sale_date");
                    if (ticketSaleTimestamp != null) {
                        event.setTicketSaleDate(ticketSaleTimestamp.toLocalDateTime());
                    }

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp endTimestamp = rs.getTimestamp("end_date");
                    if (endTimestamp != null) {
                        event.setEndDate(endTimestamp.toLocalDateTime());
                    }

                    event.setPoster(rs.getString("poster"));
                    event.setBanner(rs.getString("banner"));
                    event.setApproved(rs.getInt("is_approve"));
                    event.setOrganizer(organizerRepo.findById(rs.getInt("organizer_id")));
                }
                return event;
            }
        }
    }

    public List<Event> findUpcomingByOrganizerId(int organizerId) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement("SELECT * FROM Event WHERE organizer_id = ? AND end_date >= ?");) {

            statement.setInt(1, organizerId);
            statement.setDate(2, new Date(System.currentTimeMillis()));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Event event = new Event();
                    event.setEventId(resultSet.getInt("event_id"));
                    event.setName(resultSet.getString("name"));
                    event.setDescription(resultSet.getString("description"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp startTimestamp = resultSet.getTimestamp("start_date");
                    if (startTimestamp != null) {
                        event.setStartDate(startTimestamp.toLocalDateTime());
                    }

                    event.setLocation(resultSet.getString("location"));
                    event.setType(resultSet.getString("type"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp ticketSaleTimestamp = resultSet.getTimestamp("ticket_sale_date");
                    if (ticketSaleTimestamp != null) {
                        event.setTicketSaleDate(ticketSaleTimestamp.toLocalDateTime());
                    }

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp endTimestamp = resultSet.getTimestamp("end_date");
                    if (endTimestamp != null) {
                        event.setEndDate(endTimestamp.toLocalDateTime());
                    }

                    event.setPoster(resultSet.getString("poster"));
                    event.setBanner(resultSet.getString("banner"));
                    event.setApproved(resultSet.getInt("is_approve"));
                    // Set the organizer if applicable
                    events.add(event);
                }
            }
        }
        return events;
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Event");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Event event = new Event();
                event.setEventId(resultSet.getInt("event_id"));
                event.setName(resultSet.getString("name"));
                event.setDescription(resultSet.getString("description"));

                // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                Timestamp startTimestamp = resultSet.getTimestamp("start_date");
                if (startTimestamp != null) {
                    event.setStartDate(startTimestamp.toLocalDateTime());
                }

                event.setLocation(resultSet.getString("location"));
                event.setType(resultSet.getString("type"));

                // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                Timestamp ticketSaleTimestamp = resultSet.getTimestamp("ticket_sale_date");
                if (ticketSaleTimestamp != null) {
                    event.setTicketSaleDate(ticketSaleTimestamp.toLocalDateTime());
                }

                // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                Timestamp endTimestamp = resultSet.getTimestamp("end_date");
                if (endTimestamp != null) {
                    event.setEndDate(endTimestamp.toLocalDateTime());
                }

                event.setPoster(resultSet.getString("poster"));
                event.setBanner(resultSet.getString("banner"));
                event.setApproved(resultSet.getInt("is_approve"));
                // Set the organizer if applicable
                events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public Event getEventById(int eventid) {
        Event event = null;
        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Event WHERE event_id = ?");) {

            statement.setInt(1, eventid); // Truyền tên sự kiện cụ thể vào câu lệnh SQL

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    event = new Event();
                    event.setEventId(resultSet.getInt("event_id"));
                    event.setName(resultSet.getString("name"));
                    event.setDescription(resultSet.getString("description"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp startTimestamp = resultSet.getTimestamp("start_date");
                    if (startTimestamp != null) {
                        event.setStartDate(startTimestamp.toLocalDateTime());
                    }

                    event.setLocation(resultSet.getString("location"));
                    event.setType(resultSet.getString("type"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp ticketSaleTimestamp = resultSet.getTimestamp("ticket_sale_date");
                    if (ticketSaleTimestamp != null) {
                        event.setTicketSaleDate(ticketSaleTimestamp.toLocalDateTime());
                    }

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp endTimestamp = resultSet.getTimestamp("end_date");
                    if (endTimestamp != null) {
                        event.setEndDate(endTimestamp.toLocalDateTime());
                    }

                    event.setPoster(resultSet.getString("poster"));
                    event.setBanner(resultSet.getString("banner"));
                    event.setApproved(resultSet.getInt("is_approve"));
                    event.setView(resultSet.getInt("eyeview"));
                    // Set the organizer if applicable
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }

    public EventStatistics getEventStatisticsByEventId(int eventId) {
        EventStatistics stats = null;

        String sql = "SELECT " +
                "SUM(CASE WHEN t.is_returned = 0 THEN s.ticket_price ELSE 0 END) AS total_revenue, " +
                "COUNT(CASE WHEN t.is_returned = 0 THEN 1 END) AS tickets_sold, " +
                "COUNT(CASE WHEN t.is_returned = 1 THEN 1 END) AS tickets_returned, " +
                "(SELECT COUNT(*) FROM Seat s2 " +
                "JOIN `Row` r2 ON s2.row_id = r2.row_id " +
                "JOIN Area a2 ON r2.area_id = a2.area_id " +
                "WHERE a2.event_id = ?) " + // Sử dụng tham số
                "- COUNT(CASE WHEN t.is_returned = 0 THEN 1 END) " +
                "- COUNT(CASE WHEN t.is_returned = 1 THEN 1 END) AS tickets_remaining " +
                "FROM " +
                "Ticket t " +
                "JOIN " +
                "Seat s ON t.seat_id = s.seat_id " +
                "JOIN " +
                "`Row` r ON s.row_id = r.row_id " +
                "JOIN " +
                "Area a ON r.area_id = a.area_id " +
                "WHERE " +
                "a.event_id = ?;";

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eventId);
            statement.setInt(2, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    stats = new EventStatistics();
                    stats.setTotalRevenue(resultSet.getDouble("total_revenue"));
                    stats.setTicketsSold(resultSet.getInt("tickets_sold"));
                    stats.setTicketsReturned(resultSet.getInt("tickets_returned"));
                    stats.setTicketsRemaining(resultSet.getInt("tickets_remaining"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    public Map<String, Double> getDailyRevenueByEventId(int eventId) {
        Map<String, Double> dailyRevenueMap = new HashMap<>();

        String sql = "SELECT DATE(t.purchase_date) AS date, SUM(s.ticket_price) AS daily_revenue " +
                "FROM Ticket t " +
                "JOIN Seat s ON t.seat_id = s.seat_id " +
                "JOIN `Row` r ON s.row_id = r.row_id " +
                "JOIN Area a ON r.area_id = a.area_id " +
                "WHERE a.event_id = ? AND t.is_returned = 0 " + // Chỉ lấy vé không bị hoàn trả
                "GROUP BY DATE(t.purchase_date);";

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String date = resultSet.getString("date");
                    Double dailyRevenue = resultSet.getDouble("daily_revenue");
                    dailyRevenueMap.put(date, dailyRevenue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dailyRevenueMap;
    }

    public int addEvent(String name, String description, LocalDateTime startDate, String location, String type,
                        LocalDateTime ticketSaleDate, LocalDateTime endDate, int organizerId, String poster, String banner)
            throws ClassNotFoundException, SQLException {

        Class.forName(Baseconnection.nameClass);
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int eventId = 0;

        try {
            connection = ConnectionPoolManager.getConnection();

            // Sử dụng tùy chọn RETURN_GENERATED_KEYS để lấy khóa tự động tăng
            ps = connection.prepareStatement(
                    "INSERT INTO Event (name, description, start_date, location, type, ticket_sale_date, end_date, organizer_id, poster, banner, is_approve) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, name);
            ps.setString(2, description);

            // Chuyển đổi LocalDateTime sang Timestamp
            if (startDate != null) {
                ps.setTimestamp(3, Timestamp.valueOf(startDate));
            } else {
                ps.setTimestamp(3, null);
            }

            ps.setString(4, location);
            ps.setString(5, type);

            // Chuyển đổi LocalDateTime sang Timestamp
            if (ticketSaleDate != null) {
                ps.setTimestamp(6, Timestamp.valueOf(ticketSaleDate));
            } else {
                ps.setTimestamp(6, null);
            }

            // Chuyển đổi LocalDateTime sang Timestamp
            if (endDate != null) {
                ps.setTimestamp(7, Timestamp.valueOf(endDate));
            } else {
                ps.setTimestamp(7, null);
            }

            ps.setInt(8, organizerId);
            ps.setString(9, poster);
            ps.setString(10, banner);
            ps.setBoolean(11, false);

            // Thực thi câu lệnh SQL
            int affectedRows = ps.executeUpdate();

            // Kiểm tra nếu có dòng nào bị ảnh hưởng
            if (affectedRows > 0) {
                // Lấy khóa tự động tăng vừa được tạo
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    eventId = rs.getInt(1);
                }
            }
        } finally {
            // Đóng các tài nguyên
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return eventId;
    }

    public int updateEvent(int eventId, String name, String description, LocalDateTime startDate, String location,
                           String type,
                           LocalDateTime ticketSaleDate, LocalDateTime endDate, int organizerId, String poster, String banner)
            throws ClassNotFoundException, SQLException {

        Class.forName(Baseconnection.nameClass);
        Connection connection = ConnectionPoolManager.getConnection();

        // Câu lệnh SQL để cập nhật sự kiện
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE Event SET name = ?, description = ?, start_date = ?, location = ?, type = ?, " +
                        "ticket_sale_date = ?, end_date = ?, organizer_id = ?, poster = ?, banner = ?, is_approve = ? "
                        +
                        "WHERE event_id = ?");

        ps.setString(1, name);
        ps.setString(2, description);

        // Chuyển đổi LocalDateTime sang Timestamp
        if (startDate != null) {
            ps.setTimestamp(3, Timestamp.valueOf(startDate));
        } else {
            ps.setTimestamp(3, null);
        }

        ps.setString(4, location);
        ps.setString(5, type);

        // Chuyển đổi LocalDateTime sang Timestamp
        if (ticketSaleDate != null) {
            ps.setTimestamp(6, Timestamp.valueOf(ticketSaleDate));
        } else {
            ps.setTimestamp(6, null);
        }

        // Chuyển đổi LocalDateTime sang Timestamp
        if (endDate != null) {
            ps.setTimestamp(7, Timestamp.valueOf(endDate));
        } else {
            ps.setTimestamp(7, null);
        }

        ps.setInt(8, organizerId);
        ps.setString(9, poster);
        ps.setString(10, banner);
        ps.setBoolean(11, false);
        ps.setInt(12, eventId);

        int rowsUpdated = ps.executeUpdate();

        ps.close();
        connection.close();

        return rowsUpdated;
    }

    public ArrayList<Event> getAllEventsByOrganizerId(int organizerId) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM Event WHERE organizer_id = ?";

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, organizerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Event event = new Event();
                    event.setEventId(resultSet.getInt("event_id"));
                    event.setName(resultSet.getString("name"));
                    event.setDescription(resultSet.getString("description"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp startTimestamp = resultSet.getTimestamp("start_date");
                    if (startTimestamp != null) {
                        event.setStartDate(startTimestamp.toLocalDateTime());
                    }

                    event.setLocation(resultSet.getString("location"));
                    event.setType(resultSet.getString("type"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp ticketSaleTimestamp = resultSet.getTimestamp("ticket_sale_date");
                    if (ticketSaleTimestamp != null) {
                        event.setTicketSaleDate(ticketSaleTimestamp.toLocalDateTime());
                    }

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp endTimestamp = resultSet.getTimestamp("end_date");
                    if (endTimestamp != null) {
                        event.setEndDate(endTimestamp.toLocalDateTime());
                    }

                    event.setPoster(resultSet.getString("poster"));
                    event.setBanner(resultSet.getString("banner"));
                    event.setApproved(resultSet.getInt("is_approve"));
                    event.setAreas(getAreasByEventId(event.getEventId()));

                    // Lấy ảnh sơ đồ chỗ ngồi
                    event.setSeatMap((getSeatMapDetailsByEventId(event.getEventId())));
                    events.add(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    public ArrayList<Event> searchEventByNameAndOrganizerId(String name, int organizerId) {
        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM Event WHERE organizer_id = ? AND name LIKE ?";

        try (Connection connection = ConnectionPoolManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, organizerId);
            statement.setString(2, "%" + name + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Event event = new Event();
                    event.setEventId(resultSet.getInt("event_id"));
                    event.setName(resultSet.getString("name"));
                    event.setDescription(resultSet.getString("description"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp startTimestamp = resultSet.getTimestamp("start_date");
                    if (startTimestamp != null) {
                        event.setStartDate(startTimestamp.toLocalDateTime());
                    }

                    event.setLocation(resultSet.getString("location"));
                    event.setType(resultSet.getString("type"));

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp ticketSaleTimestamp = resultSet.getTimestamp("ticket_sale_date");
                    if (ticketSaleTimestamp != null) {
                        event.setTicketSaleDate(ticketSaleTimestamp.toLocalDateTime());
                    }

                    // Sử dụng getTimestamp() và chuyển đổi thành LocalDateTime
                    Timestamp endTimestamp = resultSet.getTimestamp("end_date");
                    if (endTimestamp != null) {
                        event.setEndDate(endTimestamp.toLocalDateTime());
                    }

                    event.setPoster(resultSet.getString("poster"));
                    event.setBanner(resultSet.getString("banner"));
                    event.setApproved(resultSet.getInt("is_approve"));
                    // Set the organizer_id if applicable
                    event.setAreas(getAreasByEventId(event.getEventId()));

                    // Lấy ảnh sơ đồ chỗ ngồi
                    event.setSeatMap((getSeatMapDetailsByEventId(event.getEventId())));

                    events.add(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    private List<Area> getAreasByEventId(int eventId) throws Exception {
        List<Area> areas = new ArrayList<>();
        Connection con = ConnectionPoolManager.getConnection();
        String query = "SELECT * FROM Area WHERE event_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Area area = new Area();
            area.setAreaId(rs.getInt("area_id"));
            area.setName(rs.getString("name"));
            area.setTicketPrice(rs.getFloat("ticket_price"));
            areas.add(area);
        }

        rs.close();
        ps.close();
        con.close();

        return areas;
    }

    private SeatMap getSeatMapDetailsByEventId(int eventId) throws Exception {
        SeatMap seatMapDetails = null;
        Connection con = ConnectionPoolManager.getConnection();
        String query = "SELECT img, name FROM SeatMap WHERE event_id = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, eventId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String seatMapImage = rs.getString("img");
            String seatMapName = rs.getString("name");
            seatMapDetails = new SeatMap(seatMapName, seatMapImage);
        }

        rs.close();
        ps.close();
        con.close();

        return seatMapDetails;
    }

    public List<Event> searchEvents(String keyword) {
        List<Event> events = getAllEvents();
        List<Event> findEvents = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getName().toLowerCase().contains(keyword)) {
                findEvents.add(events.get(i));
            }
        }
        return findEvents;
    }

    public void incrementClickCount(int eventId) {
        String sql = "UPDATE Event SET eyeview = eyeview + 1 WHERE event_id = ?";
        try (Connection conn = ConnectionPoolManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
