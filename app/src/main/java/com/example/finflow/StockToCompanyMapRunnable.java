package com.example.finflow;

import android.net.Uri;
import android.util.Log;

import com.example.finflow.bottom_fragment.StocksWatchlistFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class StockToCompanyMapRunnable implements  Runnable {

    private static final String TAG = "StockToCompanyMapRunnable";
    private static final String SYM_NAME_URL = "https://dumbstockapi.com/stock?exchanges=NASDAQ";
    public static HashMap<String, String> symbolCompNameMap = new HashMap<>();
    final private StocksWatchlistFragment stocks;

    public StockToCompanyMapRunnable(StocksWatchlistFragment stocks) {
        this.stocks = stocks;
    }

    @Override
    public void run() {

        Uri dataUri = Uri.parse(SYM_NAME_URL);
        String urlBuilt = dataUri.toString();
        Log.d(TAG, "run: " + urlBuilt);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlBuilt);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode = NOT OK: " + connection.getResponseCode());
                stocks.getActivity().runOnUiThread(stocks::downloadErrorToast);

                return;
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }

            Log.d(TAG, "run: " + stringBuilder.toString());

        } catch (Exception exception) {
            Log.e(TAG, "run: ", exception);
            exception.printStackTrace();
            return;
        }

        processStock(stringBuilder.toString());
        Log.d(TAG, "run: ");

    }

    private void processStock(String stock) {
        try {
            JSONArray jsonArray = new JSONArray(stock);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                String symbol = jsonObject.getString("ticker");
                String name = jsonObject.getString("name");

                symbolCompNameMap.put(symbol, name);
            }

            Log.d(TAG, "processStock: ");
        } catch (Exception exception) {
            Log.d(TAG, "processStock: " + exception.getMessage());
            exception.printStackTrace();

        }

    }

    public static ArrayList<String> findMatch(String string) {
        String stringToMatch = string.toLowerCase().trim();
        HashSet<String> matchSet = new HashSet<>();

        for(String stockSymbol: symbolCompNameMap.keySet()) {
            if(stockSymbol.toLowerCase().trim().contains(stringToMatch)) {
                matchSet.add(stockSymbol + " - " + symbolCompNameMap.get(stockSymbol));
            }
            String compName = symbolCompNameMap.get(stockSymbol);
            if(compName != null && compName.toLowerCase().trim().contains(stringToMatch)) {
                matchSet.add(stockSymbol + " - " + compName);
            }
        }

        ArrayList<String> resultSet = new ArrayList<>(matchSet);
        Collections.sort(resultSet);

        return resultSet;
    }


}



