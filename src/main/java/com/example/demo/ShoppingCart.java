package com.example.demo;

import java.util.List;

public class ShoppingCart {
    private final double ticketTaxAmount;
    private final double productTaxAmount;
    private int cartID;
    private int customerID;
    private double discountAmount;
    private double totalPrice;
    private List<Product> selectedProducts;
    private List<Integer> selectedSeats;
    private double finalAmount;
    protected static final double PRODUCT_TAX_RATE = 0.10;
    protected static final double TICKET_TAX_RATE = 0.20;

    public ShoppingCart(int cartID, int customerID, double discountAmount,double ticketTaxAmount, double productTaxAmount, double totalPrice, List<Integer> selectedSeats, List<Product> selectedProducts, double FinalAmount) {
        this.ticketTaxAmount = ticketTaxAmount;
        this.productTaxAmount = productTaxAmount;
        this.cartID = cartID;
        this.customerID = customerID;
        this.discountAmount = discountAmount;
        this.totalPrice = totalPrice;
        this.selectedSeats = selectedSeats;
        this.selectedProducts = selectedProducts;
        this.finalAmount = FinalAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public double getTicketTaxAmount() {
        return ticketTaxAmount;
    }

    public double getProductTaxAmount() {
        return productTaxAmount;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Product> getSelectedProducts() {
        return selectedProducts;
    }

    public void setSelectedProducts(List<Product> selectedProducts) {
        this.selectedProducts = selectedProducts;
    }

    public List<Integer> getSelectedSeats() {
        return selectedSeats;
    }

    public int getCartID() {
        return cartID;
    }
    public void setSelectedSeats(List<Integer> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }


}
