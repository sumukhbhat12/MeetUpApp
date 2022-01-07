package com.example.exposysdatalabproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exposysdatalabproject.firebase.MessagingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class MainMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_main);
        //Firebase
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser user = fauth.getCurrentUser();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        Button logout = findViewById(R.id.logoutbtn);
        TextView username = findViewById(R.id.usernamemain);
        TextView verify = findViewById(R.id.verificationtext);
        TextView mess = findViewById(R.id.messaging);
        TextView profile = findViewById(R.id.profile);
        String email = user.getEmail().toString();

        //Show Email of the user at the top
        username.setText(email);

        //Log out button at the top
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //redirect to log in activity

                fauth.signOut();

                startActivity(new Intent(MainMainActivity.this,MainActivity.class));
                finish();
            }
        });



        if(!user.isEmailVerified())
        {
            verify.setVisibility(View.VISIBLE);
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Send the Verification Email

                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(MainMainActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(MainMainActivity.this, "Email not sent!" + task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            });
        }



        //OPTIONS BAR
        //Message
        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMainActivity.this, MessagingActivity.class));
            }
        });

        //Profile
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainMainActivity.this,ProfileActivity.class));
            }
        });
    }
}