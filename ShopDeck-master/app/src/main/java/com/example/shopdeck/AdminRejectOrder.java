package com.example.shopdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminRejectOrder extends AppCompatActivity {
    private EditText reason;
    private Button reject,back;
    private DatabaseReference ordersRef;

    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reject_order);

        userID=getIntent().getStringExtra("uid");

        ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders");

        reason=findViewById(R.id.reason);
        reject=findViewById(R.id.send_reason);
        back=findViewById(R.id.backReject);

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AdminRejectOrder.this,AdminNewOrders.class);
                startActivity(intent);
            }
        });
    }

    private void check() {
        if (TextUtils.isEmpty(reason.getText().toString())){
            Toast.makeText(this, "Please Enter Reason", Toast.LENGTH_SHORT).show();
        }
        else
        {
            RejectOrder();
        }
    }

    private void RejectOrder() {
        String Reason = reason.getText().toString();
        String number = userID;

        DatabaseReference db =FirebaseDatabase.getInstance().getReference().child("Cart List");
        db.child("Admin View").child(number).child("Products")
                .removeValue();

        ordersRef.child(number).removeValue();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                sendSMS(Reason,number);

            }
            else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
            }
        }


    }

    private void sendSMS(String reason, String number) {
        final String saveCurrentTime,saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());


        String message = "Your ShopDeck order was rejected on "+saveCurrentDate+ " at " + saveCurrentTime + " For reason : \n" +reason ;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message,null,null);
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Message NOT sent", Toast.LENGTH_SHORT).show();
        }

        Intent intent= new Intent(AdminRejectOrder.this,AdminNewOrders.class);
        startActivity(intent);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminRejectOrder.this,AdminNewOrders.class));
        finish();
    }
}