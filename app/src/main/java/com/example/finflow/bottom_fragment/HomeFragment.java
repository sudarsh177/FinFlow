package com.example.finflow.bottom_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finflow.MainActivity1;
import com.example.finflow.R;
import com.example.finflow.ReminderCode.AddReminderFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigation);
        int itemCount = bottomNavigationView.getMenu().size();
        Log.d("MenuItemCount", "Number of items in BottomNavigationView: " + itemCount);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.home_id:
                        fragment = new HomeFragment();
                        break;
                    case R.id.stocks_id:
                        fragment = new StocksWatchlistFragment();
                        break;
                    case R.id.logIncomeExpenses_id:
                        fragment = new LogIncomeExpensesFragment();
                        break;
                    case R.id.news_id:
                        fragment = new NewsFeedFragment();
                        break;
                    case R.id.remainder:
                        fragment = new AddReminderFragment();
                        break;
                }
                if (fragment != null) {
                    replaceFragment(fragment);
                    return true;
                } else {
                    return false;
                }
            }
        });

        MaterialButton stocksWatchlist = view.findViewById(R.id.stocks_watchlist);
        stocksWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new StocksWatchlistFragment());
            }
        });

        MaterialButton logIncomeExpenses = view.findViewById(R.id.log_income_expenses);
        logIncomeExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new LogIncomeExpensesFragment());
            }
        });

        MaterialButton newsFeed = view.findViewById(R.id.news_feed);
        newsFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new NewsFeedFragment());
            }
        });

        MaterialButton addItemReminderButton = view.findViewById(R.id.button_add_item_reminder);
        addItemReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity1.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottomFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
