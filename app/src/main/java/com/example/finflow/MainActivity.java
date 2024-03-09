package com.example.finflow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText email;
    EditText password;
    Button signupbtn;
    FirebaseAuth firebaseAuth;
    TextView alreadyUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initiailzing Firebase Auth
        firebaseAuth= FirebaseAuth.getInstance();
        //Initializing the components
        username = (EditText)findViewById(R.id.usernameEditText);
        email = (EditText)findViewById(R.id.emailEditText);
        password = (EditText)findViewById(R.id.passwordEditText);

        signupbtn = (Button)findViewById(R.id.signupButton);
        alreadyUser = (TextView)findViewById(R.id.alreadyUser);
        //setting onclick listener on signup btn

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp(view);
            }
        });

        alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });
    }

    public void signUp(View view){
       String user_email = email.getText().toString().trim();
       String user_password = password.getText().toString().trim();
       String user_username = username.getText().toString().trim();

        if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password) || TextUtils.isEmpty(user_username)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        //storing the username and email in firestore db
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("username", user_username);
                        userMap.put("email", user_email);
                        db.collection("users").document(user.getUid())
                                .set(userMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent =  new Intent(getApplicationContext(),Login.class);
                                        startActivity(intent);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error creating user", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else{
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }


}