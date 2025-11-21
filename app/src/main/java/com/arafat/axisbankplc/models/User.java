package com.arafat.axisbankplc.models;

public class User {
    private String uid;
    private String fullName;
    private String phone;
    private String email;
    private String accountNumber;
    private double balance;
    private boolean isLoggedIn; // Firestore warning fix

    public User() { }

    public User(String uid, String fullName, String phone, String email, String accountNumber, double balance, boolean isLoggedIn) {
        this.uid = uid;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.isLoggedIn = isLoggedIn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}