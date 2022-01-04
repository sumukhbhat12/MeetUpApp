package com.example.exposysdatalabproject;

import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class RegisterActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    ActivityResultLauncher<Intent> activityResultLauncher;

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
        ImageView google = findViewById(R.id.googlelogin2);

        if (fauth.getCurrentUser() != null) {
            //redirect to the next activity
            startActivity(new Intent(RegisterActivity.this, MainMainActivity.class));
            finish();
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String user = username.getText().toString().trim();
                String confpass = confirmpassword.getText().toString().trim();
                if (TextUtils.isEmpty(em)) {
                    email.setError("Enter an Email!");
                    return;
                }
                if (TextUtils.isEmpty(user)) {
                    username.setError("Enter a username!");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    password.setError("Enter a password!");
                    return;
                }
                if (pass.length() < 6) {
                    password.setError("password must be at least 6 characters");
                    return;
                }
                if (TextUtils.isEmpty(confpass)) {
                    confirmpassword.setError("Enter the password again!");
                    return;
                }
                if (!pass.equals(confpass)) {
                    confirmpassword.setError("password & confirm password do not match");
                    return;
                }
                pbar.setVisibility(View.VISIBLE);

                //Register the user in firebase

               fauth.createUserWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //verify email before creating the account

                            sendVerificationEmail();

                            //redirect to next activity

                            startActivity(new Intent(RegisterActivity.this, MainMainActivity.class));
                            pbar.setVisibility(View.INVISIBLE);
                            finish();

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        //Login through google, facebook or twitter

        //GOOGLE LOGIN/SIGNUP

        mAuth = FirebaseAuth.getInstance();
        createRequest();

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);

                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Toast.makeText(RegisterActivity.this, "Login Failed!" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }


    //Send Verification Email

    private void sendVerificationEmail()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(RegisterActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Email not sent!" + task.getException(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }


    //Google Sign In API code


    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(RegisterActivity.this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(new Intent(RegisterActivity.this,MainMainActivity.class));
                            Toast.makeText(RegisterActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Login Failed!" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            startActivity(new Intent(RegisterActivity.this,MainMainActivity.class));
            finish();
        }
    }


}