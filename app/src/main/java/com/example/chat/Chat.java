package com.example.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chat extends AppCompatActivity {

    TextView textView2, Messages;
    EditText message;
    String username, selectedUser;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        textView2 = findViewById(R.id.textView2);
        auth = FirebaseAuth.getInstance();
        FirebaseUser connectUser = auth.getCurrentUser();
        if (connectUser != null) {
            username= connectUser.getDisplayName();
        } else {
            username = "";
        }
        Messages = findViewById(R.id.textView3);
        Messages.setText("");
        message = findViewById(R.id.editTextText3);
        selectedUser = getIntent().getStringExtra("selectedUser");
        loadMessages(selectedUser);
    }

    public void back(View view) {
        Intent intent = new Intent(this, Menu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void loadMessages(String selectedUser) {
        String ConversationID = Conversation(username, selectedUser);
        textView2.setText("Chat with " + selectedUser);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("messages").child(ConversationID);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Messages.setText("");
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String message = childSnapshot.getValue(String.class);
                    Messages.append(message + "\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String Conversation(String connectUser, String chatUser) {
        if (TextUtils.isEmpty(connectUser)) {
            connectUser = "";
        }

        if (TextUtils.isEmpty(chatUser)) {
            chatUser = "";
        }

        return (connectUser.compareTo(chatUser) < 0) ? connectUser + "_" + chatUser : chatUser + "_" + connectUser;
    }

    public void send(View view) {
        if (!TextUtils.isEmpty(message.getText().toString().trim())) {
            sendMessage(username, selectedUser, message.getText().toString());
            message.setText("");
            loadMessages(selectedUser);
        }
    }

    private void sendMessage(String sender, String receiver, String messageText) {
        String conversationID = Conversation(sender, receiver);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("messages").child(conversationID);
        String key = userReference.push().getKey();
        if (key != null) {
            userReference.child(key).setValue(sender + ": " + messageText);
        }
    }
}
