package com.example.finflow;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


public class UserProfile extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        EditText emailUser=findViewById(R.id.email_account);
        EditText dateofCreation=findViewById(R.id.dateofCreation);
        EditText timeOfCreation=findViewById(R.id.timeOfCreation);
        EditText signInAt=findViewById(R.id.lastSignInAt);
        Button reset_btn = findViewById(R.id.reset_pass_btn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        emailUser.setText(user.getEmail());
        Long timestampCreate=user.getMetadata().getCreationTimestamp();
        Date date1 = new Date(timestampCreate);
        SimpleDateFormat jdf = new SimpleDateFormat("dd MMM yyyy");
        String java_date = jdf.format(date1);

        SimpleDateFormat jdf1 = new SimpleDateFormat("HH:mm:ss z");
        String TimeOfCreation = jdf1.format(date1);
        dateofCreation.setText(java_date);
        timeOfCreation.setText(TimeOfCreation);
        Long lastSignInTS=user.getMetadata().getLastSignInTimestamp();

        Date date2 = new Date(lastSignInTS);
        SimpleDateFormat jdf2 = new SimpleDateFormat("dd MMM yyyy    HH:mm:ss z");
        String SignInAt = jdf2.format(date2);
        signInAt.setText(SignInAt);



        // Setup Toolbar
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Return to the previous activity on back press
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                if(isValidEmail(user.getEmail())){
                    firebaseAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email Sent");
                                Log.d(user.getEmail(),"Email");
                                Toast.makeText(UserProfile.this, "Please check your email with password reset instructions. If you don't receive an email, please make sure you've entered the email address you registered with, or that you used this email to register.", Toast.LENGTH_LONG).show();
                                Intent intent =  new Intent(UserProfile.this,Login.class);
                                startActivity(intent);

                            } else {
                                Log.d(TAG, "Failed to send reset email.");
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(UserProfile.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void setSupportActionBar(Toolbar mToolbar) {
    }

    // To prevent crashes due to pressing physical menu buttons
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            // return true to prevent further propagation of the key event
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // On clicking the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean isValidEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


}
