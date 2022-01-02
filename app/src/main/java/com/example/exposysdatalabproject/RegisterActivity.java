package com.example.exposysdatalabproject;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register = findViewById(R.id.registerbtn);
        TextView email = findViewById(R.id.email2);
        TextView password = findViewById(R.id.password2);
        TextView confirmpassword = findViewById(R.id.confirmpassword2);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        ProgressBar pbar = findViewById(R.id.pbar2);
        TextView username = findViewById(R.id.username2);

        if(fauth.getCurrentUser()!=null)
        {
            //redirect to the next activity
            startActivity(new Intent(RegisterActivity.this,MainMainActivity.class));
            finish();
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String user = username.getText().toString().trim();
                String confpass = confirmpassword.getText().toString().trim();

                if(TextUtils.isEmpty(em))
                {
                    email.setError("Enter an Email!");
                    return;
                }
                if(TextUtils.isEmpty(user))
                {
                    username.setError("Enter a username!");
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
                if(TextUtils.isEmpty(confpass))
                {
                    confirmpassword.setError("Enter the password again!");
                    return;
                }
                if(!pass.equals(confpass))
                {
                    confirmpassword.setError("password & confirm password do not match");
                    return;
                }
                pbar.setVisibility(View.VISIBLE);

                //Register the user in firebase

                fauth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this, "Account created Successfully", Toast.LENGTH_SHORT).show();
                            //redirect to next activity
                            startActivity(new Intent(RegisterActivity.this,MainMainActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this, "Error! "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            pbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        //Redirect to Log in Page
        TextView alreadyhave = findViewById(R.id.alreadyhaveanaccount);
        alreadyhave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });
    }
}