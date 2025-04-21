package com.example.demo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;

public class adminController {


    @FXML
    private Label name;
    @FXML
    private ComboBox<Movie> movieComboboxForUpdateSession, movieComboboxForCreateSession;
    @FXML
    private ComboBox<String> hallComboboxForCreateSession, hallComboboxForUpdateSession;
    @FXML
    private ComboBox<SessionDetails> sessionComboboxForUpdateSession;
    @FXML
    private DatePicker datePickerForCreateSession, datePickerForUpdateSession;
    @FXML
    private TextField createStartTimeField, createEndTimeField, updateStartTimeField, updateEndTimeField ;


    @FXML
    private TextField titleField1, posterField1;
    @FXML
    private TextArea summaryArea1;
    @FXML
    private ComboBox<String> genreComboBox1;

    @FXML
    private TextField titleField, posterField;
    @FXML
    private TextArea summaryArea;
    @FXML
    private ComboBox<String> genreComboBox;
    @FXML
    private ComboBox<Movie> movieComboBox;


    // Process Cancellation
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerSurnameField;

    @FXML
    private TextField sessionIDField;
    @FXML
    private TableView<Ticket> ticketTableView;
    @FXML
    private TableColumn<Ticket, Integer> ticketIDColumn;
    @FXML
    private TableColumn<Ticket, Integer> sessionIDColumn;
    @FXML
    private TableColumn<Ticket, Integer> seatIDColumn;
    @FXML
    private TableColumn<Ticket, Double> priceColumn;
    @FXML
    private TableColumn<Ticket, Double> discountedPriceColumn;
    @FXML
    private Button filterButton;

    private ObservableList<Ticket> ticketList = FXCollections.observableArrayList();

    @FXML
    private TableView<CartItems> productTableView;
    @FXML
    private TableColumn<CartItems, Integer> productColumn,quantityColumn;
    @FXML
    private TableColumn<CartItems, Double> productPriceColumn,productTotalPriceColumn;


    public void afterLogin(Employee employee) {
        initialize();
        this.name.setText(" Welcome " + employee.getRole()+ " "+ employee.getName() + " " +  employee.getSurname());
    }

    public void initialize() {
        genreComboBox1.setItems(FXCollections.observableArrayList(Admin.getGenres()));
        genreComboBox.setItems(FXCollections.observableArrayList(Admin.getGenres()));

        movieComboBox.setItems(FXCollections.observableArrayList(Admin.getAllMovies()));
        movieComboBox.setOnAction(event -> populateMovieDetails());

        hallComboboxForCreateSession.setItems(FXCollections.observableArrayList("Hall_A", "Hall_B"));
        hallComboboxForUpdateSession.setItems(FXCollections.observableArrayList("Hall_A", "Hall_B"));

        movieComboboxForCreateSession.setItems(FXCollections.observableArrayList(Admin.getAllMovies()));
        movieComboboxForUpdateSession.setItems(FXCollections.observableArrayList(Admin.getAllMovies()));

        movieComboboxForUpdateSession.setOnAction(event -> loadSessionsForUpdate());
        hallComboboxForUpdateSession.setOnAction(event -> loadSessionsForUpdate());


        ticketIDColumn.setCellValueFactory(new PropertyValueFactory<>("ticketID"));
        sessionIDColumn.setCellValueFactory(new PropertyValueFactory<>("sessionID"));
        seatIDColumn.setCellValueFactory(new PropertyValueFactory<>("seatID"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        discountedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("discountedPrice"));


        productColumn.setCellValueFactory(new PropertyValueFactory<>("ProductName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productTotalPriceColumn.setCellValueFactory(cellData -> {
            CartItems item = cellData.getValue();
            double totalPrice = item.getQuantity() * item.getPrice();
            return new SimpleDoubleProperty(totalPrice).asObject();
        });

        ticketTableView.setItems(ticketList);
        filterButton.setOnAction(e -> handleFilterTickets());
    }


    private void loadProductsForTicket(int customerID) {
        ObservableList<CartItems> items = FXCollections.observableArrayList(Admin.getItemsForTicket(customerID));
        productTableView.setItems(items);
    }

    private void populateMovieDetails() {
        Movie selectedMovie = movieComboBox.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            titleField.setText(selectedMovie.getTitle());
            summaryArea.setText(selectedMovie.getSummary());
            posterField.setText(selectedMovie.getPosterUrl());
            genreComboBox.getSelectionModel().select(selectedMovie.getGenre() - 1);
        }
    }
    @FXML
    private void handleFilterTickets() {
        String customerName = customerNameField.getText().trim();
        String customerSurname = customerSurnameField.getText().trim();
        String sessionID = sessionIDField.getText().trim();

        if (customerName.isEmpty() || customerSurname.isEmpty() || sessionID.isEmpty()) {
            Main.showError("Please fill in all fields to filter tickets.");
            return;
        }

        try {
            int sessionIDInt = Integer.parseInt(sessionID);

            ticketList.clear();
            ticketList.addAll(Admin.getFilteredTickets(customerName, customerSurname, sessionIDInt));

            ticketTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadProductsForTicket(newSelection.getCustomerID());
                }
            });

        } catch (NumberFormatException e) {
            Main.showError("Session ID must be a valid number.");
        }
    }

    @FXML
    private void handleCancelTicketButton() {
        Ticket selectedTicket = ticketTableView.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            boolean success = Admin.cancelTicket(selectedTicket.getTicketID(), selectedTicket.getSeatID(), selectedTicket.getSessionID());
            if (success) {
                Main.showSuccess("Ticket canceled successfully!");
                ticketList.remove(selectedTicket);
            } else {
                Main.showError("Failed to cancel the ticket.");
            }
        } else {
            Main.showError("Please select a ticket to cancel.");
        }
    }

    @FXML
    public void handleCancelProductButton(ActionEvent actionEvent) {
        CartItems selectedProduct = productTableView.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            boolean success = Admin.cancelProduct(selectedProduct.getItemID(), selectedProduct.getProductId(), selectedProduct.getQuantity());
            if (success) {
                Main.showSuccess("Product canceled successfully!");
                productTableView.getItems().remove(selectedProduct);
            } else {
                Main.showError("Failed to cancel the product.");
            }
        } else {
            Main.showError("Please select a product to cancel.");
        }
    }

    @FXML
    private void handleAddMovie() {
        String title = titleField1.getText();
        String summary = summaryArea1.getText();
        String posterUrl = posterField1.getText();
        int genreId = genreComboBox1.getSelectionModel().getSelectedIndex() + 1;

        if (title.isEmpty() || summary.isEmpty() || posterUrl.isEmpty() || genreId == -1) {
            Main.showError("Please fill all fields before adding the movie.");
            return;
        }

        if (!Admin.copyPosterToImgFolder(posterUrl, "img")) {
            Main.showError("Failed to copy poster to img folder.");
            return;
        }

        String copiedPosterPath = "/img/" + new File(posterUrl).getName();
        if (Admin.addMovie(title, summary, copiedPosterPath, genreId)) {
            Main.showSuccess("Movie added successfully!");
            clearAddMovieForm();
        } else {
            Main.showError("Failed to add the movie. Please try again.");
        }
    }

    @FXML
    private void handleUpdateMovie() {
        Movie selectedMovie = movieComboBox.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            Main.showError("Please select a movie to update.");
            return;
        }

        String title = titleField.getText();
        String summary = summaryArea.getText();
        String posterUrl = posterField.getText();
        int genreId = genreComboBox.getSelectionModel().getSelectedIndex() + 1;

        if (title.isEmpty() || summary.isEmpty() || posterUrl.isEmpty() || genreId == -1) {
            Main.showError("Please fill all fields before updating the movie.");
            return;
        }

        if (!Admin.copyPosterToImgFolder(posterUrl, "img")) {
            Main.showError("Failed to copy poster to img folder. Please select an image to update the movie.");
            posterField.clear();
            return;
        }

        String copiedPosterPath = "/img/" + new File(posterUrl).getName();
        Admin.updateMovie(selectedMovie.getId(), title, genreId, summary, copiedPosterPath);

        Main.showSuccess("Movie updated successfully!");
        clearUpdateMovieForm();
        movieComboBox.setItems(FXCollections.observableArrayList(Admin.getAllMovies()));
    }

    @FXML
    private void handleBrowsePosterForAdd() {
        File selectedFile = showFileChooser();
        if (selectedFile != null) {
            posterField1.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleBrowsePosterForUpdate() {
        File selectedFile = showFileChooser();
        if (selectedFile != null) {
            posterField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void handleCreateSession() {
        Movie selectedMovie = movieComboboxForCreateSession.getSelectionModel().getSelectedItem();
        String selectedHall = hallComboboxForCreateSession.getSelectionModel().getSelectedItem();
        String sessionDate = (datePickerForCreateSession.getValue() != null) ? datePickerForCreateSession.getValue().toString() : null;
        String startTime = createStartTimeField.getText();
        String endTime = createEndTimeField.getText();

        if (selectedMovie == null || selectedHall == null || sessionDate == null || startTime.isEmpty() || endTime.isEmpty()) {
            Main.showError("Please fill all fields to create a session.");
            return;
        }

        if (Admin.isSessionConflict(selectedHall, sessionDate, startTime, endTime)) {
            Main.showError("There is a scheduling conflict for the selected hall and time. Please choose another time.");
            return;
        }


        boolean success = Admin.createSession(selectedMovie.getId(), selectedHall, sessionDate, startTime, endTime);

        if (success) {
            Admin.updateSeatsForSession(Admin.getLastInsertedSessionId(), selectedHall);
            Main.showSuccess("Session created successfully!");
            clearCreateSessionForm();
        } else {
            Main.showError("Failed to create session.");
        }
    }

    @FXML
    public void handleUpdateSession() {
        SessionDetails selectedSession = sessionComboboxForUpdateSession.getSelectionModel().getSelectedItem();
        if (selectedSession == null) {
            Main.showError("Please select a session to update.");
            return;
        }

        Movie selectedMovie = movieComboboxForUpdateSession.getSelectionModel().getSelectedItem();
        String selectedHall = hallComboboxForUpdateSession.getSelectionModel().getSelectedItem();
        String sessionDate = (datePickerForUpdateSession.getValue() != null) ? datePickerForUpdateSession.getValue().toString() : null;
        String startTime = updateStartTimeField.getText();
        String endTime = updateEndTimeField.getText();

        if (selectedMovie == null || selectedHall == null || sessionDate == null || startTime.isEmpty() || endTime.isEmpty()) {
            Main.showError("Please fill all fields to update the session.");
            return;
        }

        if (Admin.areTicketsAssignedToSession(selectedSession.getSessionID())) {
            Main.showError("Cannot update this session as tickets have already been sold.");
            return;
        }

        if (Admin.isSessionConflict(selectedHall, sessionDate, startTime, endTime)) {
            Main.showError("There is a scheduling conflict for the selected hall and time. Please choose another time.");
            return;
        }

        boolean success = Admin.updateSession(selectedSession.getSessionID(), selectedMovie.getId(), selectedHall, sessionDate, startTime, endTime);
        if (success) {
            Admin.updateSeatsForSession(selectedSession.getSessionID(), selectedHall);
            Main.showSuccess("Session updated successfully!");
            clearUpdateSessionForm();
        } else {
            Main.showError("Failed to update session.");
        }
    }

    private void loadSessionsForUpdate() {
        Movie selectedMovie = movieComboboxForUpdateSession.getSelectionModel().getSelectedItem();
        String selectedHall = hallComboboxForUpdateSession.getSelectionModel().getSelectedItem();

        if (selectedMovie != null && selectedHall != null) {
            sessionComboboxForUpdateSession.setItems(
                    FXCollections.observableArrayList(Admin.getSessionsByMovieAndHall(selectedMovie.getId(), selectedHall))
            );
        }
    }

    private File showFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Movie Poster");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));

        File defaultDirectory = new File(System.getProperty("user.dir") + "/src/main/resources/com/example/demo/img");
        if (defaultDirectory.exists()) {
            fileChooser.setInitialDirectory(defaultDirectory);
        }

        return fileChooser.showOpenDialog(null);
    }

    private void clearAddMovieForm() {
        titleField1.clear();
        summaryArea1.clear();
        posterField1.clear();
        genreComboBox1.getSelectionModel().clearSelection();
    }

    private void clearUpdateMovieForm() {
        titleField.clear();
        summaryArea.clear();
        posterField.clear();
        genreComboBox.getSelectionModel().clearSelection();
    }

    private void clearCreateSessionForm() {
        movieComboboxForCreateSession.getSelectionModel().clearSelection();
        hallComboboxForCreateSession.getSelectionModel().clearSelection();
        datePickerForCreateSession.setValue(null);
        createStartTimeField.clear();
        createEndTimeField.clear();
    }

    private void clearUpdateSessionForm() {

    }

    public void handleLogout() {
        Main.goBacktoLogin();
    }
}
