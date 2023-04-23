package com.artanst.checkczk.utility;

import android.os.AsyncTask;
import com.artanst.checkczk.models.Rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Parser {

    public List<Rate> rates;
    public int leftYear;
    public int rightYear;
    public String[] resultQuieries;

    public Parser() {
        rates = new ArrayList<>();
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String result = "";
            int years = rightYear - leftYear;
            int i = 0;
            for (int year = leftYear; year <= rightYear; year++) {
                try {
                    url += year;
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line + "-");
                        }
                        resultQuieries[i] = stringBuilder.toString();
                        i++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "SUCCESS";
        }

        @Override
        protected void onPostExecute(String result) {
            // Обработка результата запроса
        }
    }
    NetworkTask networkTask;

    public void getRates() throws IOException, ExecutionException, InterruptedException {
        if (resultQuieries == null) {
            networkTask = new NetworkTask();
            resultQuieries = new String[rightYear - leftYear + 1];
            networkTask.execute("https://www.cnb.cz/en/financial_markets/foreign_exchange_market/exchange_rate_fixing/year.txt?year=");

            try {
                String result = networkTask.get(); // ожидание завершения работы AsyncTask
                // дальнейшая обработка результата
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else {
            parseRates();
        }
    }

    private void parseRates()
    {
        List<String> keys = parseKeys(resultQuieries[0].indexOf("-"), resultQuieries[0]);
        for (int i = 0; i < resultQuieries.length; i++) {
            List<List<String>> values = parseValues(resultQuieries[i]);
        }
        HashMap<String, List<String>> maps;
    }

    private List<String> parseKeys(int indexEndKeys, String resultQuiery)
    {
        List<String> keys = new ArrayList();
        String key = new String();
        int ids[];
        for (int i = 0; i < indexEndKeys; i++)
        {
            if (resultQuiery.charAt(i) == '|')
            {
                keys.add(key);
                key = "";
            }
            else if (resultQuiery.charAt(i) == '-')
                return keys;
            else
                key += String.valueOf(resultQuiery.charAt(i));
        }
        return keys;
    }

    private List<List<String>> parseValues(String resultQuiery)
    {
        List<List<String>> values = new ArrayList<List<String>>();
        boolean findKeys = false;
        List<String> currentValues = new ArrayList<>();
        String value = new String();
        for (int i = 0; i < resultQuiery.length(); ++i)
        {
            if (!findKeys) {
                if (resultQuiery.charAt(i) == '-')
                    findKeys = true;
                continue;
            }

            if (resultQuiery.charAt(i) == '|')
            {
                if (!value.isEmpty())
                {
                    currentValues.add(value);
                    value = "";
                }
            }
            else if (resultQuiery.charAt(i) == '-')
            {
                List<String> tmp = new ArrayList<String>(currentValues);
                values.add(tmp);
                currentValues.clear();
            }
            else
            {
                value += String.valueOf(resultQuiery.charAt(i));
            }
        }
        return values;
    }
}
