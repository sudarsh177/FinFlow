package com.example.finflow;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class RecoverAccount extends AppCompatActivity {
    EditText recoverEmail;
    Button recoverBtn;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_account);

        recoverEmail = (EditText)findViewById(R.id.recoverEmailTxt);
        recoverBtn = (Button)findViewById(R.id.recoverBtn);

        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               recoverAccount(view);
            }
        });


    }

    public void recoverAccount(View view){
        String email = recoverEmail.getText().toString();
        firebaseAuth = FirebaseAuth.getInstance();

        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Query queryByEmail = db.collection("users").whereEqualTo("email",email);
        if(isValidEmail(email)) {
        //    queryByEmail.get().addOnCompleteListener(task -> {
          //      if (task.isSuccessful()) {
            //        for (QueryDocumentSnapshot document : task.getResult()) {
              //          if (document.exists()) {
                            // Email exists, proceed with account recovery
                            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email Sent");
                                        Log.d(email,"Email");
                                        Toast.makeText(RecoverAccount.this, "Please check your email with password reset instructions. If you don't receive an email, please make sure you've entered the email address you registered with, or that you used this email to register.", Toast.LENGTH_LONG).show();
                                        Intent intent =  new Intent(RecoverAccount.this,Login.class);
                                        startActivity(intent);

                                    } else {
                                        Log.d(TAG, "Failed to send reset email.");
                                    }
                                }
                            });
//                        } else {
//                            Log.d(TAG,"Email Id not present");
//                            Toast.makeText(RecoverAccount.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
//                        }


                    //}
//                } else {
//                    Log.d(TAG, "Error performing querying email");
//                }
//            });
        }
        else{
            Toast.makeText(RecoverAccount.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();

        }
    }

    public boolean isValidEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
