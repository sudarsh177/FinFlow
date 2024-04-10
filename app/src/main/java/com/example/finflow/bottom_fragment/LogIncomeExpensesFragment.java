package com.example.finflow.bottom_fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finflow.R;
import com.example.finflow.income_expense_bottom_fragment.Dashboard;
import com.example.finflow.income_expense_bottom_fragment.ExpenseFragment;
import com.example.finflow.income_expense_bottom_fragment.IncomeFragment;
import com.example.finflow.income_expense_bottom_fragment.StatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
}