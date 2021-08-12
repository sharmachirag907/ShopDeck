package com.example.shopdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopdeck.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Feedback extends AppCompatActivity {
    private EditText Feedback;
    private Button Send_Feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Feedback=findViewById(R.id.feedback);
        Send_Feedback=findViewById(R.id.add_Feedback);

        Send_Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFeedback();
            }
        });
    }

    private void SendFeedback() {
        String feedback = Feedback.getText().toString();
        if (TextUtils.isEmpty(feedback)){
            Toast.makeText(this, "Enter Feedback", Toast.LENGTH_SHORT).show();
        }
        else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Feedback").child(Prevalent.currentOnlineUser.getPhone());

            String user =Prevalent.currentOnlineUser.getPhone();
            HashMap<String, Object> hashMap =new HashMap<>();

            hashMap.put("user", user);
            hashMap.put("feedback",feedback);

            reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Feedback.this, "Feedback Sent Successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Feedback.this,Nav_Home.class));
                    }
                    else{
                        Toast.makeText(Feedback.this, "Error Occurred...Try again later", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Feedback.this,Nav_Home.class));
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Feedback.this,Nav_Home.class));
        finish();
    }
}