package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class MainActivity extends AppCompatActivity {
    EditText email,password;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        password=findViewById(R.id.password);
        email=findViewById(R.id.email);
        auth = FirebaseAuth.getInstance();
    }
    public void signup(View view){
        Intent intent = new Intent(this, CreateUser.class);
        startActivity(intent);
    }
    public void signin(View view){
        String enteredEmail = email.getText().toString().trim();
        String enteredPassword = password.getText().toString().trim();
        if(!enteredEmail.isEmpty() && !enteredPassword.isEmpty()){
            auth.signInWithEmailAndPassword(enteredEmail, enteredPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    String username = user.getDisplayName();
                                    if (username == null) {
                                        username = user.getEmail();
                                    }
                                    showMessage("Success","User signed in successfully!");
                                    Intent intent = new Intent(MainActivity.this, Menu.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                }
                            } else {
                                showMessage("Error", task.getException().getLocalizedMessage());
                            }
                        }
                    });
        } else {
            showMessage("Error", "Please enter both email and password.");
        }
    }
    void showMessage(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}