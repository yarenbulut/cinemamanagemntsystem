package com.example.demo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class cashierStage3Controller {


    @FXML
    public Label seatWarningLabel;
    @FXML
    private GridPane seatGrid;
    @FXML
    private Label sessionDetailsLabel;
    @FXML
    private Text selectedSeatsTexts;
    @FXML
    private Text totalPrice;

    private List<Integer> selectedSeats = new ArrayList<>();
    public static Scene cashierStage4Scene;

    private Connection connection;
    private int sessionId;
    private double totalSeatPrice = 0;
    private int sessionSize;

    public void setupSeatLayout(SessionDetails sessionDetails) {
        if (Objects.equals(sessionDetails.getHall(), "Hall_A")){
            sessionSize = 16;
        } else {
            sessionSize = 48;
        }

        sessionDetailsLabel.setText(sessionDetails.getHall() + " " + sessionDetails.getDay() + " " + sessionDetails.getTime() + ".");
        initializeDatabaseConnection();
        loadSeatData(sessionDetails);
    }

    private void initializeDatabaseConnection() {
        try {
            connection = Database.getConnection();
        } catch (SQLException e) {
            Main.showError("Database connection failed: " + e.getMessage());
        }
    }

    private void loadSeatData(SessionDetails sessionDetails) {
        seatGrid.getChildren().clear();
        sessionId = sessionDetails.getSessionID();

        String query = "SELECT SeatID, SeatNumber, IsOccupied FROM seats WHERE HallName = ? AND SessionID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sessionDetails.getHall());
            stmt.setInt(2, sessionDetails.getSessionID());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int seatNumber = rs.getInt("SeatNumber");
                boolean isOccupied = rs.getBoolean("IsOccupied");
                createSeatButton(seatNumber, isOccupied);
            }
        } catch (SQLException e) {
            Main.showError("Failed to load seat data: " + e.getMessage());
        }
    }

    private void createSeatButton(int seatNumber, boolean isOccupied) {
        Button seatButton = new Button("Seat" + seatNumber);
        seatButton.setStyle(isOccupied ? "-fx-background-color: red;" : "-fx-background-color: green;");
        seatButton.setDisable(isOccupied);
        seatButton.setOnAction(event -> handleSeatSelection(seatButton, seatNumber));

        int row, col;

        if (sessionSize == 16) {
            row = (seatNumber - 1) / 4;
            col = (seatNumber - 1) % 4;
            seatGrid.vgapProperty().setValue(40);
            seatGrid.hgapProperty().setValue(40);
            seatButton.setPrefWidth(150);
            seatButton.setPrefHeight(70);
        } else {
            row = (seatNumber - 1) / 6;
            col = (seatNumber - 1) % 6;
            seatGrid.vgapProperty().setValue(10);
            seatGrid.hgapProperty().setValue(10);
            seatButton.setPrefWidth(100);
            seatButton.setPrefHeight(70);
        }

        seatGrid.add(seatButton, col, row);
    }


    private void handleSeatSelection(Button seatButton, int seatNumber) {
        String currentStyle = seatButton.getStyle();
        double seatPrice = 100;
        if (currentStyle.contains("green")) {
            seatButton.setStyle("-fx-background-color: yellow;");
            selectedSeats.add(seatNumber);
            totalSeatPrice += seatPrice;
        } else if (currentStyle.contains("yellow")) {
            seatButton.setStyle("-fx-background-color: green;");
            selectedSeats.remove((Integer) seatNumber);
            totalSeatPrice -= seatPrice;
        }
        totalPrice.setText("$"+totalSeatPrice);

        StringBuilder selectedSeatsBuilder = new StringBuilder();
        for (int i = 0; i < selectedSeats.size(); i++) {
            selectedSeatsBuilder.append("Seat ").append(selectedSeats.get(i).toString()).append(" ");
        }
        selectedSeatsTexts.setText(selectedSeatsBuilder.toString());

    }

    public void confirmSeatSelection() throws IOException {
        if (selectedSeats.isEmpty()) {
            Main.showError("Please select at least one seat!");
            return;
        }
        changeScene();
    }


    public void changeScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("cashierStage4.fxml"));
        Parent root = loader.load();

        cashierStage4Controller controller = loader.getController();
        controller.initializeController(sessionId, selectedSeats);

        cashierStage4Scene = new Scene(root, Main.sceneWidth,Main.sceneHeight);
        Main.changeScene(cashierStage4Scene);
    }

    public void goBackToSecondStage() {
        Main.changeScene(cashierController.cashierStage2Scene);
    }

    public void handleLogout() {
        Main.goBacktoLogin();
    }
}