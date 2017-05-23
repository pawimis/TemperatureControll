package com.example.root.temperaturecontroll.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.root.temperaturecontroll.CustomUIElements.MyCustomHourPicker;
import com.example.root.temperaturecontroll.CustomUIElements.MyCustomTemperaturePicker;
import com.example.root.temperaturecontroll.Database.DbPlan;
import com.example.root.temperaturecontroll.R;

import java.util.ArrayList;
import java.util.Collections;


public class TemperatureControlActivity extends Activity {
    private static final float constant = 255 / 10;
    MyCustomTemperaturePicker myCustomTemperaturePicker;
    TextView temperatureTextView;
    MyCustomHourPicker myCustomHourPicker;
    DbPlan dbPlan;
    long clickTime = 0;
    ArrayList<String> planArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_controll);
        initializeInterfaceElements();
        dbPlan = new DbPlan(getApplicationContext());
        myCustomHourPicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                processTimerUiTouch(event);
                return true;
            }
        });
        myCustomTemperaturePicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                processTemperatureUiTouch(event);
                return true;
            }
        });
        fetchAndUpdateTimerUi();
    }

    private void processTemperatureUiTouch(MotionEvent event) {
        int spot = myCustomHourPicker.getSpot();
        if (spot > 0) {
            myCustomTemperaturePicker.touch(event);
            int temperature = getTemperatureFromRadius();
            if (temperature == 0)
                temperatureTextView.setText("OFF");
            else {
                temperatureTextView.setText(String.valueOf(temperature) + " C");
            }
            colorize(temperature);
            dbPlan.insertRecord(String.valueOf(spot), String.valueOf(temperature));
        }
    }

    private void processTimerUiTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            clickTime = System.currentTimeMillis();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if ((System.currentTimeMillis() - clickTime) > 2000) {
                int temperature = getTemperatureFromRadius();
                dbPlan.setAll(String.valueOf(temperature));
                UpdateTimerUiWithTemperature(temperature);
            }
        }
        myCustomHourPicker.touch(event);
        updateTemperatureUI(myCustomHourPicker.getSpot());
    }

    private void initializeInterfaceElements() {
        myCustomTemperaturePicker = (MyCustomTemperaturePicker) findViewById(R.id.temperature_control_view);
        temperatureTextView = (TextView) findViewById(R.id.temperature_control_textView_name);
        myCustomHourPicker = (MyCustomHourPicker) findViewById(R.id.temperature_control_hour);
    }
    private void colorize(int temperature){
        int red = getTemperatureToRed(temperature);
        int green = getTemperatureToGreen(temperature);
        int blue = getTemperatureToBlue(temperature);
        myCustomHourPicker.colorize(red,green,blue);
        myCustomTemperaturePicker.colorize(red,green,blue);
    }
    private int getTemperatureFromRadius(){
        int rad = myCustomTemperaturePicker.getRadius();
        if(rad <=10) return 0;
        rad = rad - 10;
        return rad/6;
    }

    private void colorizeTimer(int temperature) {
        int red = getTemperatureToRed(temperature);
        int green = getTemperatureToGreen(temperature);
        int blue = getTemperatureToBlue(temperature);
        myCustomHourPicker.colorize(red, green, blue);
    }
    private void updateTemperatureUI(int spot){
        String temp = dbPlan.getRecord(String.valueOf(spot));
        if(temp.isEmpty() || temp.equals("0")){
            temperatureTextView.setText("OFF");
            temp = "0";
        } else{
            temperatureTextView.setText(temp + " C");
        }
        int temperature = Integer.parseInt(temp);
        if(temperature<0) { temperature = 0;}
        int radius = 10+(temperature)*6;
        myCustomTemperaturePicker.setRadius(radius);
        colorize(temperature);
    }
    private void fetchAndUpdateTimerUi(){
        planArrayList = dbPlan.getAllRecords();
        Collections.reverse(planArrayList);
        int temperature;
        for(String tempByHour:planArrayList) {
            String[] hourTemp = tempByHour.split("/");
            myCustomHourPicker.setSpot(Integer.parseInt(hourTemp[0]));
            temperature = Integer.parseInt(hourTemp[1]);
            colorizeTimer(temperature);
        }
        myCustomHourPicker.setSpot(0);
        myCustomTemperaturePicker.colorize(0, 0, 0);
    }

    private void UpdateTimerUiWithTemperature(int temperature) {
        for (int i = 1; i < 25; i++) {
            myCustomHourPicker.setSpot(i);
            colorizeTimer(temperature);
        }
        myCustomHourPicker.setSpot(0);

    }
    private int getTemperatureToRed(int temperature){
        if(temperature >30 ) return 255;
        if(temperature > 20) return (int)((temperature-20) * constant );
        return 0;
    }
    private int getTemperatureToBlue(int temperature){
        if(temperature <= 10) return 255;
        else if (temperature <= 20) return (int)(255 - ((temperature - 10) * constant ));
        else return 0;
    }
    private int getTemperatureToGreen(int temperature){
        if(temperature <= 10) return (int)((temperature - 1) *constant );
        else if (temperature > 30) return (int)(255 - ((temperature - 30) * constant ));
        else return 255;
    }

}
