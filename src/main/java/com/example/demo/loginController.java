package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;


public class loginController {

    @FXML
    public Button loginButton;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label wrongLogin;


    @FXML
    protected void userLogin() throws IOException {
        Main main = new Main();

        String username = this.username.getText();
        String password = this.password.getText();

        Employee employee = Authentication.login(username, password);

        if (employee instanceof Manager manager) {
            main.changeScene("manager.fxml", manager);
        } else if (employee instanceof Admin admin) {
            main.changeScene("admin.fxml", admin);
        } else if (employee instanceof Cashier cashier) {
            main.changeScene("cashier.fxml", cashier);
        } else if (employee == null) {
            wrongLogin.setText("Username or password cannot be empty");
        } else {
            wrongLogin.setText("Username or password is incorrect");
        }
    }
}
