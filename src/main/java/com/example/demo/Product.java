package com.example.demo;

import javafx.beans.property.*;

public class Product {
    private int productId;
    private String productName;
    private double productPrice;
    private int stock;
    private String productImage;
    private IntegerProperty quantity;

    public Product(int productId, String productName, double productPrice, int stock, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.stock = stock;
        this.productImage = productImage;
        this.quantity = new SimpleIntegerProperty(0);
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getStock() {
        return stock;
    }

    public String getProductImage() {
        return productImage;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }
}
