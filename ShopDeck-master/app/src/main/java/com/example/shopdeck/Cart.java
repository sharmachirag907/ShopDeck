package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopdeck.Model.CartM;
import com.example.shopdeck.Model.Products;
import com.example.shopdeck.Prevalent.Prevalent;
import com.example.shopdeck.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Cart extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextBtn;
    private TextView totalAmount, cartMsg;

    private int TotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextBtn=(Button)findViewById(R.id.next);
        totalAmount=(TextView)findViewById(R.id.total_amount);
        cartMsg=(TextView)findViewById(R.id.cart_msg);

        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkCart();
            }
        });
    }

    private void checkCart() {
        final DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("User View").exists()){
                    Intent intent= new Intent(Cart.this, ConfirmOrder.class);
                    intent.putExtra("Total Price",String.valueOf(TotalPrice));
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Cart.this, "Cart is Empty!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderStatus();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final DatabaseReference orderListRef = FirebaseDatabase.getInstance().getReference().child("orderProduct");

        FirebaseRecyclerOptions<CartM> options =
                new FirebaseRecyclerOptions.Builder<CartM>()
                        .setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                .child("Products"), CartM.class)
                        .build();

        FirebaseRecyclerAdapter<CartM, CartViewHolder> adapter
                =new FirebaseRecyclerAdapter<CartM, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull CartM cartM) {

                int oneTypeProductPrice =((Integer.valueOf(cartM.getPrice()))) * Integer.valueOf(cartM.getQuantity());
                TotalPrice = TotalPrice + oneTypeProductPrice;
                totalAmount.setText("Total Price = Rs." + String.valueOf(TotalPrice));

                cartViewHolder.txtProductQuantity.setText("Quantity : " + cartM.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price : RS. " + String.valueOf(oneTypeProductPrice));
                cartViewHolder.txtProductName.setText(cartM.getProduct_name());


                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]{
                                "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
                        builder.setTitle("Cart Option");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products").child(cartM.getProduct_id())
                                            .removeValue();

                                    orderListRef.child(Prevalent.currentOnlineUser.getPhone()).child(cartM.getProduct_id())
                                            .removeValue();

                                    cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products").child(cartM.getProduct_id())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(Cart.this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(Cart.this,Nav_Home.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderStatus(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingStatus = snapshot.child("status").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    if (shippingStatus.equals("shipped")){
                        totalAmount.setText("Dear " + userName +"\n order confirmed successfully.");
                        recyclerView.setVisibility(View.GONE);

                        cartMsg.setVisibility(View.VISIBLE);
                        cartMsg.setText("Congratulations, Your order is confirmed. We will Deliver it soon.");
                        NextBtn.setVisibility(View.GONE);


                    }
                    else if (shippingStatus.equals("not shipped")){
                        totalAmount.setText("Shipping Status = Not Shipped");
                        recyclerView.setVisibility(View.GONE);

                        cartMsg.setVisibility(View.VISIBLE);
                        NextBtn.setVisibility(View.GONE);

                        Toast.makeText(Cart.this, "You Can purchase more after previous order is confirmed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Cart.this,Nav_Home.class));
        finish();
    }
}