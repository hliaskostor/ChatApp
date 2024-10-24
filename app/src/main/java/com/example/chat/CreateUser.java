package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CreateUser extends AppCompatActivity {
    EditText email, password, username;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        username = findViewById(R.id.editTextText5);
        password = findViewById(R.id.editTextTextPassword3);
        email = findViewById(R.id.editTextTextEmailAddress);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    public void create(View view) {
        if (!email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() &&
                !username.getText().toString().isEmpty()) {
            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                user = auth.getCurrentUser();
                                updateUser(user, username.getText().toString());
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference usersRef = db.collection("users").document(user.getUid());
                                Map<String, Object> usernameData = new HashMap<>();
                                usernameData.put("username", username.getText().toString());
                                usersRef.set(usernameData, SetOptions.merge());
                                showMessage("Success", "User profile created!");
                                Intent intent=new Intent(CreateUser.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                showMessage("Error", task.getException().getLocalizedMessage());
                            }
                        }
                    });
        } else {
            showMessage("Error", "Please provide all info!");
        }
    }


    private void updateUser(FirebaseUser user, String username) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        user.updateProfile(request);
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    void showMessage(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}
