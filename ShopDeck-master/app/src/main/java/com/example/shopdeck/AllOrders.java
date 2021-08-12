package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.shopdeck.Model.AdminOrders;
import com.example.shopdeck.Model.CartM;
import com.example.shopdeck.Model.OrderM;
import com.example.shopdeck.Prevalent.Prevalent;
import com.example.shopdeck.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllOrders extends AppCompatActivity {
    private RecyclerView AllOrdersList;
    private DatabaseReference ordersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        AllOrdersList=findViewById(R.id.orderList);
        AllOrdersList.setHasFixedSize(true);
        AllOrdersList.setLayoutManager(new LinearLayoutManager(AllOrders.this));

        ordersRef = FirebaseDatabase.getInstance().getReference().child("orderProduct").child(Prevalent.currentOnlineUser.getPhone());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<OrderM> options=
                new FirebaseRecyclerOptions.Builder<OrderM>()
                        .setQuery(ordersRef, OrderM.class).build();

        FirebaseRecyclerAdapter<OrderM, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<OrderM, OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i, @NonNull OrderM orderM) {
                        int tp =((Integer.valueOf(orderM.getPrice()))) * Integer.valueOf(orderM.getQuantity());

                        orderViewHolder.txtProductQuantity.setText("Quantity : " + orderM.getQuantity());
                        orderViewHolder.txtProductPrice.setText("Price : RS." + String.valueOf(tp));
                        orderViewHolder.txtProductName.setText(orderM.getProduct_name());
                        orderViewHolder.txtProductDate.setText("Date : " + orderM.getDate());
                    }

                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items_layout, parent, false);
                        return new OrderViewHolder(view);
                    }
                };
        AllOrdersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        public TextView txtProductName, txtProductPrice, txtProductQuantity, txtProductDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.cart_product_name);
            txtProductPrice= itemView.findViewById(R.id.cart_product_price);
            txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
            txtProductDate= itemView.findViewById(R.id.datetv);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AllOrders.this,Nav_Home.class));
        finish();
    }
}