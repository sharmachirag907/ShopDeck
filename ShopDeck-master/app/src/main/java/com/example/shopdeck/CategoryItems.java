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
import android.widget.TextView;

import com.example.shopdeck.Model.Products;
import com.example.shopdeck.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CategoryItems extends AppCompatActivity {
    private TextView CategoryNameHeader;
    private String categoryName;
    private RecyclerView categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        CategoryNameHeader=(TextView)findViewById(R.id.categoryNameHeader);
        categoryList=findViewById(R.id.categoryList);
        categoryList.setLayoutManager(new LinearLayoutManager(CategoryItems.this));

        categoryName=getIntent().getExtras().get("category").toString();

    }

    @Override
    protected void onStart() {
        super.onStart();

        CategoryNameHeader.setText(categoryName);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("category").startAt(categoryName).endAt(categoryName),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products)
            {
                productViewHolder.txtproductName.setText(products.getProduct_name());
                productViewHolder.txtproductPrice.setText("Price: " + " ₹ " + products.getPrice() );
                Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent intent=new Intent(CategoryItems.this,ProductDetails.class);
                            intent.putExtra("product_id", products.getProduct_id());
                            startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        categoryList.setAdapter(adapter);
        adapter.startListening();
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CategoryItems.this,Category.class));
        finish();
    }
}
