package com.arafat.axisbankplc.models;

public class Transaction {
    private String type; // Deposit / Withdraw / Transfer
    private double amount;
    private String date;
    private String recipientAccount; // Transfer er jonno

    public Transaction() { }

    // Getters & Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getRecipientAccount() { return recipientAccount; }
    public void setRecipientAccount(String recipientAccount) { this.recipientAccount = recipientAccount; }
}
