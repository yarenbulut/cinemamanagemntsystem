package com.example.demo;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Map;

public class CashierFinalStageController {

    @FXML
    private ImageView poster;
    @FXML
    private Label totalAmountLabel, taxesLabel, discountAmountLabel;
    @FXML
    private GridPane productGridPane;
    @FXML
    private TabPane ticketTabPane;
    @FXML
    private TextField returnAmountTextField;
    @FXML
    private Button generateInvoiceButton, confirmPaymentButton, returntoSearchButton;
    @FXML
    private Label returnAmountLabel;

    private Customer customer;
    private ShoppingCart shoppingCart;

    public void initialize(Customer customer, ShoppingCart shoppingCart) {
        double totalTaxAmountToBePaid = shoppingCart.getProductTaxAmount() + shoppingCart.getTicketTaxAmount();
        this.customer = customer;
        this.shoppingCart = shoppingCart;

        generateInvoiceButton.setDisable(true);

        totalAmountLabel.setText("$" + String.format("%.2f", shoppingCart.getFinalAmount()));
        taxesLabel.setText("$" + String.format("%.2f", totalTaxAmountToBePaid));
        discountAmountLabel.setText("-$"+ String.format("%.2f", shoppingCart.getDiscountAmount()));

        setProducts();
        populateTabs();

        try {
            Image image = new javafx.scene.image.Image(getClass().getResourceAsStream(cashierController.movie.getPosterUrl()));
            poster.setImage(image);
        } catch (Exception e) {
            Main.showError("Image not found" + e.getMessage());
            poster.setImage(null);
        }

    }


    private void setProducts() {
        productGridPane.getChildren().clear();

        Label productHeader = new Label("Products");
        Label quantityHeader = new Label("Quantity");
        Label totalAmountHeader = new Label("Total Amount");

        productHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        quantityHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        totalAmountHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        productGridPane.add(productHeader, 0, 0);
        productGridPane.add(quantityHeader, 1, 0);
        productGridPane.add(totalAmountHeader, 2, 0);

        int rowIndex = 1;

        for (Product product : shoppingCart.getSelectedProducts()) {
            if (product.getQuantity() > 0) {
                Label productLabel = new Label(product.getProductName());
                Label quantityLabel = new Label(String.valueOf(product.getQuantity()));
                double amount = product.getQuantity() * product.getProductPrice();
                Label totalAmountLabel = new Label(String.format("$%.2f", amount));

                productLabel.setStyle("-fx-font-size: 14px;");
                quantityLabel.setStyle("-fx-font-size: 14px;");
                totalAmountLabel.setStyle("-fx-font-size: 14px;");

                productGridPane.add(productLabel, 0, rowIndex);
                productGridPane.add(quantityLabel, 1, rowIndex);
                productGridPane.add(totalAmountLabel, 2, rowIndex);

                rowIndex++;
            }
        }
    }

    public void populateTabs() {
        for (int seatNumber : shoppingCart.getSelectedSeats()) {
            addTicketTab(seatNumber, cashierController.movie.getTitle(), cashierStage2Controller.selectedSession.getDay(), cashierStage2Controller.selectedSession.getHall());
        }
    }

    private void addTicketTab(int seatNumber, String movieName, String sessionTime, String hallName) {
        Tab ticketTab = new Tab("Seat " + seatNumber + " Ticket");

        VBox ticketContent = new VBox();
        ticketContent.setSpacing(10);
        ticketContent.setPadding(new Insets(10));
        ticketContent.setStyle("-fx-background-color: white;");

        Label movieLabel = new Label("Movie: " + movieName);
        Label sessionLabel = new Label("Session: " + sessionTime);
        Label hallLabel = new Label("Hall: " + hallName);
        Label seatLabel = new Label("Seat: " + seatNumber);

        movieLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        sessionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        hallLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        seatLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000;");


        ticketContent.getChildren().addAll(movieLabel, sessionLabel, hallLabel, seatLabel);

        ticketTab.setContent(ticketContent);

        ticketTabPane.getTabs().add(ticketTab);
    }



    @FXML
    public void handleConfirmPayment() {
        try {
            double paymentAmount = Double.parseDouble(returnAmountTextField.getText());
            double finalAmount = shoppingCart.getFinalAmount();

            if (paymentAmount < finalAmount) {
                Main.showError("Insufficient payment amount. Please enter an amount greater than or equal to $" + String.format("%.2f", finalAmount));
                return;
            }

            double returnAmount = paymentAmount - finalAmount;
            returnAmountLabel.setText(String.format("$%.2f", returnAmount));

            Main.showSuccess("Payment successful! Change: $" + String.format("%.2f", returnAmount));
            Cashier.setOccupiedSeats(shoppingCart.getSelectedSeats(),  cashierStage2Controller.selectedSession.getSessionID());

            returnAmountTextField.clear();
            generateInvoiceButton.setDisable(false);
            confirmPaymentButton.setDisable(true);
            returntoSearchButton.setDisable(true);




        } catch (NumberFormatException e) {
            Main.showError("Invalid payment amount. Please enter a valid number.");
        }
    }


    @FXML
    private void handleGenerateInvoice() {
        try {
            String projectPath = System.getProperty("user.dir");
            String invoicesPath = projectPath + "/src/invoices/";
            String filePath = invoicesPath + customer.getName() + "_" + customer.getCustomerID() + "_" + "invoice.html";

            Cashier.generateInvoice(customer, shoppingCart, filePath);

            Cashier.saveTicketsToDatabase(filePath, shoppingCart, customer);

            Cashier.saveShoppingCartItemsToDatabase(shoppingCart, customer);

            Main.showSuccess("Invoice generated successfully at: " + filePath);



            generateInvoiceButton.setDisable(true);
            returntoSearchButton.setDisable(false);

        } catch (IOException e) {
            Main.showError("Failed to generate invoice: " + e.getMessage());
        }
    }


    @FXML
    public void handleReturnToStage4() {
        Main.changeScene(Main.roleScene);
    }
}
