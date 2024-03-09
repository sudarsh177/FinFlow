package com.example.finflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText email;
    EditText password;
    Button loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.loginemailEditText);
        password = (EditText)findViewById(R.id.loginpasswordEditText);
        mAuth = FirebaseAuth.getInstance();
        loginbtn = (Button)findViewById(R.id.loginBtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
    }

    public void login(View view){
        String login_email = email.getText().toString().trim();
        String login_password = password.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(login_email,login_password).addOnCompleteListener(this,task -> {
            if(task.isSuccessful()){
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