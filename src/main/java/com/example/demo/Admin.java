package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin extends Employee {


    public Admin(int employeeID, String username, String password, String role, String name, String surname) {
        super(employeeID, username, password, role, name, surname);
    }

    public static boolean addMovie(String title, String summary, String posterUrl, int genreId) {
        String query = "INSERT INTO movies (title, summary, poster_url, genre) VALUES (?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, summary);
            stmt.setString(3, posterUrl);
            stmt.setInt(4, genreId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean areTicketsAssignedToSession(int sessionId) {
        String query = "SELECT COUNT(*) AS ticketCount FROM tickets WHERE SessionID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("ticketCount") > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error checking tickets for session: " + e.getMessage());
        }
        return false;
    }

    public static List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movies";
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("summary"),
                        rs.getString("poster_url"),
                        rs.getInt("genre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }

    public static List<String> getGenres() {
        List<String> genres = new ArrayList<>();
        String query = "SELECT genre_name FROM genre";
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                genres.add(rs.getString("genre_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }

    public static void updateMovie(int movieId, String title, int genreId, String summary, String posterUrl) {
        String query = "UPDATE movies SET title = ?, genre = ?, summary = ?, poster_url = ? WHERE movie_id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setInt(2, genreId);
            stmt.setString(3, summary);
            stmt.setString(4, posterUrl);
            stmt.setInt(5, movieId);
            stmt.executeUpdate();
            System.out.println("Movie updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean copyPosterToImgFolder(String sourceFilePath, String destinationFolderName) {
        try {
            String projectPath = System.getProperty("user.dir");

            String destinationFolderPath = projectPath + "/src/main/resources/" + destinationFolderName;

            File sourceFile = new File(sourceFilePath);
            File destinationFile = new File(destinationFolderPath, sourceFile.getName());

            File destinationFolder = new File(destinationFolderPath);
            if (!destinationFolder.exists()) {
                boolean isCreated = destinationFolder.mkdirs();
                if (!isCreated) {
                    throw new IOException("Failed to create directory: " + destinationFolderPath);
                }
            }

            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Poster copied to: " + destinationFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.err.println("Error while copying poster: " + e.getMessage());
            return false;
        }
    }


    public static boolean isSessionConflict(String hallName, String sessionDate, String startTime, String endTime) {
        String query = "SELECT COUNT(*) AS conflictCount FROM sessions " +
                "WHERE HallName = ? AND SessionDate = ? " +
                "AND ((StartTime < ? AND EndTime > ?) OR (StartTime < ? AND EndTime > ?) OR (StartTime >= ? AND EndTime <= ?))";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, hallName);
            stmt.setString(2, sessionDate);
            stmt.setString(3, endTime);
            stmt.setString(4, endTime);
            stmt.setString(5, startTime);
            stmt.setString(6, startTime);
            stmt.setString(7, startTime);
            stmt.setString(8, endTime);

            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("conflictCount") > 0) {
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error checking session conflict: " + e.getMessage());
        }

        return false;
    }

    public static boolean createSession(int movieId, String hallName, String sessionDate, String startTime, String endTime) {
        String query = "INSERT INTO sessions (MovieID, HallName, SessionDate, StartTime, EndTime) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, movieId);
            stmt.setString(2, hallName);
            stmt.setString(3, sessionDate);
            stmt.setString(4, startTime);
            stmt.setString(5, endTime);
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.err.println("Error creating session: " + e.getMessage());
            return false;
        }
    }

    public static List<SessionDetails> getSessionsByMovieAndHall(int movieId, String hallName) {
        List<SessionDetails> sessions = new ArrayList<>();
        String query = "SELECT * FROM sessions WHERE MovieID = ? AND HallName = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, movieId);
            stmt.setString(2, hallName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SessionDetails session = new SessionDetails();
                session.setSessionId(rs.getInt("SessionID"));
                session.setHall(rs.getString("HallName"));
                session.setDay(rs.getString("SessionDate"));
                session.setTime(rs.getString("StartTime"));
                sessions.add(session);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sessions: " + e.getMessage());
        }
        return sessions;
    }

    public static boolean updateSession(int sessionId, int movieId, String hallName, String sessionDate, String startTime, String endTime) {
        String updateSessionQuery = "UPDATE sessions SET MovieID = ?, HallName = ?, SessionDate = ?, StartTime = ?, EndTime = ? WHERE SessionID = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement updateSessionStmt = connection.prepareStatement(updateSessionQuery)) {


            updateSessionStmt.setInt(1, movieId);
            updateSessionStmt.setString(2, hallName);
            updateSessionStmt.setString(3, sessionDate);
            updateSessionStmt.setString(4, startTime);
            updateSessionStmt.setString(5, endTime);
            updateSessionStmt.setInt(6, sessionId);


            int rowsUpdated = updateSessionStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Session updated successfully!");
                return true;
            } else {
                System.out.println("Failed to update session.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating session: " + e.getMessage());
            return false;
        }
    }

    public static void updateSeatsForSession(int sessionId, String hallName) {
        String deleteSeatsQuery = "DELETE FROM seats WHERE SessionID = ?";
        String insertSeatsQuery = "INSERT INTO seats (HallName, SeatNumber, IsOccupied, SessionID) VALUES (?, ?, 0, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteSeatsQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertSeatsQuery)) {

            deleteStmt.setInt(1, sessionId);
            deleteStmt.executeUpdate();

            int totalSeats = hallName.equals("Hall_A") ? 16 : 48;

            for (int i = 1; i <= totalSeats; i++) {
                insertStmt.setString(1, hallName);
                insertStmt.setInt(2, i);
                insertStmt.setInt(3, sessionId);
                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            System.out.println("Seats updated successfully for session ID: " + sessionId);

        } catch (SQLException e) {
            System.err.println("Error updating seats: " + e.getMessage());
        }
    }

    public static int getLastInsertedSessionId() {
        String query = "SELECT LAST_INSERT_ID() AS last_id";
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching last inserted session ID: " + e.getMessage());
        }
        return -1;
    }

    public static List<Ticket> getFilteredTickets(String customerName, String customerSurname, int sessionID) {
        List<Ticket> tickets = new ArrayList<>();
        String query = """
            SELECT t.TicketID, t.SessionID, t.SeatID, t.Price, t.DiscountedPrice, t.CustomerID
            FROM tickets t
            JOIN customers c ON t.CustomerID = c.CustomerID
            WHERE c.FirstName = ? AND c.LastName = ? AND t.SessionID = ?;
            
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, customerName);
            preparedStatement.setString(2, customerSurname);
            preparedStatement.setInt(3, sessionID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Ticket ticket = new Ticket(
                            resultSet.getInt("TicketID"),
                            resultSet.getInt("SessionID"),
                            resultSet.getInt("SeatID"),
                            resultSet.getDouble("Price"),
                            resultSet.getDouble("DiscountedPrice"),
                            resultSet.getInt("CustomerID")
                    );
                    tickets.add(ticket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public static boolean cancelTicket(int ticketID, int seatID, int sessionId) {
        String deleteTicketQuery = "DELETE FROM tickets WHERE TicketID = ?";
        String updateSeatQuery = "UPDATE seats SET IsOccupied = 0 WHERE SeatID = ? AND SessionID = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement deleteTicketStmt = connection.prepareStatement(deleteTicketQuery);
             PreparedStatement updateSeatStmt = connection.prepareStatement(updateSeatQuery)) {

            deleteTicketStmt.setInt(1, ticketID);
            int rowsDeleted = deleteTicketStmt.executeUpdate();

            System.out.println("Seats updated successfully for session ID: " + sessionId);
            System.out.println("" + sessionId);
            updateSeatStmt.setInt(1, seatID);
            updateSeatStmt.setInt(2, sessionId);
            int rowsUpdated = updateSeatStmt.executeUpdate();

            return rowsDeleted > 0 && rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error canceling ticket: " + e.getMessage());
            return false;
        }
    }

    public static boolean cancelProduct(int itemID, int productID, int quantity) {
        String deleteProductQuery = "DELETE FROM shoppingcartitems WHERE item_id = ?";
        String updateStockQuery = "UPDATE products SET StockQuantity = StockQuantity + ? WHERE ProductID = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement deleteProductStmt = connection.prepareStatement(deleteProductQuery);
             PreparedStatement updateStockStmt = connection.prepareStatement(updateStockQuery)) {

            deleteProductStmt.setInt(1, itemID);
            int rowsDeleted = deleteProductStmt.executeUpdate();

            updateStockStmt.setInt(1, quantity);
            updateStockStmt.setInt(2, productID);
            int rowsUpdated = updateStockStmt.executeUpdate();

            return rowsDeleted > 0 && rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error canceling product: " + e.getMessage());
            return false;
        }
    }

    public static List<CartItems> getItemsForTicket(int customerID) {
        List<CartItems> items = new ArrayList<>();
        String query = """
                SELECT s.item_id, s.cart_id,  s.product_id , s.quantity, p.ProductName, p.Price
                        FROM shoppingcartitems s
                        JOIN products p ON s.product_id = p.ProductID
                        WHERE s.CustomerID = ?;
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            System.out.println(customerID);
            preparedStatement.setInt(1, customerID );

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    CartItems item = new CartItems(
                            resultSet.getInt("item_id"),
                            resultSet.getInt("cart_id"),
                            resultSet.getInt("product_id"),
                            resultSet.getInt("quantity"),
                            resultSet.getInt("Price"),
                            resultSet.getString("ProductName")
                    );
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
}