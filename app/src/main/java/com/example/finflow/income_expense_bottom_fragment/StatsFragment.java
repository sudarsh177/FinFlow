package com.example.finflow.income_expense_bottom_fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.finflow.Login;
import com.example.finflow.Model.Data;
import com.example.finflow.R;
import com.example.finflow.UserProfile;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private String[] type={"Income", "Expense"};
    private int[] values={0,0};
    private Map<Integer, Integer> MonthWiseIncome = new TreeMap<Integer, Integer>();
    private Map<Integer, Integer> MonthWiseExpense = new TreeMap<Integer, Integer>();
    private static Set<Pair<Integer,Integer>> MonthWiseIncomeSorter= new HashSet<Pair<Integer,Integer>>();;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int totalExpense=0;
    private int totalIncome=0;

    private PieChart pieChart;

    private BarChart barChart;



    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);



        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_stats, container, false);

         pieChart = view.findViewById(R.id.piechart);

         barChart = view.findViewById(R.id.barchart);


         //init spinner
        Spinner monthSpinner = view.findViewById(R.id.spinner_stats);
        List<String> months = Arrays.asList("Jan", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);


        // init Spinner

        Spinner YearSpinner = view.findViewById(R.id.spinner_stats_year);
        List<String> Year = Arrays.asList("2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013");
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Year);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YearSpinner.setAdapter(yearAdapter);




        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int monthPosition, long l) {
                YearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        int selectedMonth = monthPosition + 1; // Assuming the first position is January
                        int selectedYear = Integer.parseInt(YearSpinner.getSelectedItem().toString());
                        fetchTotalIncomeExpense(selectedMonth, selectedYear);
                        fetchExpenseForMonth(selectedMonth, selectedYear);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void fetchExpenseForMonth(int month,int year) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference expenseRef = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);

//        int montNumber = position+1;

        Query expenseQuery = expenseRef.orderByChild("monthYear").equalTo("0"+month+"-"+year);


        expenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> categoryExpenses = new TreeMap<>();

                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Data expenseData = expenseSnapshot.getValue(Data.class);
                    Log.d(expenseData.toString(),"DATA FROM FIREBASE");
                    String category = expenseData.getCategories(); // Assuming you have a getCategory method
                    int amount = expenseData.getAmount();
                    categoryExpenses.put(category, categoryExpenses.getOrDefault(category, 0) + amount);
                }
                updateBarChart(categoryExpenses);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }

    private void updateBarChart(Map<String, Integer> categoryExpenses) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>(categoryExpenses.keySet());

        int i = 0;

        for (Map.Entry<String, Integer> entry : categoryExpenses.entrySet()) {
            entries.add(new BarEntry(i, entry.getValue()));
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Expenses by Category");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels.toArray(new String[0])));
        barChart.invalidate(); // Refresh the chart

    }

    public void fetchTotalIncomeExpense(int month,int year){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //reference to income databse
        DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference("IncomeData").child(uid);
        DatabaseReference expenseRef = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);



//        int montNumber = position+1;

        Query incomeQuery = incomeRef.orderByChild("monthYear").equalTo("0"+month+"-"+year);
        // Query to get expense for the selected month and year
        Query expenseQuery = expenseRef.orderByChild("monthYear").equalTo("0"+month+"-"+year);

        AtomicInteger queryCounter = new AtomicInteger(0);



//        Query incomeQuery = incomeRef.orderByChild("month").equalTo(montNumber);

        incomeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalIncome = 0;
                for (DataSnapshot incomeSnapshot : snapshot.getChildren()) {
                    Data incomeData = incomeSnapshot.getValue(Data.class);
                    totalIncome += incomeData.getAmount(); // Assuming you have a getAmount method in your Data class
                }
                queryCounter.incrementAndGet();
                updatePieChartIfBothQueriesCompleted(queryCounter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }



    });

//        DatabaseReference expenseRef = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);

        // Query to get expense for the selected month
//        Query expenseQuery = expenseRef.orderByChild("month").equalTo(montNumber);

        expenseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalExpense = 0;
                for (DataSnapshot incomeSnapshot : snapshot.getChildren()) {
                    Data expenseData = incomeSnapshot.getValue(Data.class);
                    totalExpense += expenseData.getAmount(); // Assuming you have a getAmount method in your Data class
                }

                queryCounter.incrementAndGet();
                updatePieChartIfBothQueriesCompleted(queryCounter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Update the pie chart with the total income and expense

    }

    private void updatePieChartIfBothQueriesCompleted(AtomicInteger queryCounter) {

        if (queryCounter.get() == 2) { // Both queries have completed
            updatePieChart(totalIncome, totalExpense);
        }
    }


    public void updatePieChart(int totalIncome,int totalExpense){

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalIncome, "Income"));
        entries.add(new PieEntry(totalExpense, "Expense"));


        PieDataSet dataSet = new PieDataSet(entries, "Monthly Income and Expense");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // Create a data object
        PieData data = new PieData(dataSet);
        data.setValueTextSize(14);

        // Set data to the pie chart
        pieChart.setData(data);
        pieChart.setDescription(null); // Hide the description
        pieChart.animateXY(1400, 1400); // Animate the pie chart
        pieChart.invalidate(); // Redraw the pie chart
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
