package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.sql.*;

public class Manager extends Employee {

    public Manager(int employeeId, String username, String password, String role, String name, String surname) {
        super(employeeId, username, password, role, name, surname);
    }


    public void viewAndUpdateStock(TableView<Product> productTable) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/yourDatabaseName", "yourUsername", "yourPassword");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM products")) {

            ObservableList<Product> products = FXCollections.observableArrayList();
            while (resultSet.next()) {
                products.add(new Product(
                        resultSet.getInt("ProductID"),
                        resultSet.getString("ProductName"),
                        resultSet.getDouble("Price"),
                        resultSet.getInt("StockQuantity"),
                        resultSet.getString("ImageURL")
                ));

            }
            productTable.setItems(products);

        } catch (SQLException e) {
            System.err.println("Error while viewing stock: " + e.getMessage());
        }
    }

    public void updateStock(int productId, int newStock) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/yourDatabaseName", "yourUsername", "yourPassword");
             PreparedStatement updateStatement = connection.prepareStatement("UPDATE products SET StockQuantity = ? WHERE ProductID = ?")) {

            updateStatement.setInt(1, newStock);
            updateStatement.setInt(2, productId);
            updateStatement.executeUpdate();

            System.out.println("Stock updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error while updating stock: " + e.getMessage());
        }
    }
}