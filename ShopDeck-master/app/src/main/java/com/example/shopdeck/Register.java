package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private Button Registerbtn;
    private EditText Name,PhoneNumber,Password,cpasswordEditText;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Registerbtn = (Button) findViewById(R.id.register);
        Name = (EditText) findViewById(R.id.register_Name);
        PhoneNumber = (EditText) findViewById(R.id.register_Contact);
        Password = (EditText) findViewById(R.id.register_Password);
        cpasswordEditText=(EditText)findViewById(R.id.register_cPassword);
        LoadingBar= new ProgressDialog(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification3","My Notification3", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        Registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateAccount();
            }
        });
    }
    private void CreateAccount()
    {
        String name = Name.getText().toString().trim();
        String phone = PhoneNumber.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String cpassword=cpasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter your Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please enter your Phone number", Toast.LENGTH_SHORT).show();
        }
        else if (phone.length() <10){
            Toast.makeText(this, "Phone number must be of 10 digit", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your Password", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cpassword)){
            Toast.makeText(this, "Please enter Confirm Password", Toast.LENGTH_SHORT).show();
        }
        else if (!cpassword.equals(password)){
            Toast.makeText(this, "Passwords not matching !!", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() <6){
            Toast.makeText(this, "Length of password should be greater than 6 characters", Toast.LENGTH_SHORT).show();
        }
        else{
            LoadingBar.setTitle("Creating Account");
            LoadingBar.setMessage("Loading...Please wait");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();

            ValidatephoneNumber(name, phone, password);
        }
    }


    private void ValidatephoneNumber(String name, String phone, String password)
    {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference rootref=db.getReference();

        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if ((snapshot.child("Users").child(phone).exists()))
                {
                    Toast.makeText(Register.this, "ERROR: " + phone + " Already exists ", Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                    Toast.makeText(Register.this, "please try again using another number", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(Register.this,Home.class));
                }
                else
                {
                    HashMap<String, Object> userdatamap =new HashMap<>();
                    userdatamap.put("name", name);
                    userdatamap.put("password", password);
                    userdatamap.put("phone", phone);

                    rootref.child("Users").child(phone).updateChildren(userdatamap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(Register.this, "Account has been created successfully", Toast.LENGTH_SHORT).show();
                                        LoadingBar.dismiss();

                                        //Notification
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(Register.this,"My Notification3");
                                        builder.setContentTitle("ShopDeck");
                                        builder.setContentText("Account Created successfully");
                                        builder.setSmallIcon(R.drawable.icon);
                                        builder.setAutoCancel(false);
                                        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Account Created successfully"));
                                        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

                                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Register.this);
                                        managerCompat.notify(2,builder.build());



                                        Intent intent=new Intent(Register.this, Login.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else{
                                        LoadingBar.dismiss();
                                        Toast.makeText(Register.this, "Some error occured...please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}