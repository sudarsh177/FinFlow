package com.example.finflow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.finflow.bottom_fragment.HomeFragment;
import com.example.finflow.bottom_fragment.NewsFeedFragment;
import com.example.finflow.bottom_fragment.StocksWatchlistFragment;
import com.example.finflow.income_expense_bottom_fragment.Dashboard;
import com.example.finflow.income_expense_bottom_fragment.ExpenseFragment;
import com.example.finflow.income_expense_bottom_fragment.IncomeFragment;
import com.example.finflow.income_expense_bottom_fragment.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LogIncomExpenseDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_incom_expense_dashboard);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                    Fragment selectedFragment = null;
                    if (item.getItemId() == R.id.navigation_dashboard) {
                        selectedFragment = new Dashboard();
                    } else if (item.getItemId() == R.id.navigation_stats) {
                        selectedFragment = new StatsFragment();
                    } else if (item.getItemId() == R.id.navigation_income) {
                        selectedFragment = new IncomeFragment();
                    } else if (item.getItemId() == R.id.navigation_expense) {
                        selectedFragment = new ExpenseFragment();

                    }
                if (selectedFragment != null) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.content, selectedFragment);
                        transaction.commit();
                    }
                    return true;
                }

        });


    }
}