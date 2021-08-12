package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopdeck.Model.CartM;
import com.example.shopdeck.Model.Products;
import com.example.shopdeck.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrder extends AppCompatActivity  {
    private EditText nameET,phoneET,addressET,cityET;
    private Button confirmOrder;
    public String NameET,NumberET;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = Rs." + totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrder =(Button)findViewById(R.id.confirm_order);
        nameET=(EditText)findViewById(R.id.shipment_name);
        phoneET=(EditText)findViewById(R.id.shipment_PhoneNo);
        addressET=(EditText)findViewById(R.id.shipment_address);
        cityET=(EditText)findViewById(R.id.city_name);

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });
    }
    private void Check() {
        if (TextUtils.isEmpty(nameET.getText().toString())){
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneET.getText().toString())){
            Toast.makeText(this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressET.getText().toString())){
            Toast.makeText(this, "Please Enter Address", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityET.getText().toString())){
            Toast.makeText(this, "Please Enter City", Toast.LENGTH_SHORT).show();
        }
        else if (phoneET.length() <10) {
            Toast.makeText(this, "Phone number must be of 10 digit", Toast.LENGTH_SHORT).show();
        }
        else {
            confirmOrder();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        NameET=Prevalent.currentOnlineUser.getName();
        nameET.setText(NameET);

        NumberET=Prevalent.currentOnlineUser.getPhone();
        phoneET.setText(NumberET);
    }

    private void confirmOrder() {
        final String saveCurrentTime,saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        String orderID = saveCurrentDate + "  " +saveCurrentTime;

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        /*final DatabaseReference allOrders =FirebaseDatabase.getInstance().getReference().child("All Orders").child(Prevalent.currentOnlineUser.getPhone()).child(orderID);
        HashMap<String, Object> allOrder =new HashMap<>();
        allOrder.put("totalAmount", totalAmount);
        allOrder.put("name", nameET.getText().toString());
        allOrder.put("phone", phoneET.getText().toString());
        allOrder.put("address", addressET.getText().toString());
        allOrder.put("city", cityET.getText().toString());
        allOrder.put("date", saveCurrentDate);
        allOrder.put("time", saveCurrentTime);
        allOrders.updateChildren(allOrder);*/



        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("name", nameET.getText().toString());
        orderMap.put("phone", phoneET.getText().toString());
        orderMap.put("address", addressET.getText().toString());
        orderMap.put("city", cityET.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("status", "not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        //Notification
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(ConfirmOrder.this,"My Notification");
                                        builder.setContentTitle("ShopDeck");
                                        builder.setContentText("Order placed successfully");
                                        builder.setSmallIcon(R.drawable.icon);
                                        builder.setAutoCancel(false);
                                        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Order placed successfully"));
                                        builder.setPriority(NotificationCompat.PRIORITY_HIGH);


                                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ConfirmOrder.this);
                                        managerCompat.notify(1,builder.build());

                                        Toast.makeText(ConfirmOrder.this, "order placed successfully", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ConfirmOrder.this,Nav_Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ConfirmOrder.this,Cart.class));
        finish();
    }
}