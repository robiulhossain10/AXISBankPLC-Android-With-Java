package com.arafat.axisbankplc.firebase;

import com.arafat.axisbankplc.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUserService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(String error);
    }

    // Get current logged-in user
    public void getCurrentUser(UserCallback callback) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onFailure("User not logged in");
            return;
        }

        db.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("User data not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Save or update user
    public void saveUser(User user, Runnable onSuccess, UserCallback onFailure) {
        if (user.getUid() == null || user.getUid().isEmpty()) {
            onFailure.onFailure("User UID is null or empty");
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(a -> onSuccess.run())
                .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    // Get user by account number
    public void getUserByAccountNumber(String accountNumber, UserCallback callback) {
        db.collection("users")
                .whereEqualTo("accountNumber", accountNumber)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("User not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
