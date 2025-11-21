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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.arafat.axisbankplc.models.Transaction;

public class WithdrawActivity extends AppCompatActivity {

    EditText edtAmount;
    Button btnWithdraw;
    ProgressBar progressBar;

    FirebaseTransactionService transactionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        edtAmount = findViewById(R.id.edtAmount);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        progressBar = findViewById(R.id.progressBar);

        transactionService = new FirebaseTransactionService();

        btnWithdraw.setOnClickListener(v -> withdrawMoney());
    }

    private void withdrawMoney() {

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

        // 1️⃣ Create Withdraw Transaction
        Transaction transaction = FirebaseTransactionService.createTransaction(
                "Withdraw",
                amount,
                null
        );

        // 2️⃣ Save withdraw transaction
        transactionService.addTransaction(uid, transaction, new FirebaseTransactionService.TransactionSaveCallback() {
            @Override
            public void onSuccess() {

                // 3️⃣ Now decrease user balance
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .update("balance", FieldValue.increment(-amount)) // decrement
                        .addOnSuccessListener(aVoid -> {

                            progressBar.setVisibility(View.GONE);
                            edtAmount.setText("");

                            Toast.makeText(WithdrawActivity.this, "Withdraw Successful", Toast.LENGTH_SHORT).show();

                            // 4️⃣ Redirect to Dashboard
                            Intent intent = new Intent(WithdrawActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();

                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(WithdrawActivity.this, "Balance update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WithdrawActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
