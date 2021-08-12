package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopdeck.Model.Products;
import com.example.shopdeck.Prevalent.Prevalent;
import com.example.shopdeck.ViewHolder.ProductViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetails extends AppCompatActivity {
    private Button addtocart,addtowishlist;
    private ImageView productImage;
    private TextView productprice,productDesc,productName;
    private EditText productQuantity;
    private String productId = "", status ="Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("product_id");


        addtocart =(Button)findViewById(R.id.add_cart_btn);
        addtowishlist =(Button)findViewById(R.id.add_wishlist_btn);
        productImage =(ImageView)findViewById(R.id.product_image_details);
        productprice =(TextView)findViewById(R.id.product_price_details);
        productName =(TextView)findViewById(R.id.product_name_details);
        productDesc =(TextView)findViewById(R.id.product_desc_details);
        productQuantity=(EditText)findViewById(R.id.product_quantity_details);

        getProductDetails(productId);


        addtowishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToWishList();
            }
        });


        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Quantity = productQuantity.getText().toString().trim();

                if (status.equals("Order Placed") || status.equals("Order Shipped")){
                    Toast.makeText(ProductDetails.this, "You can place more order after your previous order is confirmed.", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(Quantity)) {
                    Toast.makeText(ProductDetails.this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                }
                else if (Quantity.length() >1){
                    Toast.makeText(ProductDetails.this, "Quantity must be less than 9", Toast.LENGTH_SHORT).show();
                }
                else if (Quantity.equals("0")){
                    Toast.makeText(ProductDetails.this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                }
                else {
                    addingToCartList();
                }
            }
        });
    }

    private void addingToWishList() {


        String saveCurrentTime,saveCurrentDate,orderId;


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        orderId = saveCurrentDate+saveCurrentTime;

        FirebaseDatabase db=FirebaseDatabase.getInstance();


        final DatabaseReference cartListRef=db.getReference().child("Wish List");
        final HashMap<String , Object> cartMap = new HashMap<>();
        cartMap.put("product_id", productId);
        cartMap.put("product_name", productName.getText().toString());
        cartMap.put("price", productprice.getText().toString());
        cartMap.put("description", productDesc.getText().toString());


        cartListRef.child(Prevalent.currentOnlineUser.getPhone())
                .child(productId).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ProductDetails.this, "Added to Wish list", Toast.LENGTH_SHORT).show();

                            Intent intent= new Intent(ProductDetails.this,Nav_Home.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(ProductDetails.this, "Error1", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void getProductDetails(String productId) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);

                    productName.setText(products.getProduct_name());
                    productprice.setText(products.getPrice());
                    productDesc.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
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
    }

    private void addingToCartList() {


        String saveCurrentTime,saveCurrentDate,orderId;


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        orderId = saveCurrentDate+saveCurrentTime;

        FirebaseDatabase db=FirebaseDatabase.getInstance();

        final DatabaseReference orders = db.getReference().child("orderProduct");
        final HashMap<String , Object> orderMap = new HashMap<>();
        orderMap.put("product_id", productId);
        orderMap.put("product_name", productName.getText().toString());
        orderMap.put("price", productprice.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("quantity", productQuantity.getText().toString());
        orders.child(Prevalent.currentOnlineUser.getPhone()).child(productId).updateChildren(orderMap);


        final DatabaseReference cartListRef=db.getReference().child("Cart List");
        final HashMap<String , Object> cartMap = new HashMap<>();
        cartMap.put("product_id", productId);
        cartMap.put("product_name", productName.getText().toString());
        cartMap.put("price", productprice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", productQuantity.getText().toString());


        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProductDetails.this, "Added to cart list", Toast.LENGTH_SHORT).show();

                                                Intent intent= new Intent(ProductDetails.this,Nav_Home.class);
                                                startActivity(intent);
                                            }
                                            else {
                                                Toast.makeText(ProductDetails.this, "Error2", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(ProductDetails.this, "Error1", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void CheckOrderStatus(){
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingStatus = snapshot.child("status").getValue().toString();


                    if (shippingStatus.equals("shipped")){
                        status ="Order Shipped";
                    }
                    else if (shippingStatus.equals("not shipped")){
                        status ="Order Placed";
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

        startActivity(new Intent(ProductDetails.this,Nav_Home.class));

    }


}