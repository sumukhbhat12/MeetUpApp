package com.example.exposysdatalabproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.model.UriLoader;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    ImageView pp;
    Button ppbtn,logout;
    ActivityResultLauncher<Intent> activityResultLauncher;
    StorageReference storageReference;
    FirebaseAuth fauth;
    FirebaseUser user;
    TextView username,verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();

        pp = findViewById(R.id.profilepic);
        ppbtn = findViewById(R.id.ppuploadbtn);
        username = findViewById(R.id.usermailprofile);
        String email = user.getEmail();
        logout = findViewById(R.id.logoutbtn);
        verify = findViewById(R.id.verificationtext);


        StorageReference profileref = storageReference.child("Users/"+fauth.getCurrentUser().getUid()+"/profilepic.jpg");

        //Profile pic is there by default and no need to upload everytime
        profileref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Picasso.get().load(task.getResult()).into(pp);
                }
            }
        });

        //click on the button to choose a profile pic from the device
        ppbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //choose a profile pic
                Intent opengalleryintent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(opengalleryintent);
            }
        });

                //choose a profile pic
                activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Uri imageuri = data.getData();
                            //below line is only for testing
                            //pp.setImageURI(imageuri);

                            //upload the image to Firebase
                            uploadImageToFirebase(imageuri);
                        }
                    }
                });



        //Show Email of the user below the profile picture
        username.setText(email);

        //Log out button at the top
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder logoutdialog = new AlertDialog.Builder(view.getContext());
                logoutdialog.setTitle("Log out?");
                logoutdialog.setMessage("Are you sure you want to log out?");

                logoutdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GoogleSignIn.getClient(ProfileActivity.this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();
                        fauth.signOut();
                        //redirect to log in activity
                        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                        finish();
                    }
                });

                logoutdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nothing
                    }
                });

                logoutdialog.create().show();

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
                                Toast.makeText(ProfileActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this, "Email not sent!" + task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            });
        }


    }

    //Upload the profile picture to Firebase storage
    private void uploadImageToFirebase(Uri imageuri){
        StorageReference fileref = storageReference.child("Users/"+fauth.getCurrentUser().getUid()+"/profilepic.jpg");

        fileref.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                    fileref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Picasso.get().load(task.getResult()).into(pp);
                        }
                    });
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "Error! " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}