package com.artanst.checkczk.utility;

import com.artanst.checkczk.models.Rate;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Parser {

    public List<Rate> rates;
    public int leftYear;
    public int rightYear;

    public Parser() {
        rates = new ArrayList<>();
    }

    public void parseRates() throws IOException {
        Executor executor = Executors.newSingleThreadExecutor();
        String url = "https://www.cnb.cz/en/financial_markets/foreign_exchange_market/exchange_rate_fixing/year.txt?year=";
        OkHttpClient client = new OkHttpClient();
        executor.execute(new Runnable() {
                             @Override
                             public void run() {
                                 for (int year = leftYear; year <= rightYear; ++year)
                                 {
                                     String tmpUrl = url + year;
                                     Request request = new Request.Builder()
                                             .url(tmpUrl)
                                             .build();
                                     Response response = null;
                                     try {
                                         response = client.newCall(request).execute();
                                     } catch (IOException e) {
                                         throw new RuntimeException(e);
                                     }
                                     String html = response.body().toString();
                                 }
                             }
                         });
    }
}
