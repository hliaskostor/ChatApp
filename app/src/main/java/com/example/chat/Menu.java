package com.example.chat;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {
    ListView viewUsers;
    FirebaseFirestore usersShow;
    String username;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        username = getIntent().getStringExtra("username");
        viewUsers = findViewById(R.id.viewUsers);
        usersShow = FirebaseFirestore.getInstance();
        listUsers();
    }

    public void logout(View view){
        auth.signOut();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(Menu.this,"Logout Successfull",Toast.LENGTH_SHORT).show();
    }

    public void listUsers() {
        usersShow.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> userList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            userList.add(username);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Menu.this, android.R.layout.simple_list_item_1, userList);
                        viewUsers.setAdapter(adapter);
                        viewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String selectedUser = parent.getItemAtPosition(position).toString();
                                Intent intent = new Intent(Menu.this, Chat.class);
                                intent.putExtra("username", username);
                                intent.putExtra("selectedUser", selectedUser);
                                startActivity(intent);
                            }
                        });
                    }
                });
    }
}



