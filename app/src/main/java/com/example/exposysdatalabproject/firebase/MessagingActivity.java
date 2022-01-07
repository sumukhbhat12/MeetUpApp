package com.example.exposysdatalabproject.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exposysdatalabproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MessagingActivity extends AppCompatActivity {

    private Button senddata1;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private TextView body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        senddata1 = findViewById(R.id.senddata);

        senddata1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addDataToFirestore();
                getDataFromFirestore();
            }
        });
    }

    private void addDataToFirestore(){
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String,Object> data = new HashMap<>();
        CollectionReference Users = db.collection("Users");
        String mail = user.getEmail().toString();
        data.put("email",mail);
        data.put("username","Sumukh Bhat2");
        Users.document(mail).set(data);
    }

    private void getDataFromFirestore(){
        senddata1 = findViewById(R.id.senddata);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String mail = user.getEmail().toString();
        DocumentReference docref = db.collection("Users").document(mail);
        body = findViewById(R.id.message_body);

        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists())
                    {


                        body.setText(document.get("username").toString());
                        //Toast.makeText(MessagingActivity.this, "document exists", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        body.setText("No document found");
                    }
                }
                else
                {
                    Toast.makeText(MessagingActivity.this, "Failed to Retrieve the data!" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}