package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Admin_AddProduct extends AppCompatActivity {
    private String categoryName,Desc,Price,PName,SaveDate,SaveTime;
    private ImageView ProductImage;
    private Button AddNewProduct;
    private EditText ProductName,ProductDesc,ProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productKey, downloadImageurl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__add_product);
        ProductImage=(ImageView)findViewById(R.id.product_Image);
        AddNewProduct=(Button)findViewById(R.id.add_Product);
        ProductName=(EditText)findViewById(R.id.product_Name);
        ProductDesc=(EditText)findViewById(R.id.product_Desc);
        ProductPrice=(EditText)findViewById(R.id.product_Price);
        LoadingBar=new ProgressDialog(this);

        categoryName=getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent =new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode== RESULT_OK && data!=null){
            ImageUri=data.getData();
            ProductImage.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData(){
        Desc = ProductDesc.getText().toString();
        Price = ProductPrice.getText().toString();
        PName = ProductName.getText().toString();

        if (ImageUri == null){
            Toast.makeText(this, "Product Image Required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(PName)){
            Toast.makeText(this, "Please Enter Product Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Desc)){
            Toast.makeText(this, "Please Enter Product Description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Please Enter Product Price", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProduct();
        }
    }

    private void StoreProduct() {

        LoadingBar.setTitle("Adding New Product");
        LoadingBar.setMessage("Loading...Please wait");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentdate = new SimpleDateFormat("MM dd,yyyy");
        SaveDate = currentdate.format(calendar.getTime());

        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        SaveTime = currenttime.format(calendar.getTime());

        productKey = SaveDate + SaveTime;

        StorageReference filepath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productKey + ".jpg");

        final UploadTask uploadTask = filepath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(Admin_AddProduct.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                LoadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Admin_AddProduct.this, "Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageurl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageurl = task.getResult().toString();

                            Toast.makeText(Admin_AddProduct.this, "Got image Url successfully...", Toast.LENGTH_SHORT).show();

                            SaveInfoToDatabase();
                        }
                    }
                });
            }
        });
    }


    private void SaveInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("product_id", productKey);
        productMap.put("date", SaveDate);
        productMap.put("time", SaveTime);
        productMap.put("description", Desc);
        productMap.put("image", downloadImageurl);
        productMap.put("category", categoryName);
        productMap.put("price", Price);
        productMap.put("product_name", PName);

        ProductsRef.child(productKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent=new Intent(Admin_AddProduct.this,Admin_ProductCategory.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            LoadingBar.dismiss();
                            Toast.makeText(Admin_AddProduct.this, "Product Added Successfully...", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            LoadingBar.dismiss();
                            String message =task.getException().toString();
                            Toast.makeText(Admin_AddProduct.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}