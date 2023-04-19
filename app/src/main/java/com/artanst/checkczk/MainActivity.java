package com.artanst.checkczk;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.artanst.checkczk.utility.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final String[] KEYS = {"AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "DKK", "EUR", "GBP",
            "HKD", "HUF", "IDR", "ILS", "INR", "ISK", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD",
            "PHP", "PLN", "RON", "SEK", "SGD", "THB", "TRY", "USD", "XDR", "ZAR"};

    private DatePicker datePicker;
    private TextView firstDate, secondDate, hint;
    private Spinner currencies;
    private Button reset, accept;

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

        parser = new Parser();

        setSpinner();
        setAcceptListener();

    }

    private void parseData() {

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

                String date = String.valueOf(day) + "." + String.valueOf(month) + "." + String.valueOf(year);

                if (leftDate.isEmpty())
                {
                    parser.leftYear = year;
                    firstDate.setText(date);
                    hint.setText("Введите 2 дату");
                }
                else if (rightDate.isEmpty()) {
                    parser.rightYear = year;
                    secondDate.setText(date);
                    hint.setText("Нажмите потвердить для формирования отчета");
                }
                else {
                    try {
                        parser.parseRates();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void setResetListener() {

    }

    private void setSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, KEYS)  {
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
}