package com.example.finflow;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;


public class UserProfile extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

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
}
