package com.arafat.axisbankplc.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arafat.axisbankplc.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText edtFullName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView txtGoLogin;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtGoLogin = findViewById(R.id.txtGoLogin);
        progressBar = findViewById(R.id.progressBar);

        txtGoLogin.setOnClickListener(v->{
            startActivity(new Intent(this, LoginActivity.class));
        });

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Register Button Click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        // Validation
        if (fullName.isEmpty()) {
            edtFullName.setError("Full Name Required");
            return;
        }
        if (email.isEmpty()) {
            edtEmail.setError("Email Required");
            return;
        }
        if (phone.isEmpty()) {
            edtPhone.setError("Phone Required");
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Password Required");
            return;
        }
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Password doesn't match");
            return;
        }

        // Show Progress
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Firebase Auth Create User
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String userId = auth.getCurrentUser().getUid();

                        // Save user details into Firestore
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("fullName", fullName);
                        userMap.put("email", email);
                        userMap.put("phone", phone);
                        userMap.put("balance", 0.0);   // default balance

                        db.collection("users")
                                .document(userId)
                                .set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnRegister.setEnabled(true);

                                    Toast.makeText(RegisterActivity.this,
                                            "Registration Successful",
                                            Toast.LENGTH_LONG).show();

                                    finish(); // go back to Login
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnRegister.setEnabled(true);
                                    Toast.makeText(RegisterActivity.this,
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,
                                "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
    }
}
