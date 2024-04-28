package com.example.finflow.bottom_fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finflow.Model.StocksData;
import com.example.finflow.R;
import com.example.finflow.StockAdapter;
import com.example.finflow.StockToCompanyMapRunnable;
import com.example.finflow.StocksDataDowloaderRunnable;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StocksWatchlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StocksWatchlistFragment extends Fragment implements View.OnClickListener,View.OnLongClickListener{

    private static final String TAG = "Stocks";
    private final List<StocksData> stocksList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private static final String STOCK_URL = "http://www.marketwatch.com/investing/stock/";
    private String someStock;
//    private BottomAppBar bottomAppBar;
//    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StocksWatchlistFragment.
     */


    // TODO: Rename and change types and number of parameters
    public static StocksWatchlistFragment newInstance(String param1, String param2) {
        StocksWatchlistFragment fragment = new StocksWatchlistFragment();
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
        setHasOptionsMenu(true);

        SharedPreferences prefs = getActivity().getSharedPreferences("StockWatchlist", Context.MODE_PRIVATE);
        Set<String> stockSymbols = prefs.getStringSet("stockSymbols", new HashSet<>());

        // Fetch data for each stored stock symbol
        for (String symbol : stockSymbols) {
            fetchStock(symbol);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks_watchlist, container, false);
//        bottomAppBar = view.findViewById(R.id.bottomAppBar1);
//        showBottomAppBar(true);
//        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigation);
//        bottomNavigationView.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.recyclerView);
        stockAdapter = new StockAdapter(stocksList,StocksWatchlistFragment.this);
        recyclerView.setAdapter(stockAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(this::swipeRefreshStocks);

//        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle("FinFlow");


//        // Find the toolbar view inside the inflated layout
//        Toolbar toolbar = view.findViewById(R.id.toolbar);


        //Loading stocks data when opened app
        StockToCompanyMapRunnable mapThread = new StockToCompanyMapRunnable(this);
        new Thread(mapThread).start();
        swipeRefreshStocksFirst();
        // Inflate the layout for this fragment

        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // Retrieve the stored stock symbols from SharedPreferences
//        SharedPreferences prefs = getActivity().getSharedPreferences("StockWatchlist", Context.MODE_PRIVATE);
//        Set<String> stockSymbols = prefs.getStringSet("stockSymbols", new HashSet<>());
//
//        // Fetch data for each stored stock symbol
//        for (String symbol : stockSymbols) {
//            fetchStock(symbol);
//        }
//    }


    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_menu,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addStockMenu) {
            addStockToMainDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Add Stocks Feature
    private void addStockToMainDialog(){
        if(!checkNetConnection()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
            builder.setPositiveButton("OK", (dialogInterface, i) -> {
                // Do nothing!
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        //If connection there tell the user to input stock details
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(editText);

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            someStock = editText.getText().toString().trim();
            ArrayList<String> result = StockToCompanyMapRunnable.findMatch(someStock);

            //checking if the input stock found any result after mapping process

            if(result.size()==0){
                noStockDataDialog(someStock);
            }
            else if(result.size() == 1) {
                fetchStock(result.get(0));
            }

            else{
                String[] arr = result.toArray(new String[0]);
                AlertDialog.Builder builder1 = new AlertDialog.Builder((getActivity()));
                builder1.setTitle("Make a selection");
                builder1.setItems(arr, (dialog, pos) -> {
                    String stockSymbol = result.get(pos);
                    fetchStock(stockSymbol);
                });
                builder1.setNegativeButton("Nevermind", (dialog, pos) -> {
                    // do nothing
                });
                AlertDialog dialog = builder1.create();
                dialog.show();
            }


        });

        builder.setNegativeButton("Cancel", (dialog, id) -> {
            // do nothing
        });

        builder.setMessage("Please enter a Stock Symbol or a Company Name:");
        builder.setTitle("Stock Selection");
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    //fetching requested stock data from API call
    private void fetchStock(String someStock) {
        Log.d("Stock",someStock);
        String[] data = someStock.split("-");
        StocksDataDowloaderRunnable stockDataDownloaderRunnable = new StocksDataDowloaderRunnable(this, data[0].trim());
        new Thread(stockDataDownloaderRunnable).start();
    }


    /*No Stock Dialog*/
    private void noStockDataDialog(String someStock){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Symbol Not Found: " + someStock);
        builder.setMessage("Data for stock symbol " + someStock + " not found");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing!
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


        @Override
    public void onClick(View view) {
        int position = recyclerView.getChildLayoutPosition(view);
        String symbol = stocksList.get(position).getStockSymbol();

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(STOCK_URL + symbol));
        startActivity(browserIntent);

    }




    @Override
    public boolean onLongClick(View view) {
        final int position = recyclerView.getChildLayoutPosition(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.baseline_delete_24));
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                stocksList.remove(position);
                stockAdapter.notifyDataSetChanged();
                writeToJSON();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                // cancelling the dialog, doing nothing!
            }
        });

        builder.setMessage("Delete Stock Symbol " + stocksList.get(position).getStockSymbol()+ "?");
        builder.setTitle("Delete Stock");
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }


    //Writing Stocks data to json

    private void writeToJSON() {
        Log.d(TAG, "writeToJSON: Saving Stocks Data into the JSON File");
        try {
            FileOutputStream outputStream = getActivity().openFileOutput("StockData.json", Context.MODE_PRIVATE);
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            jsonWriter.setIndent(" ");
            jsonWriter.beginArray();
            for(StocksData stock: stocksList) {
                jsonWriter.beginObject();
                jsonWriter.name("stockSymbol").value(stock.getStockSymbol());
                jsonWriter.name("companyName").value(stock.getCompanyName());
                jsonWriter.name("price").value(stock.getPrice());
                jsonWriter.name("priceChange").value(stock.getPriceChange());
                jsonWriter.name("changePercentage").value(stock.getChangePercentage());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.close();
        }
        catch (Exception exception) {
            Log.d(TAG, "writeToJSON: "+ exception.getMessage());
            exception.printStackTrace();
        }
    }

    //Reading Json File

    private void readFromJSON() {
        Log.d(TAG, "readFromJSON: Reading Stocks Data from the JSON File");
        try {
            double price;
            double priceChange;
            double changePercentage;
            FileInputStream inputStream = getActivity().openFileInput("StockData.json");
            byte[] data = new byte[inputStream.available()];
            int loadData = inputStream.read(data);
            Log.d(TAG, "readFromJSON: Loaded Data: " + loadData + " bytes");
            inputStream.close();
            String jsonData = new String(data);

            // Creating JSON Array from JSON object
            JSONArray stockArray = new JSONArray(jsonData);
            for (int i = 0; i < stockArray.length(); i++) {
                JSONObject jsonObject = stockArray.getJSONObject(i);
                String stockSymbol = jsonObject.getString("stockSymbol");
                String companyName = jsonObject.getString("companyName");
                price = jsonObject.getDouble("price");
                priceChange = jsonObject.getDouble("priceChange");
                changePercentage = jsonObject.getDouble("changePercentage");
                StocksData stock = new StocksData(stockSymbol, companyName, price, priceChange, changePercentage);
                stocksList.add(stock);
            }
        }
        catch (Exception exception) {
            Log.d(TAG, "readFromJSON: " + exception.getMessage());
            exception.printStackTrace();
        }
    }


    //Adding stock after recieving API

    public void addStock(StocksData stock) {
        if(stock == null) {
            noStockDataDialog(someStock);
            return;
        }
        if(stocksList.contains(stock)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.baseline_feedback_24));
            builder.setTitle("Duplicate Stock");
            builder.setMessage("Stock Symbol " + stock.getStockSymbol() + " is already displayed.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Do nothing!
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        stocksList.add(stock);
        Collections.sort(stocksList);
        writeToJSON();
        stockAdapter.notifyDataSetChanged();
        // shared preference for adding stock

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("StockWatchlist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> stockSymbols = new HashSet<>();
        for (StocksData stockData : stocksList) {
            stockSymbols.add(stockData.getStockSymbol());
        }
        editor.putStringSet("stockSymbols", stockSymbols);
        editor.apply();
    }


    public void downloadErrorToast() {
        Toast.makeText(getActivity(), "Failed to Download Symbols or Names", Toast.LENGTH_LONG).show();
    }


    private boolean checkNetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)(getActivity().getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void defaultValues() {
        for(StocksData stock: stocksList){
            stock.setPrice(0.0);
            stock.setPriceChange(0.0);
            stock.setChangePercentage(0.0);
        }
    }


    // Swipe Refresh Stock Data
    private void swipeRefreshStocks() {
        if(!checkNetConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Do nothing!
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            swipeRefresh.setRefreshing(false);
            return;
        }

        StockToCompanyMapRunnable nameDownloaderRunnable = new StockToCompanyMapRunnable(this);
        new Thread(nameDownloaderRunnable).start();
        List<StocksData> tempStockList = new ArrayList<>();
        for(StocksData stock: stocksList) {
            tempStockList.add(stock);
        }
        stocksList.clear();
        for(StocksData stock: tempStockList){
            String symbol = stock.getStockSymbol();
            StocksDataDowloaderRunnable stockDownloaderRunnable = new StocksDataDowloaderRunnable(this, symbol);
            new Thread(stockDownloaderRunnable).start();
        }
        swipeRefresh.setRefreshing(false);
    }




    // Swipe Refreshing Data on Initial Loading to Main Activity
    private void swipeRefreshStocksFirst() {
        if (!checkNetConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            defaultValues();
            swipeRefresh.setRefreshing(false);
            return;
        }

        List<StocksData> tempStockList = new ArrayList<>();
        tempStockList.addAll(stocksList);
        stocksList.clear();

        for (StocksData stock : tempStockList) {
            String symbol = stock.getStockSymbol();
            StocksDataDowloaderRunnable stockDownloaderRunnable = new StocksDataDowloaderRunnable(this, symbol);
            new Thread(stockDownloaderRunnable).start();
        }

        swipeRefresh.setRefreshing(false);


    }

//    private void showBottomAppBar(boolean show) {
//        if(view!=null) {
//            BottomAppBar bottomAppBar = view.findViewById(R.id.bottomAppBar);
//            if (bottomAppBar != null) {
//                bottomAppBar.setVisibility(show ? View.VISIBLE : View.GONE);
//            }
//        }
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        showBottomAppBar(true); // Ensure the BottomAppBar is visible when HomeFragment is resumed
//    }
//    private void showBottomAppBar(boolean show) {
//        BottomAppBar bottomAppBar = requireActivity().findViewById(R.id.bottomAppBar);
//        if (bottomAppBar != null) {
//            bottomAppBar.setVisibility(View.VISIBLE);
//        }
//    }

}