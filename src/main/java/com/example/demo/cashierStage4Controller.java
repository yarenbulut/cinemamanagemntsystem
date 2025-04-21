package com.example.demo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class cashierStage4Controller {


    @FXML
    private TextField ageField;
    @FXML
    private ImageView productImageView;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private Button saveCustomerButton;
    @FXML
    private Button confirmCartButton;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Double> productPriceColumn;
    @FXML
    private TableColumn<Product, Integer> productQuantityColumn;
    @FXML
    private Label totalTicketPrice, totalAgeDiscount, ticketTax, ProductTax, calculatedTotalPrice, totalProductPrice;
    @FXML
    private Text customerInfoLabel;


    public static final double SEAT_PRICE = 100.0;
    private static final double PRODUCT_TAX_RATE = 0.10;
    private static final double AGE_DISCOUNT_RATE = 0.20;
    private static final double TICKET_TAX_RATE = 0.20;

    private int currentSeatIndex = 0;
    double ticketTaxAmount;
    double productTaxAmount;
    double discount = 0;
    private List<Integer> selectedSeats;
    double totalTicketCost;
    double totalCost;
    int sessionId;
    Customer customer;
    double totalProductCost;
    private List<Product> selectedProducts;
    Map<Integer, Integer> seatToCustomerMap = new HashMap<>();





    public void initializeController(int sessionId, List<Integer> selectedSeats) {
        confirmCartButton.setDisable(true);
        this.sessionId = sessionId;
        this.selectedSeats = selectedSeats;
        this.selectedProducts = new ArrayList<>();
        customerInfoLabel.setStyle("-fx-fill: red;");
        customerInfoLabel.setText("Enter Customer Information for Seat " + selectedSeats.get(0) + "!");
        initializeTicketCosts();
        initializeProductTable();
        Cashier.loadProductsToTable(productTable);
    }

    private void initializeTicketCosts() {
        int seatCount = selectedSeats.size();
        totalTicketCost = seatCount * SEAT_PRICE;
        ticketTaxAmount = totalTicketCost * TICKET_TAX_RATE;
        totalCost = totalTicketCost + ticketTaxAmount;

        totalTicketPrice.setText(String.format("$%.2f", totalTicketCost));
        ticketTax.setText(String.format("$%.2f", ticketTaxAmount));
        calculatedTotalPrice.setText(String.format("$%.2f", totalCost));
    }

    private void initializeProductTable() {
        productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        productPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProductPrice()).asObject());
        productQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        productQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        productQuantityColumn.setOnEditCommit(event -> {
            Product product = event.getRowValue();
            int newQuantity = event.getNewValue();

            if (newQuantity > product.getStock()) {
                Main.showError("Insufficient stock for " + product.getProductName() + ". Available stock: " + product.getStock());
                product.setQuantity(product.getQuantity());
                productTable.refresh();
            } else {
                product.setQuantity(newQuantity);
                updateCart();

                if (newQuantity > 0 && !selectedProducts.contains(product)) {
                    selectedProducts.add(product);
                } else if (newQuantity == 0) {
                    selectedProducts.remove(product);
                }
            }
        });

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateProductImage(newSelection.getProductImage());
            }
        });
    }

    private void updateProductImage(String productImage) {
        if (productImage != null && !productImage.isEmpty()) {
            try {
                Image image = new javafx.scene.image.Image(getClass().getResourceAsStream("/"+productImage));
                productImageView.setImage(image);
            } catch (Exception e) {
                Main.showError("Failed to load image: " + productImage);
            }
        } else {
            productImageView.setImage(null);
        }
    }

    @FXML
    private void saveCustomerInfo() {
        if (!validateCustomerInfo()) return;

        int customerID = Cashier.saveCustomerToDatabase(nameField, surnameField, ageField);
        customer = new Customer(customerID, nameField.getText(), surnameField.getText(), Integer.parseInt(ageField.getText()));
        discount = calculateDiscount(Integer.parseInt(ageField.getText()));
        updateAgeBasedDiscount(discount);


        currentSeatIndex++;
        updateCart();
        toggleSaveButton();

        nameField.clear();
        surnameField.clear();
        ageField.clear();

        if (currentSeatIndex < selectedSeats.size()) {
            customerInfoLabel.setText("Enter Customer Information for Seat " + selectedSeats.get(currentSeatIndex) + "!");
        } else {
            customerInfoLabel.setStyle("-fx-fill: green;");
            customerInfoLabel.setText("You Can Choose Products and Proceed");
        }
    }

    private double calculateDiscount(int age) {
        int seatPrice = 100;
        if (age < 18 || age > 65) {
            discount += seatPrice * AGE_DISCOUNT_RATE;
        }
        return discount;
    }

    private boolean validateCustomerInfo() {

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            Main.showError("Name field cannot be empty. Please enter a valid name.");
            return false;
        }

        if (surnameField.getText() == null || surnameField.getText().trim().isEmpty()) {
            Main.showError("Surname field cannot be empty. Please enter a valid surname.");
            return false;
        }

        String ageText = ageField.getText();
        if (ageText == null || ageText.trim().isEmpty()) {
            Main.showError("Age field cannot be empty. Please enter a valid age.");
            return false;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age <= 0) {
                Main.showError("Age must be a positive number. Please enter a valid age.");
                return false;
            }
        } catch (NumberFormatException e) {
            Main.showError("Age must be a number. Please enter a valid age.");
            return false;
        }

        return true;
    }

    private void updateAgeBasedDiscount(double discountAmount) {
        totalAgeDiscount.setText(String.format("-$%.2f", discountAmount));
    }

    private void toggleSaveButton() {
        boolean confirmSave = currentSeatIndex >= selectedSeats.size();
        saveCustomerButton.setDisable(confirmSave);
        confirmCartButton.setDisable(!confirmSave);
    }


    private void updateCart() {
        totalProductCost = productTable.getItems().stream().mapToDouble(product -> product.getProductPrice() * product.getQuantity()).sum();
        totalProductPrice.setText(String.format("$%.2f", totalProductCost));

        productTaxAmount = totalProductCost * PRODUCT_TAX_RATE;
        totalCost = totalTicketCost + totalProductCost + ticketTaxAmount + productTaxAmount - discount;

        ProductTax.setText(String.format("$%.2f", productTaxAmount));
        calculatedTotalPrice.setText(String.format("$%.2f", totalCost));
    }

    @FXML
    private void confirmCart() throws IOException {

        for (Product product : productTable.getItems()) {
            if (product.getQuantity() > 0) {
                Cashier.updateProductStock(product);
            }
        }

        double totalCostBefore = totalProductCost + totalTicketCost;
        int shoppingCartID = Cashier.insertIntoShoppingCart(customer.getCustomerID(), totalCost, discount, ticketTaxAmount, productTaxAmount);
        ShoppingCart shoppingCart = new ShoppingCart(shoppingCartID,customer.getCustomerID(), discount, ticketTaxAmount, productTaxAmount, totalCostBefore , selectedSeats, selectedProducts , totalCost );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("cashierFinalStage.fxml"));
        Parent root = loader.load();

        CashierFinalStageController cashierFinalStageController = loader.getController();
        cashierFinalStageController.initialize(customer, shoppingCart);


        Scene cashierFinalStageScene = new Scene(root, Main.sceneWidth,Main.sceneHeight);
        cashierFinalStageScene.getStylesheets().add("tabpane.css");
        Main.changeScene(cashierFinalStageScene);
    }

    @FXML
    public void returnToSeatSelection() {
        Main.changeScene(cashierStage2Controller.seatSelectionScene);
    }

    public void handleLogout( ) {
        Main.goBacktoLogin();
    }
}
