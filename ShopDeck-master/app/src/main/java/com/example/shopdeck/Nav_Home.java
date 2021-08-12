package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shopdeck.Model.Products;
import com.example.shopdeck.Prevalent.Prevalent;
import com.example.shopdeck.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class Nav_Home extends AppCompatActivity {

    DrawerLayout drawer;
    private DatabaseReference ProductsRef;
    ActionBarDrawerToggle toggle;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String type ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav__home);

        Intent intent=getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            type=getIntent().getExtras().get("Admin").toString();
        }

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Paper.init(this);

        recyclerView=findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.userName);

        if (!type.equals("Admin")){
            userName.setText(Prevalent.currentOnlineUser.getName());
        }

        
        if (!type.equals("Admin")){
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.Category:
                            startActivity(new Intent(getApplicationContext(),Category.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.cart:
                            startActivity(new Intent(getApplicationContext(),Cart.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.wishlist:
                            startActivity(new Intent(getApplicationContext(),WishList.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.Orders:
                            startActivity(new Intent(getApplicationContext(),AllOrders.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.About_Us:
                            startActivity(new Intent(getApplicationContext(),AboutUs.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.contact_us:
                            startActivity(new Intent(getApplicationContext(),ContactUs.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.feedback:
                            startActivity(new Intent(getApplicationContext(),Feedback.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;

                        case R.id.setting:
                            startActivity(new Intent(getApplicationContext(),Settings.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;


                        case R.id.logout:

                            CharSequence options[] = new CharSequence[]{
                                    "Yes",
                                    "No"
                            };

                            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(Nav_Home.this);
                            builder.setTitle("Confirm Logout");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Paper.book().destroy();
                                        startActivity(new Intent(Nav_Home.this,Home.class));
                                        finish();
                                    }
                                    else {
                                        startActivity(new Intent(Nav_Home.this,Nav_Home.class));
                                        finish();
                                    }
                                }
                            });
                            builder.show();


                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef, Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtproductName.setText(products.getProduct_name());
                        productViewHolder.txtproductPrice.setText("Price: " + " ₹ " + products.getPrice() );
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (type.equals("Admin")){
                                    Intent intent=new Intent(Nav_Home.this,AdminMaintain.class);
                                    intent.putExtra("product_id", products.getProduct_id());
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent=new Intent(Nav_Home.this,ProductDetails.class);
                                    intent.putExtra("product_id", products.getProduct_id());
                                    startActivity(intent);
                                }

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }



    @Override
    public void onBackPressed() {
        if (!type.equals("Admin")){
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
        }
        else {
            startActivity(new Intent(Nav_Home.this,AdminHome.class));
        }
    }


}
