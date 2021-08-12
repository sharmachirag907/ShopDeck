package com.example.shopdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Admin_ProductCategory extends AppCompatActivity {
    private Button Fruits,Grocery,Chocolates,Stationaries,AdminHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__product_category);

        Fruits = (Button) findViewById(R.id.fruits);
        Grocery = (Button) findViewById(R.id.grocery);
        Chocolates = (Button) findViewById(R.id.chocolates);
        Stationaries = (Button) findViewById(R.id.stationaries);
        AdminHome = (Button) findViewById(R.id.adminHome);


        Fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin_ProductCategory.this,Admin_AddProduct.class);
                intent.putExtra("category","Fruits");
                startActivity(intent);
            }
        });

        Grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin_ProductCategory.this,Admin_AddProduct.class);
                intent.putExtra("category","Grocery");
                startActivity(intent);
            }
        });

        Chocolates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin_ProductCategory.this,Admin_AddProduct.class);
                intent.putExtra("category","Chocolates");
                startActivity(intent);
            }
        });

        Stationaries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin_ProductCategory.this,Admin_AddProduct.class);
                intent.putExtra("category","Stationaries");
                startActivity(intent);
            }
        });

        AdminHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Admin_ProductCategory.this, AdminHome.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Admin_ProductCategory.this, AdminHome.class));
        finish();
    }
}