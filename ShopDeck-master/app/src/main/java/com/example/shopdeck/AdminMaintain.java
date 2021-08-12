package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintain extends AppCompatActivity {
    private Button applyChange,deletebtn;
    private TextView name,desc;
    private EditText price;
    private ImageView imageView;

    private String productId = "";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain);

        productId = getIntent().getStringExtra("product_id");
        productRef= FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        applyChange=(Button)findViewById(R.id.maintain_btn);
        deletebtn=(Button)findViewById(R.id.delete_btn);
        name=(TextView) findViewById(R.id.maintain_product_Name);
        price=(EditText) findViewById(R.id.maintain_product_Price);
        desc=(TextView) findViewById(R.id.maintain_product_Desc);
        imageView=(ImageView) findViewById(R.id.maintain_product_Image);

        displayProductInfo();

        applyChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });
    }
    private void deleteProduct() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(AdminMaintain.this,AdminHome.class));
                finish();

                Toast.makeText(AdminMaintain.this, "Product is removed successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void applyChanges() {

        String PPrice = price.getText().toString();


        if (PPrice.equals("")){
            Toast.makeText(this, "Enter New Price", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("price", PPrice);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AdminMaintain.this, "Price updated successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(AdminMaintain.this,AdminHome.class));
                        finish();
                    }
                }
            });
        }

    }



    private void displayProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String Pname =snapshot.child("product_name").getValue().toString();
                    String Pdesc =snapshot.child("description").getValue().toString();
                    String Pimage =snapshot.child("image").getValue().toString();

                    name.setText(Pname);
                    desc.setText(Pdesc);
                    Picasso.get().load(Pimage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}