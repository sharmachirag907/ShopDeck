package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shopdeck.Model.Users;
import com.example.shopdeck.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class Home extends AppCompatActivity {
    private Button login,register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        register= (Button) findViewById(R.id.register_main);
        login= (Button) findViewById(R.id.login_main);

        Paper.init(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Register.class);
                startActivity(intent);
            }
        });

        String UserPhoneKey = Paper.book().read((Prevalent.UserPhoneKey));
        String UserPasswordKey = Paper.book().read((Prevalent.UserPasswordKey));
        if (UserPhoneKey != "" && UserPasswordKey != ""){
            if (!TextUtils.isEmpty(UserPasswordKey) && !TextUtils.isEmpty(UserPasswordKey)){
                AllowAccess(UserPhoneKey,UserPasswordKey);
            }
        }
    }
    private void AllowAccess(String phone, String password) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference rootref=db.getReference();

        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()){
                    Users userdata= snapshot.child("Users").child(phone).getValue(Users.class);

                    if (userdata.getPhone().equals(phone)){
                        if (userdata.getPassword().equals(password)){

                            Toast.makeText(Home.this, "Already Logged in...", Toast.LENGTH_SHORT).show();


                            Prevalent.currentOnlineUser = userdata;

                            Intent intent=new Intent(Home.this, Nav_Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}