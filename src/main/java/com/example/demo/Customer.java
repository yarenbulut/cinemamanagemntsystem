package com.example.demo;

public class Customer {
    private String name;
    private String surname;
    private int age;
    int customerID;

    public Customer(int customerID, String name, String surname, int age) {
        this.customerID = customerID;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getCustomerID() {
        return customerID;
    }
}