package com.example.finflow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finflow.R;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("FinFlow");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.home_id) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.stocks_id) {
                    selectedFragment = new StocksWatchlistFragment();
                } else if (item.getItemId() == R.id.logIncomeExpenses_id) {
                    selectedFragment = new LogIncomeExpensesFragment();
                } else if (item.getItemId() == R.id.news_id) {
                    selectedFragment = new NewsFeedFragment();
                }
                if (selectedFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.bottomFragment, selectedFragment);
                    transaction.commit();
                }
                return true;
            }
        });

        // Set the initial fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.bottomFragment, new HomeFragment()).commit();
    }
}
