package com.example.root.temperaturecontroll.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MenuActivity extends Activity {
    Button graph;
    Button clear;
    Button refresh;
    Button control;
    Button addNew;
    Button test;
    ProgressDialog progressDialog;
    DbTemperature dbTemperature;
    DbPlan dbPlan;
    TextView minTemp;
    TextView maxTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        updateDatabase();
        dbTemperature = new DbTemperature(getApplicationContext());
        dbPlan = new DbPlan(getApplicationContext());
        minTemp = (TextView) findViewById(R.id.activity_menu_text_temp_min);
        maxTemp = (TextView) findViewById(R.id.activity_menu_text_temp_max);

        test = (Button) findViewById(R.id.activity_menu_button_test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,TestActivity.class);
                startActivity(i);
            }
        });
        graph = (Button) findViewById(R.id.activity_menu_button_show_graph);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGraph();
            }
        });
        clear = (Button) findViewById(R.id.activity_menu_button_clear_db);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDatabase();
            }
        });
        refresh = (Button) findViewById(R.id.activity_menu_button_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDatabase();
            }
        });
        control = (Button) findViewById(R.id.activity_menu_button_control);
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTemperatureValues();
            }
        });
        addNew = (Button) findViewById(R.id.activity_menu_button_add_new);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNew();
            }
        });
        String minDate = dbTemperature.getMinDate();
        String maxDate = dbTemperature.getMaxDate();
        minTemp.setText("min Date " + minDate);
        maxTemp.setText("max Date " + maxDate);


    }

    private void updateDatabase() {
        DatabaseUpdater update = new DatabaseUpdater();
        update.execute();

    }
    private void deleteDatabase(){
        DbTemperature db = new DbTemperature(getApplicationContext());
        db.removeAll();
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
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),results,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String URL = Variables.SERVER_BASE + Variables.GET_ALL_TEMP;
            String data;
            String result = "No new data to fetch";
            try {
                data = GetDataService.sendRequest(URL, Credentials.getToken(getApplicationContext()));
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

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

}
