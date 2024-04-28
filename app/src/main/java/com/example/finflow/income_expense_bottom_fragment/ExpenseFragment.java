package com.example.finflow.income_expense_bottom_fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.finflow.Login;
import com.example.finflow.Model.Data;
import com.example.finflow.R;
import com.example.finflow.UserProfile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference mExpenseDatabase;

    private FirebaseAuth mAuth;

    private TextView totalExpenseText;

    private RecyclerView recyclerView;

    private EditText etAmount;
    private Spinner updateSpinner;

    private EditText etNote;

    private Button cancelbtn;
    private Button updatebtn;

    private DatePicker datePickerUpdate;

    private String post_key;
    private String type;
    private String note;
    private int amount;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
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
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        totalExpenseText = view.findViewById(R.id.expense_txt_result);
        recyclerView = view.findViewById(R.id.recycler_id_expense);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        Spinner monthSpinner = view.findViewById(R.id.spinner);
        List<String> months = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);


        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Call a method to update the UI with expenses for the selected month

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser mUser = mAuth.getCurrentUser();
                String uid = mUser.getUid();
                mExpenseDatabase = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);



                int montNumber = position+1;


                Query query = mExpenseDatabase.orderByChild("month").equalTo(montNumber);
                Log.d("query",query.toString());

                query.addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {

                            int totalExpense = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                Data data = dataSnapshot.getValue(Data.class);
                                Log.d("DATA", data.toString());
                                totalExpense += data.getAmount();
                                String stotal = String.valueOf(totalExpense);
                                Log.d("TOTAL EXPENSE", stotal);
                                totalExpenseText.setText(stotal + ".00");




                            }
                        }
                        else{
                            totalExpenseText.setText("No expenses for this month");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();

            FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                        // Bind the Data object to the ExpenseViewHolder
                        // For example, holder.bind(model);

                        Log.d("DataBinding", "Binding data: " + model.toString());
                        holder.setExpenseType(model.getCategories());
                        holder.setNote(model.getNote());
                        holder.setExpenseDate(model.getDate());
                        holder.setExpenseAmount(model.getAmount());

                        holder.mexpenseFragmentView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                Log.d("DEBUG","ocClick called");
                                post_key = getRef(holder.getAbsoluteAdapterPosition()).getKey();
                                type = model.getCategories();
                                note = model.getNote();
                                amount = model.getAmount();

                                updateDataItem(montNumber);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create a new instance of the ViewHolder
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
                        return new MyViewHolder(view);
                    }
                };

                recyclerView.setAdapter(adapter);
                adapter.startListening();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where no month is selected
            }
        });



        return view;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mexpenseFragmentView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mexpenseFragmentView = itemView;
            // Initialize your views here
        }

        public void setExpenseType(String type) {
            TextView mtype = mexpenseFragmentView.findViewById(R.id.type_txt_expense);
            Log.i("CATEGORY", type);
            mtype.setText(type);
        }

        public void setExpenseAmount(int amount) {
            TextView mAmount = mexpenseFragmentView.findViewById(R.id.amount_txt_expense);
            String strAmount = String.valueOf(amount);
            Log.i("AMOUNT", strAmount);
            mAmount.setText(strAmount);
        }

        public void setExpenseDate(String date) {
            TextView mDate = mexpenseFragmentView.findViewById(R.id.date_txt_expense);
            Log.i("DATE", date);
            mDate.setText(date);
        }

        public void setNote(String note) {
            TextView mDate = mexpenseFragmentView.findViewById(R.id.note_txt_expense);
            Log.i("NOTE", note);
            mDate.setText(note);
        }

        public void bind(Data data) {
            // Bind your data to the views here
        }
    }

private void updateDataItem(int monthNumber){
        Log.d("DEBUG","updateDataItem() called");

    AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View myview = inflater.inflate(R.layout.update_data_item, null);
    mydialog.setView(myview);

    etAmount = myview.findViewById(R.id.amount_update);

    etNote = myview.findViewById(R.id.note_edt_update);

    updateSpinner = myview.findViewById(R.id.category_spinner_update);

    datePickerUpdate = myview.findViewById(R.id.date_picker_update);

    updatebtn = myview.findViewById(R.id.btnUpdate);
    cancelbtn = myview.findViewById(R.id.btnCancel_update);

    etNote.setText(note);


    etAmount.setText(String.valueOf(amount));


    List<String> categories = Arrays.asList("Food","Rent","Groceries","Misc");


    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    updateSpinner.setAdapter(adapter);

    updateSpinner.setSelection(adapter.getPosition(type));



    final AlertDialog dialog = mydialog.create();

    updatebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String selectedCategory_update = updateSpinner.getSelectedItem().toString();

            //retrieve selected date

//                    Calendar calendar = Calendar.getInstance();
//                    datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
            int day = datePickerUpdate.getDayOfMonth();
            int month = datePickerUpdate.getMonth();
            int year = datePickerUpdate.getYear();
            String selectedDate = String.format("%d-%02d-%02d", year, month + 1, day);

            String monthYear = String.format("%02d-%04d",month,year);


            String amount = etAmount.getText().toString().trim();
            String note = etNote.getText().toString().trim();


            if (TextUtils.isEmpty(amount)) {
                etAmount.setError("Please Enter Amount");
                return;
            }
            if (TextUtils.isEmpty(note)) {
                etNote.setError("Please Enter A Note");
                return;
            }
            int amountInInt = Integer.parseInt(amount);

            Data data = new Data(amountInInt, selectedCategory_update, note, post_key, selectedDate,day,month+1,year,monthYear);

            mExpenseDatabase.child(post_key).setValue(data);

            Query query = mExpenseDatabase.orderByChild("month").equalTo(monthNumber);
            Log.d("query",query.toString());

            query.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {

                        int totalExpense = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            Data data = dataSnapshot.getValue(Data.class);
                            Log.d("DATA", data.toString());
                            totalExpense += data.getAmount();
                            String stotal = String.valueOf(totalExpense);
                            Log.d("TOTAL EXPENSE", stotal);
                            totalExpenseText.setText(stotal + ".00");




                        }
                    }
                    else{
                        totalExpenseText.setText("No expenses for this month");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            dialog.dismiss();






        }
    });
    cancelbtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mExpenseDatabase.child(post_key).removeValue();
            dialog.dismiss();
        }
    });

    dialog.show();





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