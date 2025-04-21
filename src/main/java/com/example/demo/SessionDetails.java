package com.example.demo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDetails {
    private String day;
    private String time;
    private String hall;
    private int vacantSeats;
    private int sessionID;


    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getHall() {
        return hall;
    }

    public int getSessionID() {
        return sessionID ;
    }

    public int getVacantSeats() {
        return vacantSeats;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public void setVacantSeats(int vacantSeats) {
        this.vacantSeats = vacantSeats;
    }

    public void setSessionId(int sessionId) {
        this.sessionID = sessionId;
    }

    public List<SessionDetails> fetchAvailableSessionDetails(Movie movie) {
        List<SessionDetails> sessionsList = new ArrayList<>();

        try (Connection connection = Database.getConnection()) {
            String SELECT_QUERY = "SELECT s.SessionID, s.HallName, s.SessionDate, s.StartTime, " +
                    "(h.TotalSeats - COUNT(seat.SeatID)) AS VacantSeats " +
                    "FROM firmms.sessions s " +
                    "JOIN firmms.halls h ON s.HallName = h.HallName " +
                    "LEFT JOIN firmms.seats seat ON s.SessionID = seat.SessionID AND seat.IsOccupied = 1 " +
                    "WHERE s.MovieID = ? " +
                    "GROUP BY s.SessionID, s.HallName, s.SessionDate, s.StartTime, h.TotalSeats;";

            PreparedStatement statement = connection.prepareStatement(SELECT_QUERY);
            statement.setInt(1, movie.getId());

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                SessionDetails details = new SessionDetails();
                details.setSessionId(rs.getInt("SessionID"));
                details.setHall(rs.getString("HallName"));
                details.setDay(rs.getString("SessionDate"));
                details.setTime(rs.getString("StartTime"));
                details.setVacantSeats(rs.getInt("VacantSeats"));
                sessionsList.add(details);
            }

        } catch (SQLException e) {
            Main.showError("Error fetching available session details: " + e.getMessage());
        }

        return sessionsList;
    }

    @Override
    public String toString() {
        return "Date: " + day + ", Time: " + time + ", Hall: " + hall;
    }
}
