package com.example.finflow;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockViewHolder extends RecyclerView.ViewHolder {


    TextView stockSymbol;
    TextView companyName;
    TextView price;
    TextView priceChange;
    public StockViewHolder(@NonNull View itemView) {
        super(itemView);
        stockSymbol = itemView.findViewById(R.id.stockSymbol);
        companyName = itemView.findViewById(R.id.companyName);
        price = itemView.findViewById(R.id.price);
        priceChange = itemView.findViewById(R.id.priceChange);


    }
}
