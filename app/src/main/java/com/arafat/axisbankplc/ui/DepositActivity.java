package com.arafat.axisbankplc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arafat.axisbankplc.R;
import com.arafat.axisbankplc.firebase.FirebaseTransactionService;
import com.arafat.axisbankplc.firebase.FirebaseUserService;
import com.arafat.axisbankplc.models.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class DepositActivity extends AppCompatActivity {

    EditText edtAmount;
    Button btnDeposit;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseTransactionService transactionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        edtAmount = findViewById(R.id.edtAmount);
        btnDeposit = findViewById(R.id.btnDeposit);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        transactionService = new FirebaseTransactionService();

        btnDeposit.setOnClickListener(v -> depositMoney());
    }

    private void depositMoney() {

        String amountStr = edtAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            edtAmount.setError("Enter amount");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (Exception e) {
            edtAmount.setError("Invalid amount");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String uid = firebaseUser.getUid();

        // 1️⃣ Create transaction
        Transaction transaction = FirebaseTransactionService.createTransaction(
                "Deposit",
                amount,
                null
        );

        // 2️⃣ Save transaction first
        transactionService.addTransaction(uid, transaction, new FirebaseTransactionService.TransactionSaveCallback() {
            @Override
            public void onSuccess() {

                // 3️⃣ Now update user balance
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .update("balance", FieldValue.increment(amount))
                        .addOnSuccessListener(aVoid -> {

                            progressBar.setVisibility(View.GONE);
                            edtAmount.setText("");

                            Toast.makeText(DepositActivity.this, "Deposit Successful", Toast.LENGTH_SHORT).show();

                            // Redirect to Dashboard
                            Intent intent = new Intent(DepositActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();

                        })

                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(DepositActivity.this, "Balance update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DepositActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
