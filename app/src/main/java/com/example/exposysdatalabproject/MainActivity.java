package com.example.exposysdatalabproject;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView email = findViewById(R.id.email);
        TextView password = findViewById(R.id.password);

        Button loginbtn = findViewById(R.id.loginbtn);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        ProgressBar pbar = findViewById(R.id.pbar);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //implementing login validation

                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if(TextUtils.isEmpty(em))
                {
                    email.setError("Enter an Email!");
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    password.setError("Enter a password!");
                    return;
                }
                if(pass.length()<6)
                {
                    password.setError("password must be at least 6 characters");
                    return;
                }
                pbar.setVisibility(View.VISIBLE);
            }
        });

        //Redirect to Register page on clicking REGISTER

        TextView register = findViewById(R.id.registerhere);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
    }
}