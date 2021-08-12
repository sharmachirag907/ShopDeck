package com.example.shopdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminHome extends AppCompatActivity {

    private Button AddProduct,CheckOrders,maintainProduct,CheckFeedback,Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        AddProduct=(Button) findViewById(R.id.addProduct);
        maintainProduct=(Button) findViewById(R.id.maintain);
        CheckOrders=(Button) findViewById(R.id.check_orders);
        CheckFeedback=(Button)findViewById(R.id.check_feedbacks);
        Logout=(Button) findViewById(R.id.admin_logout);

        AddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHome.this,Admin_ProductCategory.class);
                startActivity(intent);
            }
        });

        maintainProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHome.this,Nav_Home.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });

        CheckOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHome.this,AdminNewOrders.class);
                startActivity(intent);
            }
        });

        CheckFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHome.this,AdminFeedback.class);
                startActivity(intent);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHome.this,Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AdminHome.this,Login.class));
        finish();
    }
}