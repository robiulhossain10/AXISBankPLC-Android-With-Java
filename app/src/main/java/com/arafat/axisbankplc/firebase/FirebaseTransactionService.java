package com.arafat.axisbankplc.firebase;

import com.arafat.axisbankplc.models.Transaction;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirebaseTransactionService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface TransactionSaveCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface TransactionCallback {
        void onSuccess(List<Transaction> transactions);
        void onFailure(String error);
    }

    // Add a transaction for a user
    public void addTransaction(String userId, Transaction transaction, TransactionSaveCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onFailure("Invalid user ID");
            return;
        }

        CollectionReference txRef = db.collection("users")
                .document(userId)
                .collection("transactions");

        txRef.add(transaction)
                .addOnSuccessListener(a -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Fetch all transactions for a user
    public void getTransactions(String userId, TransactionCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onFailure("Invalid user ID");
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("transactions")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Transaction> transactions = querySnapshot.toObjects(Transaction.class);
                    callback.onSuccess(transactions);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Create a transaction object
    public static Transaction createTransaction(String type, double amount, String recipientAccount) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setRecipientAccount(recipientAccount);
        transaction.setDate(String.valueOf(System.currentTimeMillis()));
        return transaction;
    }
}
