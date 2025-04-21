package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;


public class cashierStage2Controller {
    @FXML
    private Label movieTitleLabel;
    @FXML
    private Label movieSummaryLabel;
    @FXML
    private TableView<SessionDetails> sessionTable;
    @FXML
    private TableColumn<SessionDetails, String> dayColumn;
    @FXML
    private TableColumn<SessionDetails, String> timeColumn;
    @FXML
    private TableColumn<SessionDetails, String> hallColumn;
    @FXML
    private TableColumn<SessionDetails, Integer> vacantSeatsColumn;

    private SessionDetails session;

    public static Scene seatSelectionScene;
    public static SessionDetails selectedSession;


    public void initialize() {
        session = new SessionDetails();
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        hallColumn.setCellValueFactory(new PropertyValueFactory<>("hall"));
        vacantSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("vacantSeats"));

    }

    public void loadMovieData(Movie movie) {
        movieTitleLabel.setText(movie.getTitle());
        movieSummaryLabel.setText(movie.getSummary());

        List<SessionDetails> sessions = session.fetchAvailableSessionDetails(movie);

        ObservableList<SessionDetails> sessionData = FXCollections.observableArrayList(sessions);

        sessionTable.setItems(sessionData);
    }

    @FXML
    private void confirmSelection() throws IOException {
        selectedSession = sessionTable.getSelectionModel().getSelectedItem();

        if (selectedSession == null) {
            Main.showError("Please select a session!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("cashierStage3.fxml"));
        Parent root = loader.load();

        cashierStage3Controller cashierStage3Controller = loader.getController();
        cashierStage3Controller.setupSeatLayout(selectedSession);

        seatSelectionScene = new Scene(root, Main.sceneWidth,Main.sceneHeight);
        Main.changeScene(seatSelectionScene);
    }

    @FXML
    private void goBackToSearch(){
        Main.changeScene(Main.roleScene);
    }

    public void handleLogout() {
        Main.goBacktoLogin();
    }
}

