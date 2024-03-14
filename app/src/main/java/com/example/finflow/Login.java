package com.example.finflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finflow.Model.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText email;
    EditText password;
    Button loginbtn;

    TextView forgotPassTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.loginemailEditText);
        password = (EditText)findViewById(R.id.loginpasswordEditText);
        mAuth = FirebaseAuth.getInstance();
        loginbtn = (Button)findViewById(R.id.loginBtn);
        forgotPassTxt = (TextView)findViewById(R.id.forgotPassword);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });

        forgotPassTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext(),RecoverAccount.class);
                startActivity(intent);
            }
        });


    }

    public void login(View view){
        //extracting the typed email and password
        String login_email = email.getText().toString().trim();
        String login_password = password.getText().toString().trim();

        //calling signin method in firebaseAuth
        mAuth.signInWithEmailAndPassword(login_email,login_password).addOnCompleteListener(this,task -> {
            if(task.isSuccessful()){
                //Saving the User data in User class
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                /*retrieving the current user data from firestore database to User class object */
                UserData userData = new UserData();

               DocumentReference docRef =  db.collection("users").document(firebaseUser.getUid());

               docRef.get().addOnCompleteListener(task1 -> {
                  if(task1.isSuccessful()){
                      DocumentSnapshot documentSnapshot = task1.getResult();
                      if(documentSnapshot.exists()){
                          userData.setUsername(documentSnapshot.getString("username"));
                          userData.setEmail(documentSnapshot.getString("email"));

                          Log.d("FireStore","Username "+userData.getUsername());
                          Log.d("FireStore","Email "+userData.getEmail());

                      }
                      else{
                          Log.d("FireStore","No such Document");

                      }
                  }
                  else{
                      Log.d("FireStore","Task1 get failed");
                  }
               });

                Toast.makeText(Login.this, "Log in successful.", Toast.LENGTH_SHORT).show();

                Intent intent =  new Intent(getApplicationContext(),Dashboard.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(Login.this, "Login Failed.",Toast.LENGTH_SHORT).show();
            }
        });


    }
}