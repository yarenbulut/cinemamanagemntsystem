package com.example.demo;

public class Ticket {
    private int ticketID;
    private int sessionID;
    private int seatID;
    private double price;
    private double discountedPrice;
    private int CustomerID;

    public Ticket(int ticketID, int sessionID, int seatID, double price, double discountedPrice, int CustomerID) {
        this.ticketID = ticketID;
        this.sessionID = sessionID;
        this.seatID = seatID;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.CustomerID = CustomerID;
    }

    public int getTicketID() {
        return ticketID;
    }

    public int getCustomerID(){
        return CustomerID;
    }

    public int getSessionID() {
        return sessionID;
    }

    public int getSeatID() {
        return seatID;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }
}
