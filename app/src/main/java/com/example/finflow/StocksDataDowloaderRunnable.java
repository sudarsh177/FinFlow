package com.example.finflow;

import android.net.Uri;
import android.util.Log;

import com.example.finflow.Model.StocksData;
import com.example.finflow.bottom_fragment.StocksWatchlistFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StocksDataDowloaderRunnable implements Runnable{
    private static final String TAG = "StocksDataDowloaderRunnable";
    private static final String STOCK_URL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=";
    private static final String API_TOKEN = "&apikey=VP8WP2DG1ARR4Z2B";
    private StocksWatchlistFragment stocks;
    private String searchStock;

    public StocksDataDowloaderRunnable(StocksWatchlistFragment stocks , String searchStock) {
        this.stocks = stocks;
        this.searchStock = searchStock;
    }

    @Override
    public void run() {

        Uri.Builder uriBuilder = Uri.parse(STOCK_URL + searchStock + API_TOKEN).buildUpon();
        String urlBuilt = uriBuilder.toString();
        Log.d(TAG, "run: " + urlBuilt);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlBuilt);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode = NOT OK" + connection.getResponseCode());
                return;
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            Log.d(TAG, "run: " + stringBuilder.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            Log.e(TAG, "run: ", exception);
        }

        processStock(stringBuilder.toString());

    }


    private void processStock(String processStock) {
        try {
            JSONObject jsonObjectStock = new JSONObject(processStock);
            final String stockSymbol = jsonObjectStock.getJSONObject("Global Quote").getString("01. symbol");
            String companyName = jsonObjectStock.getJSONObject("Global Quote").getString("01. symbol");
            String currentPrice = jsonObjectStock.getJSONObject("Global Quote").getString("05. price");
            double price = 0.0;
            if (!currentPrice.trim().isEmpty())
                price = Double.parseDouble(currentPrice);
            String changePrice = jsonObjectStock.getJSONObject("Global Quote").getString("09. change");
            double priceChange = 0.0;
            if (!changePrice.trim().isEmpty())
                priceChange = Double.parseDouble(changePrice);
            String changePercentage = jsonObjectStock.getJSONObject("Global Quote").getString("10. change percent");
            double changePercent = 0.0;
            if (!changePercentage.trim().isEmpty())
                // Remove the percentage sign and parse as a double
                changePercent = Double.parseDouble(changePercentage.replace("%", ""));
            StocksData stock = new StocksData(stockSymbol, companyName, price, priceChange, changePercent);
            stocks.getActivity().runOnUiThread(() -> {
                stocks.addStock(stock);
                Log.d(TAG, "processStock: Stock added successfully! runOnUiThread: " + stockSymbol);
            });

        } catch (Exception exception) {
            Log.e(TAG, "processStock: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
