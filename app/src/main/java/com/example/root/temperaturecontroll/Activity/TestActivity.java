package com.example.root.temperaturecontroll.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.HashMap;


public class TestActivity extends Activity{
    Button plan;
    Button temp;
    Button testData;
    DbPlan dbPlan;
    DbTemperature dbTemperature;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        dbPlan = new DbPlan(getApplicationContext());
        dbTemperature = new DbTemperature(getApplicationContext());

        plan = (Button) findViewById(R.id.test_get_plan);
        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbPlan.getAllRecords();
                Log.i("Size",String.valueOf(dbPlan.numberOfRows()));
            }
        });
        temp = (Button) findViewById(R.id.test_get_temp);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbTemperature.getAllDates();
                dbTemperature.getAllTemperatures();
            }
        });
        testData = (Button) findViewById(R.id.test_sendData);
        testData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseUpdater databaseUpdater = new DatabaseUpdater();
                databaseUpdater.execute();
            }
        });

    }
    private class DatabaseUpdater extends AsyncTask<Void,Void,String> {
        ArrayList<String> planArrayList;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            planArrayList = new ArrayList<>();
            progressDialog = new ProgressDialog(TestActivity.this);
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
            planArrayList = dbPlan.getAllRecords();
            HashMap<String,String> map = new HashMap<>();
            String [] dataTable;
            for(String dataString : planArrayList ){
                dataTable = dataString.split("/");
                map.put(dataTable[0],dataTable[1]);
            }
            SendJsonService.sendJsonPostRequest(Variables.SERVER_BASE + Variables.SET_HOME_TEMP,map);
            return "done";
        }
    }
}
