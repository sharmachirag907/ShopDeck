package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shopdeck.Model.CartM;
import com.example.shopdeck.Prevalent.Prevalent;
import com.example.shopdeck.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class OrdersProduct extends AppCompatActivity {
    private RecyclerView ProductsList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_product);

        ProductsList=findViewById(R.id.products_list);
        ProductsList.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        ProductsList.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference productsref = FirebaseDatabase.getInstance().getReference().child("OrderViewCart").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products");

        FirebaseRecyclerOptions<CartM> options =
                new FirebaseRecyclerOptions.Builder<CartM>()
                .setQuery(productsref, CartM.class).build();

        FirebaseRecyclerAdapter<CartM, CartViewHolder> adapter = new FirebaseRecyclerAdapter<CartM, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull CartM cartM) {
                cartViewHolder.txtProductQuantity.setText("Quantity : " + cartM.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price : RS." + cartM.getPrice());
                cartViewHolder.txtProductName.setText(cartM.getProduct_name());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        ProductsList.setAdapter(adapter);
        adapter.startListening();
    }
}