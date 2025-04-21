package com.example.demo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class cashierController implements Initializable {

    @FXML
    private Label name;
    @FXML
    private Label movieTitle;
    @FXML
    private Text movieSummary;
    @FXML
    private ImageView movieImage;
    @FXML
    private TextField textFieldforSearch;
    @FXML
    private ListView<String> resultOfQuery;

    public static Movie movie;

    public static Scene cashierStage2Scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        movie = new Movie();
    }

    public void afterLogin(Employee employee) {
        this.name.setText(" Cashier " + employee.getName() + " " +  employee.getSurname());
    }


    private void handleSearchResult(List<String> movies) {
        resultOfQuery.getItems().clear();
        resultOfQuery.getItems().addAll(movies);

        resultOfQuery.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) return;

            String selectedMovie = resultOfQuery.getSelectionModel().getSelectedItem();
            movie = movie.selectedMovie(selectedMovie);

            if (movie == null) {
                movieTitle.setText("Movie not found");
                movieSummary.setText("");
                movieImage.setImage(null);
                return;
            }

            movieTitle.setText(movie.getTitle());
            movieSummary.setText(movie.getSummary());

            try {
                Image image = new javafx.scene.image.Image(getClass().getResourceAsStream(movie.getPosterUrl()));
                movieImage.setImage(image);
            } catch (Exception e) {
                Main.showError("Image not found" + e.getMessage());
                movieImage.setImage(null);
            }
        });
    }

    public void setSearchByGenre() {
        resultOfQuery.getItems().clear();
        String genre = textFieldforSearch.getText();

        if (genre == null || genre.isEmpty()) {
            Main.showError("Please enter genre");
            return;
        }

        List<String> movies = movie.searchByGenre(genre);

        if (movies.isEmpty()) {
            Main.showError("No such genre");
            return;
        }

        handleSearchResult(movies);
        textFieldforSearch.clear();
    }

    public void setSearchByPartialName() {
        resultOfQuery.getItems().clear();
        String partialName = textFieldforSearch.getText();

        if (partialName == null || partialName.isEmpty()) {
            Main.showError("Please enter a partial name");
            return;
        }

        List<String> movies = movie.searchByPartialName(partialName);

        if (movies.isEmpty()) {
            Main.showError("No such movie!");
            return;
        }

        handleSearchResult(movies);
        textFieldforSearch.clear();
    }

    public void setSearchByFullname() {
        resultOfQuery.getItems().clear();
        String fullName = textFieldforSearch.getText();

        if (fullName == null || fullName.isEmpty()) {
            Main.showError("Please enter full name.");
            return;
        }

        List<String> movies = movie.searchByFullname(fullName);

        if (movies.isEmpty()) {
            Main.showError("No such movie!");
            return;
        }

        handleSearchResult(movies);
        textFieldforSearch.clear();
    }

    public void approveSelection() throws IOException {
        String selectedMovie = resultOfQuery.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) {
            Main.showError("Please select a movie");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("cashierStage2.fxml"));
        Parent root = loader.load();

        cashierStage2Controller controller = loader.getController();
        controller.loadMovieData(movie);

        cashierStage2Scene = new Scene(root,Main.sceneWidth,Main.sceneHeight);
        Main.changeScene(cashierStage2Scene);

    }

    public void handleLogout() {
        Main.goBacktoLogin();
    }
}
