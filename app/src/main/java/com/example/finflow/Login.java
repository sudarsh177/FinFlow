package com.example.finflow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finflow.Model.UserData;
import com.example.finflow.bottom_fragment.HomeFragment;
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
    TextView signUpTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.loginemailEditText);
        password = (EditText)findViewById(R.id.loginpasswordEditText);
        mAuth = FirebaseAuth.getInstance();
        loginbtn = (Button)findViewById(R.id.loginBtn);
        forgotPassTxt = (TextView)findViewById(R.id.forgotPassword);
        signUpTV = (TextView)findViewById(R.id.signUpTV);





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

        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        // check if logged in or not
        SharedPreferences prefs = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false); // Default to false if not found

        if (isLoggedIn) {
            // Proceed to the main activity
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish(); // Close the current activity
        }



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

                          SharedPreferences sharedPrefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor = sharedPrefs.edit();
                          editor.putBoolean("isLoggedIn", true);
                          editor.apply();
                          Log.d("SharedPreferences","Created");

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



                Intent intent =  new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(Login.this, "Login Failed.",Toast.LENGTH_SHORT).show();
            }
        });


    }
}