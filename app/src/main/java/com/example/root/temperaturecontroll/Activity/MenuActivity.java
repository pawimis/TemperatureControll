package com.example.root.temperaturecontroll.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.temperaturecontroll.Credentials;
import com.example.root.temperaturecontroll.Database.DbPlan;
import com.example.root.temperaturecontroll.Database.DbTemperature;
import com.example.root.temperaturecontroll.R;
import com.example.root.temperaturecontroll.Service.GetDataService;
import com.example.root.temperaturecontroll.Service.SendJsonService;
import com.example.root.temperaturecontroll.Variables;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MenuActivity extends Activity {
    Button graph;
    Button control;
    Button addNew;
    Button save;
    TextView current;
    TextView average;
    TextView averageToday;
    TextView isOn;
    ProgressDialog progressDialog;
    DbTemperature dbTemperature;
    DbPlan dbPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        dbTemperature = new DbTemperature(getApplicationContext());
        dbPlan = new DbPlan(getApplicationContext());
        initializeInterfaceElements();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
            }
        });
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGraph();
            }
        });
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTemperatureValues();
            }
        });
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNew();
            }
        });
        updateDatabase();
    }

    private void initializeInterfaceElements() {
        save = (Button) findViewById(R.id.activity_menu_button_save);
        graph = (Button) findViewById(R.id.activity_menu_button_show_graph);
        control = (Button) findViewById(R.id.activity_menu_button_control);
        addNew = (Button) findViewById(R.id.activity_menu_button_add_new);
        current = (TextView) findViewById(R.id.activity_menu_text_curent_temp);
        average = (TextView) findViewById(R.id.activity_menu_text_average);
        averageToday = (TextView) findViewById(R.id.activity_menu_text_average_on_day);
        isOn = (TextView) findViewById(R.id.activity_menu_text_is_active);
    }
    private void updateDatabase() {
        DatabaseUpdater update = new DatabaseUpdater();
        update.execute();

    }
    private void showGraph(){
        Intent intent = new Intent(MenuActivity.this,GraphActivity.class);
        startActivity(intent);
    }
    private  void addNew(){
        Intent intent = new Intent(MenuActivity.this,NewControlerActivity.class);
        startActivity(intent);
    }
    private void setTemperatureValues(){
        Intent intent = new Intent(MenuActivity.this,TemperatureControlActivity.class);
        startActivity(intent);
    }

    private void setupDetails() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String averageTemp = dbTemperature.getAverageTemperatures();
        String averageDay = dbTemperature.getAverageTemperaturesOnDay(currentDate);
        String now = dbTemperature.getCurrentTemperature();
        Calendar c = Calendar.getInstance();
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        float planned = Float.parseFloat(dbPlan.getRecord(hour));
        float tempNow = Float.parseFloat(now);
        isOn.setText("OFF");
        Log.i("Planned", String.valueOf(planned));
        Log.i("now", now);
        if (planned > tempNow) {
            isOn.setText("ON");
            isOn.setTextColor(Color.RED);
        }
        current.setText(now + " C");
        average.setText(averageTemp + " C");
        averageToday.setText(averageDay + " C");


    }

    private class DatabaseUpdater extends AsyncTask<Void,Void,String>{
        ArrayList<String> planArrayList;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            planArrayList = new ArrayList<>();
            progressDialog = new ProgressDialog(MenuActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);
            dismissProgressDialog();
            setupDetails();
            Toast.makeText(getApplicationContext(),results,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            dismissProgressDialog();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String URL = Variables.SERVER_BASE + Variables.GET_ALL_TEMP;
            String data;
            String result = "No new data to fetch";
            try {
                data = GetDataService.sendRequest(URL);
                if(data != null) {
                    try {
                        Credentials.processJSONandInsertToDB(new JSONArray(data), dbTemperature);
                        result = "New data";

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
                planArrayList = dbPlan.getAllRecords();
                HashMap<String,String> map = new HashMap<>();
                String [] dataTable;
                for(String dataString : planArrayList ){
                    dataTable = dataString.split("/");
                    map.put(dataTable[0],dataTable[1]);
                }
                SendJsonService.sendJsonPostRequest(Variables.SERVER_BASE + Variables.SET_HOME_TEMP,map);
                dbTemperature.getAllDates();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        private void dismissProgressDialog() {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

}
