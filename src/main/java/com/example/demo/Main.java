package com.example.demo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    
    public static Stage stg;
    public static int sceneWidth = 1303;
    public static int sceneHeight = 823;
    public static Scene roleScene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stg = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), sceneWidth, sceneHeight);
        primaryStage.setTitle("Group06 CinemaCenter!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void changeScene(String fxmlPath, Employee employee) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Object controller = loader.getController();
        if (controller instanceof managerController managerController && employee instanceof Manager) {
            managerController.afterLogin(employee);
        } else if (controller instanceof adminController adminController && employee instanceof Admin) {
            adminController.afterLogin(employee);

        } else if (controller instanceof cashierController cashierController && employee instanceof Cashier) {
            cashierController.afterLogin(employee);
        }

        roleScene = new Scene(root, sceneWidth, sceneHeight);
        roleScene.getStylesheets().add("tabpane.css");
        changeScene(roleScene);
    }

    public static void changeScene(Scene scene){
        Main.stg.setScene(scene);
        Main.stg.setResizable(false);
        Main.stg.show();
    }


    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public static void goBacktoLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
            Parent root = fxmlLoader.load();

            Scene loginScene = new Scene(root, sceneWidth, sceneHeight);

            changeScene(loginScene);
        } catch (IOException e) {
            showError("Failed to load login screen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}