package com.example.demo;

public class CartItems {
    private int itemID;
    private int cart_id;
    private int product_id;
    private int quantity;
    private double price;
    private String ProductName;

    public CartItems(int itemID, int cart_id, int product_id, int quantity, double price, String ProductName) {
        this.itemID = itemID;
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.price = price;
        this.ProductName = ProductName;
    }

    public int getItemID() {
        return itemID;
    }

    public int getProductId() {
        return product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
