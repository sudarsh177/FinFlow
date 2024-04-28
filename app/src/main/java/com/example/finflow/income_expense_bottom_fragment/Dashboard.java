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
    import android.view.animation.Animation;
    import android.view.animation.AnimationUtils;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.DatePicker;
    import android.widget.EditText;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.example.finflow.Login;
    import com.example.finflow.Model.Data;
    import com.example.finflow.R;
    import com.example.finflow.UserProfile;
    import com.firebase.ui.database.FirebaseRecyclerAdapter;
    import com.firebase.ui.database.FirebaseRecyclerOptions;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;

    import java.text.DateFormat;
    import java.util.Arrays;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.List;

    /**
     * A simple {@link Fragment} subclass.
     * Use the {@link Dashboard#newInstance} factory method to
     * create an instance of this fragment.
     */
    public class Dashboard extends Fragment {

        private FloatingActionButton fab_main;
        private FloatingActionButton fab_income;
        private FloatingActionButton fab_expense;

        private boolean isOpen = false;


        private Animation fadeOpen, fadeClose;

        private FirebaseAuth mAuth;
        private DatabaseReference mIncomeDatabase;
        private DatabaseReference mExpenseDatabase;

        private RecyclerView mRecyclerIncome;
        private RecyclerView mRecyclerExpense;

        private TextView fab_income_text;
        private TextView fab_expense_text;


        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;

        public Dashboard() {
            // Required empty public constructor
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Dashboard.
         */
        // TODO: Rename and change types and number of parameters
        public static Dashboard newInstance(String param1, String param2) {
            Dashboard fragment = new Dashboard();
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
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
            setHasOptionsMenu(true);
            mAuth = FirebaseAuth.getInstance();

            FirebaseUser mUser = mAuth.getCurrentUser();
            String uid = mUser.getUid();

            mIncomeDatabase = FirebaseDatabase.getInstance().getReference("IncomeData").child(uid);
            mExpenseDatabase = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uid);

            mIncomeDatabase.keepSynced(true);
            mExpenseDatabase.keepSynced(true);
            //Connect Floating Button to layout

            fab_main = view.findViewById(R.id.fb_main_plus_btn);
            fab_income = view.findViewById(R.id.income_ft_btn);
            fab_expense = view.findViewById(R.id.expense_ft_btn);

            // Connect floating text
            fab_income_text = view.findViewById(R.id.income_ft_text);
            fab_expense_text = view.findViewById(R.id.expense_ft_text);

            mRecyclerIncome = view.findViewById(R.id.recycler_income);
            mRecyclerExpense = view.findViewById(R.id.recycler_expense);

            //Animations

            fadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
            fadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

            fab_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addData();
                    floatingButtonAnimation();
                }
            });
    //


            //Recycler

            LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    //        layoutManagerIncome.setStackFromEnd(true);
    //        layoutManagerIncome.setReverseLayout(true);
            mRecyclerIncome.setHasFixedSize(true);
            mRecyclerIncome.setLayoutManager(layoutManagerIncome);

            LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    //        layoutManagerExpense.setStackFromEnd(true);
    //        layoutManagerExpense.setReverseLayout(true);
            mRecyclerExpense.setHasFixedSize(true);
            mRecyclerExpense.setLayoutManager(layoutManagerExpense);

            return view;
        }


    //Floating button animation

        private void floatingButtonAnimation() {
            if (isOpen) {
                fab_income.startAnimation(fadeClose);
                fab_expense.startAnimation(fadeClose);
                fab_income.setClickable(false);
                fab_expense.setClickable(false);
                fab_income_text.startAnimation(fadeClose);
                fab_expense_text.startAnimation(fadeClose);
                fab_income_text.setClickable(false);
                fab_expense_text.setClickable(false);
            } else {
                fab_income.startAnimation(fadeOpen);
                fab_expense.startAnimation(fadeOpen);
                fab_income.setClickable(true);
                fab_expense.setClickable(true);
                fab_expense_text.startAnimation(fadeOpen);
                fab_income_text.startAnimation(fadeOpen);
                fab_income_text.setClickable(true);
                fab_expense_text.setClickable(true);
            }
            isOpen = !isOpen;

        }

        private void addData() {
            //Fab Button Income
            fab_income.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertIncomeData();
                }
            });

            fab_expense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertExpenseData();
                }
            });
        }

        private void insertExpenseData() {

            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View view = inflater.inflate(R.layout.custom_layout_for_insertdata, null);

            List<String> categories = Arrays.asList("Food","Rent","Groceries","Misc");

            Spinner categorySpinner = view.findViewById(R.id.category_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);


            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

            mydialog.setView(view);
            final AlertDialog dialog = mydialog.create();
            dialog.setCancelable(false);
            EditText edtamount = view.findViewById(R.id.amount);
            DatePicker datePicker = view.findViewById(R.id.date_picker);

            EditText edtNote = view.findViewById(R.id.note_edt);
            Button saveBtn = view.findViewById(R.id.btnSave);
            Button cancelBtn = view.findViewById(R.id.btnCancel);


            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //retrieve selected category
                    String selectedCategory = categorySpinner.getSelectedItem().toString();

                    //retrieve selected date
                    DatePicker datePicker = view.findViewById(R.id.date_picker);
//                    Calendar calendar = Calendar.getInstance();
//                    datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth();
                    int year = datePicker.getYear();
                    String selectedDate = String.format("%d-%02d-%02d", year, month + 1, day);
                    String monthYear = String.format("%02d-%04d",month+1,year);

                    String amount = edtamount.getText().toString().trim();
                    String note = edtNote.getText().toString().trim();


                    if (TextUtils.isEmpty(amount)) {
                        edtamount.setError("Please Enter Amount");
                        return;
                    }
                    if (TextUtils.isEmpty(note)) {
                        edtNote.setError("Please Enter A Note");
                        return;
                    }
                    int amountInInt = Integer.parseInt(amount);

                    //Create random ID inside database
                    String id = mExpenseDatabase.push().getKey();

                    String mDate = DateFormat.getDateInstance().format(new Date());

                    Data data = new Data(amountInInt, selectedCategory, note, id, selectedDate,day,month+1,year,monthYear);

                    mExpenseDatabase.child(id).setValue(data);

                    Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    floatingButtonAnimation();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatingButtonAnimation();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }


        public void insertIncomeData() {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            View view = inflater.inflate(R.layout.custom_layout_for_insertdata, null);

            List<String> categories = Arrays.asList("Income");

            Spinner categorySpinner = view.findViewById(R.id.category_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);



            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

            mydialog.setView(view);
            final AlertDialog dialog = mydialog.create();
            dialog.setCancelable(false);
            EditText edtamount = view.findViewById(R.id.amount);
            DatePicker datePicker = view.findViewById(R.id.date_picker);

            EditText edtNote = view.findViewById(R.id.note_edt);
            Button saveBtn = view.findViewById(R.id.btnSave);
            Button cancelBtn = view.findViewById(R.id.btnCancel);


            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //retrieve selected category
                    String selectedCategory = categorySpinner.getSelectedItem().toString();

                    //retrieve selected date
                    DatePicker datePicker = view.findViewById(R.id.date_picker);
                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth();
                    int year = datePicker.getYear();
                    String selectedDate = String.format("%d-%02d-%02d", year, month + 1, day);
                    String monthYear = String.format("%02d-%04d",month+1,year);


                    String amount = edtamount.getText().toString().trim();
                    String note = edtNote.getText().toString().trim();


                    if (TextUtils.isEmpty(amount)) {
                        edtamount.setError("Please Enter Amount");
                        return;
                    }
                    if (TextUtils.isEmpty(note)) {
                        edtNote.setError("Please Enter A Note");
                        return;
                    }
                    int amountInInt = Integer.parseInt(amount);

                    //Create random ID inside database
                    String id = mIncomeDatabase.push().getKey();

                    String mDate = DateFormat.getDateInstance().format(new Date());

                    Data data = new Data(amountInInt, selectedCategory, note, id, selectedDate,day,month+1,year,monthYear);

                    mIncomeDatabase.child(id).setValue(data);

                    Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                    floatingButtonAnimation();
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatingButtonAnimation();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        @Override
        public void onStart() {
            super.onStart();

            //Adding Income into Recycler View

            FirebaseRecyclerOptions<Data> options =
                    new FirebaseRecyclerOptions.Builder<Data>()
                            .setQuery(mIncomeDatabase, Data.class)
                            .build();


            FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                    Log.d("DataBinding", "Binding data: " + model.toString());
                    holder.setIncomeType(model.getCategories());
                    holder.setNote(model.getNote());
                    holder.setIncomeDate(model.getDate());
                    holder.setIncomeAmount(model.getAmount());
                }

                @Override
                public IncomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.income_expense_input_data, parent, false);
                    return new IncomeViewHolder(view);
                }


            };

            mRecyclerIncome.setAdapter(incomeAdapter);
            incomeAdapter.startListening();


            //Adding Expense into Recycler View
            FirebaseRecyclerOptions<Data> options_expense =
                    new FirebaseRecyclerOptions.Builder<Data>()
                            .setQuery(mExpenseDatabase, Data.class)
                            .build();


            FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options_expense) {
                @Override
                protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                    Log.d("DataBinding", "Binding data: " + model.toString());
                    holder.setIncomeType(model.getCategories());
                    holder.setNote(model.getNote());
                    holder.setIncomeDate(model.getDate());
                    holder.setIncomeAmount(model.getAmount());
                }

                @Override
                public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.income_expense_input_data, parent, false);
                    return new ExpenseViewHolder(view);
                }


            };

            mRecyclerExpense.setAdapter(expenseAdapter);
            expenseAdapter.startListening();


        }

        // For income Data

        public static class IncomeViewHolder extends RecyclerView.ViewHolder {
            View mIncomeView;

            public IncomeViewHolder(@NonNull View itemView) {
                super(itemView);
                mIncomeView = itemView;
            }

            public void setIncomeType(String type) {
                TextView mtype = mIncomeView.findViewById(R.id.category);
                Log.i("CATEGORY", type);
                mtype.setText(type);
            }

            public void setIncomeAmount(int amount) {
                TextView mAmount = mIncomeView.findViewById(R.id.amount);
                String strAmount = String.valueOf(amount);
                Log.i("AMOUNT", strAmount);
                mAmount.setText(strAmount);
            }

            public void setIncomeDate(String date) {
                TextView mDate = mIncomeView.findViewById(R.id.date);
                Log.i("DATE", date);
                mDate.setText(date);
            }

            public void setNote(String note) {
                TextView mDate = mIncomeView.findViewById(R.id.note);
                Log.i("NOTE", note);
                mDate.setText(note);
            }
        }

        public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
            View mIncomeView;

            public ExpenseViewHolder(@NonNull View itemView) {
                super(itemView);
                mIncomeView = itemView;
            }

            public void setIncomeType(String type) {
                TextView mtype = mIncomeView.findViewById(R.id.category);
                Log.i("CATEGORY", type);
                mtype.setText(type);
            }

            public void setIncomeAmount(int amount) {
                TextView mAmount = mIncomeView.findViewById(R.id.amount);
                String strAmount = String.valueOf(amount);
                Log.i("AMOUNT", strAmount);
                mAmount.setText(strAmount);
            }

            public void setIncomeDate(String date) {
                TextView mDate = mIncomeView.findViewById(R.id.date);
                Log.i("DATE", date);
                mDate.setText(date);
            }

            public void setNote(String note) {
                TextView mDate = mIncomeView.findViewById(R.id.note);
                Log.i("NOTE", note);
                mDate.setText(note);
            }
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