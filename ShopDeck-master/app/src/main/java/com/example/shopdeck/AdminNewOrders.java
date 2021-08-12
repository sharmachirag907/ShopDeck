package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopdeck.Model.AdminOrders;
import com.example.shopdeck.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminNewOrders extends AppCompatActivity {
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);


        ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList=findViewById(R.id.new_orders);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef, AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewholder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewholder adminOrdersViewholder, int i, @NonNull AdminOrders adminOrders) {
                        adminOrdersViewholder.userName.setText("Name: " + adminOrders.getName());
                        adminOrdersViewholder.userPhoneNumber.setText("Phone: " + adminOrders.getPhone());
                        adminOrdersViewholder.userTotalPrice.setText("Total Amount= Rs. " + adminOrders.getTotalAmount());
                        adminOrdersViewholder.userDateTime.setText("Order at: " + adminOrders.getDate() + " " + adminOrders.getTime());
                        adminOrdersViewholder.userShippingAddress.setText("Shipping Address: " + adminOrders.getAddress() + " " + adminOrders.getCity());

                        adminOrdersViewholder.ShowOrder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID =getRef(i).getKey();

                                Intent intent= new Intent(AdminNewOrders.this,AdminUserProducts.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });

                        adminOrdersViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Yes",
                                        "No",
                                        "Cancel"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrders.this);
                                builder.setTitle("Confirm Order?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            String uID =getRef(i).getKey();
                                            RemoveOrder(uID);

                                            //SMS Code
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                                                    sendSMS(uID);
                                                }
                                                else {
                                                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},1);
                                                }
                                            }


                                            DatabaseReference db =FirebaseDatabase.getInstance().getReference().child("Cart List");
                                            db.child("Admin View").child(uID).child("Products")
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()){
                                                        Toast.makeText(AdminNewOrders.this, "Error !!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }

                                        else if (which == 1){
                                            String uID =getRef(i).getKey();
                                            Intent intent= new Intent(AdminNewOrders.this,AdminRejectOrder.class);
                                            intent.putExtra("uid", uID);
                                            startActivity(intent);
                                        }

                                        else {
                                            finish();
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewholder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }




    public static class AdminOrdersViewholder extends RecyclerView.ViewHolder{

        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
        public Button ShowOrder;

        public AdminOrdersViewholder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.order_user_name);
            userPhoneNumber=itemView.findViewById(R.id.order_phone_number);
            userTotalPrice=itemView.findViewById(R.id.order_total_price);
            userDateTime=itemView.findViewById(R.id.order_date_time);
            userShippingAddress=itemView.findViewById(R.id.order_address_city);
            ShowOrder=itemView.findViewById(R.id.show_products);
        }
    }

    private void RemoveOrder(String uID)
    {
        ordersRef.child(uID).removeValue();
    }

    private void sendSMS(String uID) {
        final String saveCurrentTime,saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        String phoneNo = uID.toString();
        String message = "Your ShopDeck order was confirmed on "+saveCurrentDate+ " at " + saveCurrentTime;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message,null,null);
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Message NOT sent", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminNewOrders.this,AdminHome.class));
        finish();
    }
}