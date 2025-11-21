package com.arafat.axisbankplc.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAuthService {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String error);
    }

    // REGISTER
    public void register(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(res -> callback.onSuccess(res.getUser()))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // LOGIN with Firestore check
// LOGIN
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(res -> {
                    FirebaseUser firebaseUser = res.getUser();
                    if (firebaseUser == null) {
                        callback.onFailure("Login failed: user not found");
                        return;
                    }

                    // Update isLoggedIn in Firestore
                    db.collection("users")
                            .document(firebaseUser.getUid())
                            .get()
                            .addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    // Set logged in true
                                    db.collection("users")
                                            .document(firebaseUser.getUid())
                                            .update("isLoggedIn", true);

                                    callback.onSuccess(firebaseUser);
                                } else {
                                    auth.signOut();
                                    callback.onFailure("User data not found in Firestore");
                                }
                            })
                            .addOnFailureListener(e -> {
                                auth.signOut();
                                callback.onFailure("Firestore error: " + e.getMessage());
                            });

                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void logout() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .update("isLoggedIn", false)
                    .addOnSuccessListener(a -> {
                        // Update successful হলে signOut
                        auth.signOut();
                    })
                    .addOnFailureListener(e -> {
                        // Error হলেও signOut
                        auth.signOut();
                    });
        } else {
            auth.signOut();
        }
    }



    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }
}
