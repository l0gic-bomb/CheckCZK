package com.artanst.checkczk.utility;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Parser {

    public int leftYear;
    public int rightYear;
    public String[] resultQuieries;

    public List<String> ids;
    public List<List<String>> values;
    public List<String> keysKeys;
    public Parser() {
        values = new ArrayList<List<String>>();
        ids = new ArrayList<>();
    }

    public class YearlyTask {
        public static void runYearlyTask(Runnable task) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    task.run();
                }
            }, 0, 365 * 24 * 60 * 60 * 1000);
        }
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
                    url = params[0];
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

    public boolean getRates() throws IOException, ExecutionException, InterruptedException {
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
        parseRates();

        /*YearlyTask.runYearlyTask(() -> {
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
            try {
                parseRates();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });*/

        return true;
    }

    private void parseRates() throws IOException {
        keysKeys = parseKeys(resultQuieries[0].indexOf("-"), resultQuieries[0]);
        values = parseValues(resultQuieries);
    }

    private List<String> parseKeys(int indexEndKeys, String resultQuiery)
    {
        List<String> keys = new ArrayList();
        String key = new String();
        final String[] KEYS = {"Date", "AUD", "CAD", "EUR", "GBP", "NZD", "TRY", "USD"};

        int currentKey = 0;
        for (int i = 0; i < indexEndKeys; i++)
        {
            if (resultQuiery.charAt(i) == '|')
            {
                currentKey++;
                if (Arrays.asList(KEYS).contains(key))
                {
                    ids.add(String.valueOf(currentKey));
                    keys.add(key);
                }
                key = "";
            }
            else if (resultQuiery.charAt(i) == '-')
                return keys;
            else if (resultQuiery.charAt(i) != '1' && resultQuiery.charAt(i) != '0' && resultQuiery.charAt(i) != ' ')
                key += String.valueOf(resultQuiery.charAt(i));
        }
        return keys;
    }

    private List<List<String>> parseValues(String[] resultQuiery)
    {
        List<List<String>> values = new ArrayList<List<String>>();
        boolean findKeys = false;
        List<String> currentValues = new ArrayList<>();
        int id = 0;
        String value = new String();
        for (int k = 0; k < resultQuiery.length; ++k) {
            for (int i = 0; i < resultQuiery[k].length(); ++i) {
                if (!findKeys) {
                    if (resultQuiery[k].charAt(i) == '-')
                        findKeys = true;
                    continue;
                }

                if (resultQuiery[k].charAt(i) == '|') {
                    ++id;
                    if (!value.isEmpty()) {
                        if (ids.contains(String.valueOf(id))) {
                            currentValues.add(value);
                        }
                        value = "";
                    }
                } else if (resultQuiery[k].charAt(i) == '-') {
                    currentValues.add(value);
                    List<String> tmp = new ArrayList<String>(currentValues);
                    values.add(tmp);
                    currentValues.clear();
                    id = 0;
                    value = "";
                } else {
                    value += String.valueOf(resultQuiery[k].charAt(i));
                }
            }
        }
        return values;
    }
}
