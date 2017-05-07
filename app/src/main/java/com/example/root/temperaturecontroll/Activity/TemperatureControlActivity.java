package com.example.root.temperaturecontroll.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.root.temperaturecontroll.Database.DbPlan;
import com.example.root.temperaturecontroll.R;

import java.util.ArrayList;
import java.util.Collections;


public class TemperatureControlActivity extends Activity {
    MyCustomTemperaturePicker myCustomTemperaturePicker;
    TextView temperatureTextView;
    MyCustomHourPicker myCustomHourPicker;
    DbPlan dbPlan;
    ArrayList<String> planArrayList;
    private static final float constant = 255/10;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_controll);
        dbPlan = new DbPlan(getApplicationContext());

        myCustomTemperaturePicker = (MyCustomTemperaturePicker) findViewById(R.id.temperature_control_view);
        temperatureTextView = (TextView) findViewById(R.id.temperature_control_textView_name);
        myCustomHourPicker = (MyCustomHourPicker) findViewById(R.id.temperature_control_hour);
        myCustomHourPicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myCustomHourPicker.touch(event);
                updateTemperatureUI(myCustomHourPicker.getSpot());
                return true;
            }
        });
        myCustomTemperaturePicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int spot = myCustomHourPicker.getSpot();
                if (spot > 0) {
                    int temperature = getTemperatureFromRadius();
                    if (temperature == 0)
                        temperatureTextView.setText("OFF");
                    else{
                        temperatureTextView.setText(String.valueOf(temperature) + " C");
                    }
                    colorize(temperature);
                    dbPlan.insertRecord(String.valueOf(spot), String.valueOf(temperature));
                    myCustomTemperaturePicker.touch(event);
                    return true;
                }
                return false;
            }
        });
        fetchAndUpdateTimerUi();
    }
    private void colorize(int temperature){
        int red = getTemperatureToRed(temperature);
        int green = getTemperatureToGreen(temperature);
        int blue = getTemperatureToBlue(temperature);
        Log.i("colors: ", String.valueOf(red) + "," + String.valueOf(green) +","+ String.valueOf(blue));
        myCustomHourPicker.colorize(red,green,blue);
        myCustomTemperaturePicker.colorize(red,green,blue);
    }
    private int getTemperatureFromRadius(){
        int rad = myCustomTemperaturePicker.getRadius();
        if(rad <=10) return 0;
        rad = rad - 10;
        return rad/6;
    }
    private void updateTemperatureUI(int spot){
        String temp = dbPlan.getRecord(String.valueOf(spot));
        if(temp.isEmpty() || temp.equals("0")){
            temperatureTextView.setText("OFF");
            temp = "0";
        } else{
            temperatureTextView.setText(temp);
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
            if(temperature < 18){
                myCustomHourPicker.colorize(0,0,255);
            }else {
                colorize(temperature);
            }
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
