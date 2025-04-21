package com.example.demo;

public abstract class Employee {
    private int employeeID;
    private String username;
    private String password;
    private String role;
    private String name;
    private String surname;


    public Employee(int employeeID, String username, String password, String role, String name, String surname) {
        this.employeeID = employeeID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.surname = surname;

    }

    public int getEmployeeID() {
        return this.employeeID;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRole() {
        return this.role;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


}
