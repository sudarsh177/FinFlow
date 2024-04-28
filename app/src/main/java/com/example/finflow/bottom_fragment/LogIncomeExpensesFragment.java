package com.example.finflow.bottom_fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.finflow.Login;
import com.example.finflow.R;
import com.example.finflow.UserProfile;
import com.example.finflow.income_expense_bottom_fragment.Dashboard;
import com.example.finflow.income_expense_bottom_fragment.ExpenseFragment;
import com.example.finflow.income_expense_bottom_fragment.IncomeFragment;
import com.example.finflow.income_expense_bottom_fragment.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class LogIncomeExpensesFragment extends Fragment {


//    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
//        Fragment selectedFragment = null;
//        switch (item.getItemId()) {
//            case R.id.navigation_dashboard:
//                selectedFragment = new Dashboard();
//                break;
//            case R.id.navigation_stats:
//                selectedFragment = new StatsFragment();
//                break;
//            case R.id.navigation_income:
//                selectedFragment = new StatsFragment();
//                break;
//            case R.id.navigation_expense:
//                selectedFragment = new StatsFragment();
//                break;
//        }
//        if (selectedFragment != null) {
//            getChildFragmentManager().beginTransaction().replace(R.id.content, selectedFragment).commit();
//        }
//        return true;
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_income_expenses, container, false);
        setHasOptionsMenu(true);
        BottomNavigationView navigation = view.findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    selectedFragment = new Dashboard();
                    break;
                case R.id.navigation_stats:
                    selectedFragment = new StatsFragment();
                    break;
                case R.id.navigation_income:
                    selectedFragment = new IncomeFragment(); // Consider using a different fragment for income
                    break;
                case R.id.navigation_expense:
                    selectedFragment = new ExpenseFragment(); // Consider using a different fragment for expense
                    break;
            }
            if (selectedFragment != null) {
                getChildFragmentManager().beginTransaction().replace(R.id.content, selectedFragment).commit();
            }
            return true;
        });
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks here
        int id = item.getItemId();
        if (id == R.id.user_profile) {
            // Handle user profile action
            Intent intent = new Intent(getActivity(), UserProfile.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.logout) {
            // Handle logout action
            // Implement logout functionality here
            logout();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), Login.class));
        getActivity().finish();


    }
}