package com.example.exposysdatalabproject;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

                //Authenticate the user

                fauth.signInWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                            //redirect to next activity
                            startActivity(new Intent(MainActivity.this,MainMainActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Error! " + task.getException(), Toast.LENGTH_LONG).show();
                            pbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
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