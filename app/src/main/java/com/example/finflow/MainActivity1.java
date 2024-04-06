package com.example.finflow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finflow.bottom_fragment.HomeFragment;
import com.example.finflow.nav_fragment.AboutFragment;
import com.example.finflow.nav_fragment.FeedbackFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer_layout;
    NavigationView navigationView;
    View header;
    private OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            onBackPressedMethod();
        }
    };

    public void onBackPressedMethod() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("FinFlow");
        drawer_layout = findViewById(R.id.drawer_layout);
        navigationView = null;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.open_drawer, R.string.close_drawer);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
        if (navigationView != null) {
            header = navigationView.getHeaderView(0);
            View appName = header.findViewById(R.id.appName);
            navigationView.setNavigationItemSelectedListener(this);
        }
        replaceFragment(new HomeFragment());
    }
    private void replaceFragment(Fragment Fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.navFragment, Fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            replaceFragment(new HomeFragment());
        } else if (itemId == R.id.nav_feedback) {
            replaceFragment(new FeedbackFragment());
            setTitle("Feedback");
        } else if (itemId == R.id.nav_share) {
            Toast.makeText(this, "Share Clicked", Toast.LENGTH_LONG).show();
        } else if (itemId == R.id.nav_about) {
            replaceFragment(new AboutFragment());
            setTitle("About");
        } else if (itemId == R.id.nav_logout) {
            Toast.makeText(this, "Logout Clicked", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}