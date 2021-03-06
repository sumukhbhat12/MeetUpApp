package com.example.exposysdatalabproject;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 234;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView email = findViewById(R.id.email);
        TextView password = findViewById(R.id.password);

        Button loginbtn = findViewById(R.id.loginbtn);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        ProgressBar pbar = findViewById(R.id.pbar);
        ImageView google = findViewById(R.id.googlelogin);
        TextView resetpass = findViewById(R.id.forgotpass);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //implementing login validation

                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (TextUtils.isEmpty(em)) {
                    email.setError("Enter an Email!");
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
                pbar.setVisibility(View.VISIBLE);

                //Authenticate the user

                fauth.signInWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                            //redirect to next activity
                            startActivity(new Intent(MainActivity.this, MainMainActivity.class));
                            finish();
                        } else {
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
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        //Login through google

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
                        Toast.makeText(MainActivity.this, "Login Failed!" + task.getException(), Toast.LENGTH_SHORT).show();
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


        //Reset Password

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetlink = new EditText(view.getContext());
                AlertDialog.Builder passwordresetdialog = new AlertDialog.Builder(view.getContext());
                passwordresetdialog.setTitle("Reset Password?");
                passwordresetdialog.setMessage("Enter the Email to receive a reset password link");
                passwordresetdialog.setView(resetlink);

                passwordresetdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Extract the email and send reset link
                        String mail = resetlink.getText().toString();
                        if(TextUtils.isEmpty(mail))
                        {
                            resetlink.setError("Enter the Email!");
                        }
                        else
                        {
                            fauth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(view.getContext(), "Password Reset Mail has been sent", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(view.getContext(), "Password Reset Mail was not sent!" + task.getException(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });
                passwordresetdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close the dialog
                    }
                });
                passwordresetdialog.create().show();
            }
        });
    }

    //Google Api code

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
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
                            // Sign in success


                            //Add email and username to Firestore database, if the user is not already added
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                            String user = acc.getDisplayName();
                            String em = mAuth.getCurrentUser().getEmail();
                            HashMap<String,Object> data = new HashMap<>();
                            data.put("email",em);
                            data.put("username",user);
                            database.collection("Users").document(em).set(data);


                            //redirect to next activity
                            startActivity(new Intent(MainActivity.this,MainMainActivity.class));
                            Toast.makeText(MainActivity.this, "Logged in Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Login Failed!" + task.getException(), Toast.LENGTH_LONG).show();
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
            startActivity(new Intent(MainActivity.this,MainMainActivity.class));
            finish();
        }
    }
}