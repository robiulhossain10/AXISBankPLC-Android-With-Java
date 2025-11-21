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
import com.arafat.axisbankplc.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class TransferActivity extends AppCompatActivity {

    EditText edtReceiverAccount, edtAmount;
    Button btnTransfer;
    ProgressBar progressBar;

    FirebaseUserService userService;
    FirebaseTransactionService transactionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        edtReceiverAccount = findViewById(R.id.edtRecipientAccount);
        edtAmount = findViewById(R.id.edtAmount);
        btnTransfer = findViewById(R.id.btnTransfer);
        progressBar = findViewById(R.id.progressBar);

        userService = new FirebaseUserService();
        transactionService = new FirebaseTransactionService();

        btnTransfer.setOnClickListener(v -> transferMoney());
    }

    private void transferMoney() {

        String receiverAccount = edtReceiverAccount.getText().toString().trim();
        String amountStr = edtAmount.getText().toString().trim();

        if (receiverAccount.isEmpty()) {
            edtReceiverAccount.setError("Enter receiver account number");
            return;
        }
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

        String senderUid = firebaseUser.getUid();

        // 1️⃣ Receiver খুঁজে বের করা
        userService.getUserByAccountNumber(receiverAccount, new FirebaseUserService.UserCallback() {
            @Override
            public void onSuccess(User receiver) {
                String receiverUid = receiver.getUid();

                if (receiverUid.equals(senderUid)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(TransferActivity.this, "Cannot transfer to your own account", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2️⃣ Sender Transaction Create
                Transaction senderTransaction = FirebaseTransactionService.createTransaction(
                        "Transfer Sent",
                        amount,
                        receiverAccount
                );

                // 3️⃣ Receiver Transaction Create
                Transaction receiverTransaction = FirebaseTransactionService.createTransaction(
                        "Transfer Received",
                        amount,
                        null
                );

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // 4️⃣ Sender balance deduct
                db.collection("users").document(senderUid)
                        .update("balance", FieldValue.increment(-amount))
                        .addOnSuccessListener(a -> {

                            // 5️⃣ Receiver balance increase
                            db.collection("users").document(receiverUid)
                                    .update("balance", FieldValue.increment(amount))
                                    .addOnSuccessListener(b -> {

                                        // 6️⃣ Save sender transaction
                                        transactionService.addTransaction(senderUid, senderTransaction, new FirebaseTransactionService.TransactionSaveCallback() {
                                            @Override
                                            public void onSuccess() {

                                                // 7️⃣ Save receiver transaction
                                                transactionService.addTransaction(receiverUid, receiverTransaction, new FirebaseTransactionService.TransactionSaveCallback() {
                                                    @Override
                                                    public void onSuccess() {

                                                        progressBar.setVisibility(View.GONE);
                                                        edtAmount.setText("");
                                                        edtReceiverAccount.setText("");

                                                        Toast.makeText(TransferActivity.this, "Transfer Successful", Toast.LENGTH_SHORT).show();

                                                        // 8️⃣ Redirect to Dashboard
                                                        Intent intent = new Intent(TransferActivity.this, DashboardActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(String error) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(TransferActivity.this, "Receiver transaction error: " + error, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(String error) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(TransferActivity.this, "Sender transaction error: " + error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    })
                                    .addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(TransferActivity.this, "Receiver balance update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(TransferActivity.this, "Sender balance update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransferActivity.this, "Receiver not found: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
