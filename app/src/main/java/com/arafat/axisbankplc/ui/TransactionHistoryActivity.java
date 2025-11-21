package com.arafat.axisbankplc.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arafat.axisbankplc.R;
import com.arafat.axisbankplc.adapters.TransactionAdapter;
import com.arafat.axisbankplc.firebase.FirebaseTransactionService;
import com.arafat.axisbankplc.models.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;

    FirebaseTransactionService transactionService;
    TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.rvTransactions);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionService = new FirebaseTransactionService();

        loadTransactions();
    }

    private void loadTransactions() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        String uid = firebaseUser.getUid();

        transactionService.getTransactions(uid, new FirebaseTransactionService.TransactionCallback() {
            @Override
            public void onSuccess(List<Transaction> transactions) {

                progressBar.setVisibility(View.GONE);

                if (transactions.isEmpty()) {
                    Toast.makeText(TransactionHistoryActivity.this, "No Transactions Found!", Toast.LENGTH_SHORT).show();
                }

                adapter = new TransactionAdapter(transactions);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransactionHistoryActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
