package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class Cashier extends Employee {

    static Connection connection;

    public Cashier(int employeeID, String username, String password, String role, String name, String surname) {
        super(employeeID, username, password, role, name, surname);
    }



    private static void initializeDatabaseConnection() {
        try {
             connection = Database.getConnection();
        } catch (SQLException e) {
            Main.showError("Database connection failed: " + e.getMessage());
        }
    }

    static void loadProductsToTable(TableView<Product> productTable) {
        String query = "SELECT * FROM Products";

        initializeDatabaseConnection();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            ObservableList<Product> productList = FXCollections.observableArrayList();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getDouble("Price"),
                        rs.getInt("StockQuantity"),
                        rs.getString("ImageURL")
                );
                productList.add(product);
            }

            productTable.setItems(productList);
        } catch (SQLException e) {
            Main.showError("Failed to load products: " + e.getMessage());
        }
    }

    public static int saveCustomerToDatabase(TextField nameField, TextField surnameField, TextField ageField) {
        String query = "INSERT INTO customers (FirstName, LastName, Age) VALUES (?, ?, ?)";
        int generatedCustomerId = -1;

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nameField.getText());
            stmt.setString(2, surnameField.getText());
            stmt.setInt(3, Integer.parseInt(ageField.getText()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedCustomerId = generatedKeys.getInt(1);
                    }
                }
            } else {
                Main.showError("Customer creation failed, no rows affected.");
            }

        } catch (SQLException e) {
            Main.showError("Failed to save customer: " + e.getMessage());
        }

        return generatedCustomerId;
    }

    static void setOccupiedSeats(List<Integer> selectedSeats, int sessionId)  {
        initializeDatabaseConnection();
        String updateQuery = "UPDATE seats SET IsOccupied = 1 WHERE SeatNumber = ? AND SessionID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            for (int seatNumber : selectedSeats) {
                stmt.setInt(1, seatNumber);
                stmt.setInt(2, sessionId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            Main.showError("Failed to update seat selection: " + e.getMessage());
        }
    }

    public static void updateProductStock(Product product) {
        initializeDatabaseConnection();
        String query = "UPDATE products SET StockQuantity = StockQuantity - ? WHERE ProductID = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, product.getQuantity());
            stmt.setInt(2, product.getProductId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            Main.showError("Failed to update product stock: " + e.getMessage());
        }
    }

    public static int insertIntoShoppingCart(int customerId, double totalAmount, double discountAmount, double ticketTaxAmount, double productTaxtAmount) {
        String query = "INSERT INTO shoppingcart (CustomerID, TotalAmount, DiscountAmount, TicketTaxAmount , ProductTaxAmount) VALUES (?, ?, ?, ?, ?)";
        int generatedCartId = -1;

        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.setDouble(2, totalAmount);
            stmt.setDouble(3, discountAmount);
            stmt.setDouble(4, ticketTaxAmount);
            stmt.setDouble(5, productTaxtAmount);



            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedCartId = generatedKeys.getInt(1);
                    }
                }
            } else {
                Main.showError("Shopping cart insertion failed, no rows affected.");
            }

        } catch (SQLException e) {
            Main.showError("Failed to insert into shopping cart: " + e.getMessage());
        }

        return generatedCartId;
    }

    public static void generateInvoice(Customer customer, ShoppingCart shoppingCart, String filePath) throws IOException {
        StringBuilder invoiceContent = new StringBuilder();
        invoiceContent.append("<html>");
        invoiceContent.append("<head><title>Invoice</title></head>");
        invoiceContent.append("<body>");
        invoiceContent.append("<h1>Invoice</h1>");
        invoiceContent.append("<p>Customer Name: " + customer.getName() + " " + customer.getSurname() + "</p>");
        invoiceContent.append("<p>Customer Age: " + customer.getAge() + "</p>");
        invoiceContent.append("<h2>Purchased Items:</h2>");
        invoiceContent.append("<ul>");
        for (Product product : shoppingCart.getSelectedProducts()) {
            invoiceContent.append("<li>" + product.getProductName() + " - Quantity: " + product.getQuantity() + " - Price: $" + product.getProductPrice() + "</li>");
        }
        invoiceContent.append("</ul>");
        invoiceContent.append("<p>Total Ticket Price: $" + shoppingCart.getTotalPrice() + "</p>");
        invoiceContent.append("<p>Discount: $" + shoppingCart.getDiscountAmount() + "</p>");
        invoiceContent.append("<p>Tax Amount for Products: $" + ShoppingCart.PRODUCT_TAX_RATE + "</p>");
        invoiceContent.append("<p>Tax Amount For Tickets: $" + ShoppingCart.TICKET_TAX_RATE + "</p>");
        invoiceContent.append("<p>Final Amount: $" + shoppingCart.getFinalAmount() + "</p>");
        invoiceContent.append("</body>");
        invoiceContent.append("</html>");

        FileWriter writer = new FileWriter(filePath);
        writer.write(invoiceContent.toString());
        writer.close();
    }

    static void saveTicketsToDatabase(String invoicePath, ShoppingCart shoppingCart, Customer customer) throws IOException {
        String insertQuery = "INSERT INTO tickets (SessionID, SeatID, CustomerID, Price, DiscountedPrice, InvoicePath) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getConnection()) {
            for (int seat : shoppingCart.getSelectedSeats()) {

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setInt(1, cashierStage2Controller.selectedSession.getSessionID());
                    preparedStatement.setInt(2, seat);
                    preparedStatement.setInt(3, customer.getCustomerID());
                    preparedStatement.setDouble(4, shoppingCart.getFinalAmount());
                    preparedStatement.setDouble(5, shoppingCart.getDiscountAmount());
                    preparedStatement.setString(6, invoicePath);

                    if (preparedStatement.executeUpdate() > 0) {
                        System.out.println("Ticket successfully saved for Seat " + seat + " and CustomerID " + customer.getCustomerID());
                    } else {
                        System.err.println("Failed to save ticket for Seat " + seat);
                    }
                }
            }

            Main.showSuccess("Tickets successfully saved to the database.");
        } catch (SQLException e) {
            Main.showError("Failed to save tickets to the database: " + e.getMessage());
        }
    }

    public static void saveShoppingCartItemsToDatabase(ShoppingCart shoppingCart, Customer customer) {
        String query = """
        INSERT INTO shoppingcartitems (cart_id, product_id, quantity, CustomerID) VALUES ( ?, ?, ?, ?)
    """;

        System.out.println(shoppingCart.getCartID());
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Product product : shoppingCart.getSelectedProducts()) {
                if (product.getQuantity() > 0) {
                    preparedStatement.setInt(1, shoppingCart.getCartID());
                    preparedStatement.setInt(2, product.getProductId());
                    preparedStatement.setInt(3, product.getQuantity());
                    preparedStatement.setInt(4, customer.getCustomerID());
                    preparedStatement.addBatch();
                }
            }

            preparedStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            Main.showError("Failed to save shopping cart items: " + e.getMessage());
        }
    }

}
