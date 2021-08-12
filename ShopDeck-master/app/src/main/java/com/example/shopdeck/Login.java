package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopdeck.Model.Users;
import com.example.shopdeck.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    private EditText Login_phonenumber,Login_password;
    private Button Loginbtn;
    private ProgressDialog LoadingBar;
    private CheckBox RememberMe;
    private TextView Admin,NotAdmin;
    private String parentDB = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login_phonenumber = (EditText) findViewById(R.id.login_Contact);
        Login_password = (EditText) findViewById(R.id.login_Password);
        Loginbtn = (Button) findViewById(R.id.login);
        LoadingBar=new ProgressDialog(this);
        RememberMe=(CheckBox) findViewById(R.id.remember_me);

        Paper.init(this);

        Admin= (TextView) findViewById(R.id.admin);
        NotAdmin= (TextView) findViewById(R.id.notAdmin);


        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Loginbtn.setText("Admin Login");
                Admin.setVisibility(View.INVISIBLE);
                NotAdmin.setVisibility(View.VISIBLE);
                RememberMe.setVisibility(View.INVISIBLE);
                parentDB = "Admins";
            }
        });

        NotAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loginbtn.setText("Login");
                Admin.setVisibility(View.VISIBLE);
                NotAdmin.setVisibility(View.INVISIBLE);
                RememberMe.setVisibility(View.VISIBLE);
                parentDB = "Users";
            }
        });


        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }




            private void LoginUser() {
                String phone = Login_phonenumber.getText().toString().trim();
                String password = Login_password.getText().toString().trim();

                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(Login.this, "Please enter your Phone number", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    LoadingBar.setTitle("Loging in");
                    LoadingBar.setMessage("Loading...Please wait");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();

                    LogIn(phone, password);
                }
            }



            private void LogIn(final String phone,final String password) {

                if(RememberMe.isChecked()){
                    Paper.book().write(Prevalent.UserPhoneKey, phone);
                    Paper.book().write(Prevalent.UserPasswordKey, password);
                }

                FirebaseDatabase db=FirebaseDatabase.getInstance();
                DatabaseReference rootref=db.getReference();

                rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(parentDB).child(phone).exists()){
                            Users userdata= snapshot.child(parentDB).child(phone).getValue(Users.class);

                            if (userdata.getPhone().equals(phone)){
                                if (userdata.getPassword().equals(password)){
                                    if (parentDB.equals("Admins"))
                                    {
                                        Toast.makeText(Login.this, "Logged in Successfully...", Toast.LENGTH_SHORT).show();
                                        LoadingBar.dismiss();

                                        Prevalent.currentOnlineUser = userdata;

                                        Intent intent=new Intent(Login.this, AdminHome.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else if (parentDB.equals("Users"))
                                    {
                                        Toast.makeText(Login.this, "Logged in Successfully...", Toast.LENGTH_SHORT).show();
                                        LoadingBar.dismiss();

                                        Intent intent=new Intent(Login.this, Nav_Home.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        Prevalent.currentOnlineUser = userdata;
                                        startActivity(intent);
                                    }

                                }
                                else{
                                    LoadingBar.dismiss();
                                    Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else{
                            Toast.makeText(Login.this, "Account with Phone number "+phone+" does not exists..", Toast.LENGTH_SHORT).show();
                            LoadingBar.dismiss();
                            Toast.makeText(Login.this, "Create new account", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this,Register.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}