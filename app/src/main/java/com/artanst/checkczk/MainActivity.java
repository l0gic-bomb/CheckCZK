package com.artanst.checkczk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.artanst.checkczk.utility.DatabaseManager;
import com.artanst.checkczk.utility.Parser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public final String[] KEYS_CURRENCIES = {"AUD", "CAD", "EUR", "GBP", "NZD", "TRY", "USD"};

    private DatePicker datePicker;
    private TextView firstDate, secondDate, hint;
    private Spinner currencies;
    private Button reset, accept, printConsoleBtn;

    private TextView minTextView, maxTextView, avgTextView;

    private LinearLayout layout;

    private Parser parser;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.datePicker);
        firstDate = findViewById(R.id.firstDate);
        secondDate = findViewById(R.id.secnodDate);
        hint = findViewById(R.id.hintTextView);
        currencies = findViewById(R.id.currenciesSpinner);
        accept = findViewById(R.id.acceptButton);
        reset = findViewById(R.id.resetButton);
        printConsoleBtn = findViewById(R.id.printConsole);

        minTextView = findViewById(R.id.valueMIN);
        maxTextView = findViewById(R.id.valueMAX);
        avgTextView = findViewById(R.id.valueAVG);

        parser = new Parser();

        setSpinner();
        setAcceptListener();
        setResetListener();
        setPrintConsoleBtnListener();
    }

    private void setAcceptListener() {
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String leftDate = firstDate.getText().toString();
                String rightDate = secondDate.getText().toString();

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

                String date = new String();
                if (month < 10)
                    date = String.valueOf(day) + ".0" + String.valueOf(month) + "." + String.valueOf(year);
                else
                    date = String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year);

                if (leftDate.isEmpty())
                {
                    parser.leftYear = year;
                    firstDate.setText(date);
                    hint.setText("Введите 2 дату");
                }
                else if (rightDate.isEmpty()) {
                    parser.rightYear = year;
                    secondDate.setText(date);
                    hint.setText("Нажмите подтвердить для формирования отчета");
                }
                else {
                    boolean result = false;
                    try {
                        result = parser.getRates();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (result)
                    {
                        for (int i = 0; i < parser.values.size(); ++i) {
                            DatabaseManager.getInstance(getApplicationContext()).addData(parser.values.get(i));
                        }
                        printConsoleBtn.setEnabled(true);
                        try {
                            setJson();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }
        });
    }

    private void setResetListener() {
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstDate.setText("");
                secondDate.setText("");
                hint.setText("Введите 1 дату");
            }
        });
    }

    private void setPrintConsoleBtnListener() {
        printConsoleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout =  (LinearLayout) findViewById(R.id.min_max_avr_layout);
                layout.setVisibility(View.VISIBLE);;
                String[] values = new String[3];
                values = DatabaseManager.getInstance(getApplicationContext()).getAverage(firstDate.getText().toString(), secondDate.getText().toString(), currencies.getSelectedItem().toString());
                minTextView.setText(values[0]);
                maxTextView.setText(values[1]);
                avgTextView.setText(values[2]);
            }
        });
    }

    private void setSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KEYS_CURRENCIES)  {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextSize(36); // задаем размер текста
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencies.setAdapter(adapter);
    }

    private void setJson() throws FileNotFoundException {
        List<List<String>> values = parser.values;
        List<String> keys = parser.keysKeys;

        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < values.size(); ++i)
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(keys.get(0), values.get(i).get(0) );
            jsonObject.addProperty(keys.get(1), values.get(i).get(1) );
            jsonObject.addProperty(keys.get(2), values.get(i).get(2) );
            jsonObject.addProperty(keys.get(3), values.get(i).get(3) );
            jsonObject.addProperty(keys.get(4), values.get(i).get(4) );
            jsonObject.addProperty(keys.get(5), values.get(i).get(5) );
            jsonObject.addProperty(keys.get(6), values.get(i).get(6) );
            jsonObject.addProperty(keys.get(7), values.get(i).get(7) );
            jsonArray.add(jsonObject);
        }

        FileOutputStream fos = openFileOutput("output.json", Context.MODE_PRIVATE);
        try {
            fos.write(jsonArray.toString().getBytes());
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();
        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}