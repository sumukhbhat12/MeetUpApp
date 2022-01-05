package com.example.exposysdatalabproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_main);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        Button logout = findViewById(R.id.logoutbtn);
        TextView username = findViewById(R.id.usernamemain);
        TextView verify = findViewById(R.id.verificationtext);

        username.setText(fauth.getCurrentUser().getEmail().toString());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //redirect to log in activity

                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(MainMainActivity.this,MainActivity.class));
                finish();
            }
        });

        FirebaseUser user = fauth.getCurrentUser();

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
    }
}