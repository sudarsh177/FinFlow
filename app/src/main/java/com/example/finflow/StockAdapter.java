package com.example.finflow;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finflow.Model.StocksData;
import com.example.finflow.bottom_fragment.StocksWatchlistFragment;

import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "StockAdapter";
    final private List<StocksData> stocksList;
    final private StocksWatchlistFragment stocksActivity;


    public StockAdapter(List<StocksData> stocksList, StocksWatchlistFragment stocksActivity) {
        this.stocksList = stocksList;
        this.stocksActivity = stocksActivity;
    }

    @NonNull
    @Override
    //puts the data into the layout file
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: Creating new ViewHolder for stock");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_input, parent, false);

        itemView.setOnClickListener(stocksActivity);
        itemView.setOnLongClickListener(stocksActivity);

        return new StockViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    //changes the data based on the Recycler View's position on each of its items
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        StocksData stock = stocksList.get(position);
        holder.stockSymbol.setText(stock.getStockSymbol());
        holder.companyName.setText(stock.getCompanyName());
        holder.price.setText(String.format(Locale.getDefault(),"%.2f", stock.getPrice()));
        double priceChange = stock.getPriceChange();
        double pricePercent = stock.getChangePercentage();
        String priceChangeStr = String.format(Locale.getDefault(), "%.2f", priceChange);
        String pricePercentStr = String.format(Locale.getDefault(), "%.2f", pricePercent);

        if(priceChange < 0) {
            holder.priceChange.setText("▼ "+ priceChangeStr + "(" + pricePercentStr + "%)");
            holder.companyName.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.priceChange.setTextColor(Color.RED);
            holder.stockSymbol.setTextColor(Color.RED);
        }
        else {
            holder.priceChange.setText("▲ "+ priceChangeStr + "(" + pricePercentStr + "%)");
            holder.companyName.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.priceChange.setTextColor(Color.GREEN);
            holder.stockSymbol.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return stocksList.size();
    }
}
