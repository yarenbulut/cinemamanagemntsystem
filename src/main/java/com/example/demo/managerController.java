package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class managerController {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> colProductId;
    @FXML
    private TableColumn<Product, String> colProductName;
    @FXML
    private TableColumn<Product, Double> colProductPrice;
    @FXML
    private TableColumn<Product, Integer> colProductStock;

    @FXML
    private TextField txtStockUpdate;

    @FXML
    private Button btnUpdateStock;

    private ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupProductTable();
        loadProductsFromDatabase();
    }

    private void setupProductTable() {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        colProductStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void loadProductsFromDatabase() {
        productList.clear();
        String query = "SELECT * FROM products";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productList.add(new Product(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("Price"),
                        rs.getInt("StockQuantity"),
                        rs.getString("ImageURL")
                ));
            }

            productTable.setItems(productList);

        } catch (SQLException e) {
            e.printStackTrace();
            Main.showError("Ürünler yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateStock() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            Main.showError("Lütfen bir ürün seçiniz.");
            return;
        }

        String stockInput = txtStockUpdate.getText();
        if (stockInput.isEmpty()) {
            Main.showError("Lütfen stok miktarını giriniz.");
            return;
        }

        int additionalStock;
        try {
            additionalStock = Integer.parseInt(stockInput);
        } catch (NumberFormatException e) {
            Main.showError("Geçerli bir stok miktarı giriniz.");
            return;
        }

        String updateQuery = "UPDATE products SET StockQuantity = StockQuantity + ? WHERE ProductID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {

            ps.setInt(1, additionalStock);
            ps.setInt(2, selectedProduct.getProductId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                Main.showSuccess("Stok başarıyla güncellendi!");
                loadProductsFromDatabase();
                txtStockUpdate.clear();
            } else {
                Main.showError("Stok güncellenemedi. Lütfen tekrar deneyiniz.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Main.showError("Stok güncellenirken hata oluştu: " + e.getMessage());
        }
    }

    public void afterLogin(Employee employee) {
        System.out.println("Manager logged in: " + employee.getName());
        loadProductsFromDatabase();
    }

    public void handleLogout() {
        Main.goBacktoLogin();
    }
}