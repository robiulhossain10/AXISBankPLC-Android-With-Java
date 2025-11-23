package com.arafat.axisbankplc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arafat.axisbankplc.R;
import com.arafat.axisbankplc.firebase.FirebaseAuthService;
import com.arafat.axisbankplc.firebase.FirebaseUserService;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;

    TextView btnSignup;
    ProgressBar progressBar;

    FirebaseAuthService authService;
    FirebaseUserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnSignup = findViewById(R.id.txtGoRegister);

        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        authService = new FirebaseAuthService();
        userService = new FirebaseUserService();

        btnLogin.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();

        // Validation
        if (email.isEmpty()) {
            edtEmail.setError("Email required");
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Password required");
            return;
        }

        // Show Progress
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        authService.login(email, password, new FirebaseAuthService.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                // Redirect to DashboardActivity
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
