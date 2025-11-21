package com.arafat.axisbankplc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.arafat.axisbankplc.R;
import com.arafat.axisbankplc.firebase.FirebaseAuthService;
import com.arafat.axisbankplc.firebase.FirebaseUserService;
import com.arafat.axisbankplc.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    TextView txtName, txtEmail, txtBalance;
    ProgressBar progressBar;
    CardView cardDeposit,cardWithdraw,cardTransfer,history,logout;

    FirebaseAuthService authService;
    FirebaseUserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtBalance = findViewById(R.id.txtBalance);
        progressBar = findViewById(R.id.progressBar);

        cardDeposit = findViewById(R.id.cardDeposit);
        cardWithdraw = findViewById(R.id.cardWithdraw);
        cardTransfer = findViewById(R.id.cardTransfer);
        history = findViewById(R.id.cardTransactionHistory);
        logout = findViewById(R.id.cardProfile);

        authService = new FirebaseAuthService();
        userService = new FirebaseUserService();

        cardDeposit.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, DepositActivity.class));
        });
        cardWithdraw.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, WithdrawActivity.class));
        });
        cardTransfer.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, TransferActivity.class));
        });
        history.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, TransactionHistoryActivity.class));
        });

        logout.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("DEBUG_UID", "Current User UID: " + (user != null ? user.getUid() : "null"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);
        userService.getCurrentUser(new FirebaseUserService.UserCallback() {
            @Override
            public void onSuccess(User user) {
                progressBar.setVisibility(View.GONE);
                txtName.setText(user.getFullName());
                txtEmail.setText(user.getEmail());
                txtBalance.setText(String.format("Balance: $%.2f", user.getBalance()));
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DashboardActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
