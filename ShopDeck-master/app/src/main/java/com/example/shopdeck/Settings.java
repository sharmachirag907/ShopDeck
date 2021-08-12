package com.example.shopdeck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopdeck.Prevalent.Prevalent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Settings extends AppCompatActivity {
    private EditText fullNameEditText,passwordEditText,cpasswordEditText;
    private TextView closeTextbtn,saveTextBtn;
    private String NameSetting,PasswordSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fullNameEditText=(EditText)findViewById(R.id.setting_Name);
        passwordEditText=(EditText)findViewById(R.id.setting_Password);
        cpasswordEditText=(EditText)findViewById(R.id.setting_CPassword);
        closeTextbtn=(TextView)findViewById(R.id.close_settings);
        saveTextBtn=(TextView)findViewById(R.id.update_settings);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification2","My Notification2", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        closeTextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this,Nav_Home.class));
            }
        });

        saveTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=fullNameEditText.getText().toString();
                String password=passwordEditText.getText().toString();
                String cpassword=cpasswordEditText.getText().toString();

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(Settings.this, "Name Field is Empty", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(Settings.this, "Old Password Field is Empty", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(cpassword)){
                    Toast.makeText(Settings.this, "New Password Field is Empty", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(Prevalent.currentOnlineUser.getPassword())){
                    Toast.makeText(Settings.this, "old Password is incorrect!!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    updateUserInfo();
                }
            }
        });
    }


    private void updateUserInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap =new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("password",cpasswordEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        //Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Settings.this,"My Notification2");
        builder.setContentTitle("ShopDeck");
        builder.setContentText("Profile updated Successfully... \nrestart app to reflect changes");
        builder.setSmallIcon(R.drawable.icon);
        builder.setAutoCancel(false);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Profile updated Successfully... \nRestart app to reflect changes"));
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Settings.this);
        managerCompat.notify(2,builder.build());


        startActivity(new Intent(Settings.this,Nav_Home.class));
        Toast.makeText(this, "Profile Info Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Settings.this,Nav_Home.class));
        finish();
    }
}