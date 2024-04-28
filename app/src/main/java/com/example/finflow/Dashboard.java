package com.example.finflow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finflow.R;
import com.example.finflow.ReminderCode.AddReminderFragment;
import com.example.finflow.bottom_fragment.BillPaymentRemainderFragment;
import com.example.finflow.bottom_fragment.HomeFragment;
import com.example.finflow.bottom_fragment.LogIncomeExpensesFragment;
import com.example.finflow.bottom_fragment.NewsFeedFragment;
import com.example.finflow.bottom_fragment.StocksWatchlistFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("FinFlow");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.logIncomeExpenses_id) {
                    // Start the activity
                    Intent intent = new Intent(Dashboard.this, LogIncomExpenseDashboard.class);
                    startActivity(intent);
                    return true;
                }
                else {


                    Fragment selectedFragment = null;
                    if (item.getItemId() == R.id.home_id) {
                        selectedFragment = new HomeFragment();
                    } else if (item.getItemId() == R.id.stocks_id) {
                        selectedFragment = new StocksWatchlistFragment();
                    } else if (item.getItemId() == R.id.news_id) {
                        selectedFragment = new NewsFeedFragment();
                    } else if (item.getItemId() == R.id.remainder) {
                        selectedFragment = new AddReminderFragment();
                    }
                    if (selectedFragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.bottomFragment, selectedFragment);
                        transaction.commit();
                    }
                    return true;
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.bottomFragment, new StocksWatchlistFragment()).commit();
    }
    public void setBottomNavigationVisibility(int visibility) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(visibility);
        }
    }

}
//package com.example.finflow;
//
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.finflow.bottom_fragment.HomeFragment;
//
//public class Dashboard extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dashboard);
//        getSupportFragmentManager().beginTransaction().replace(R.id.bottomFragment, new HomeFragment()).commit();
//    }
//}
