package com.example.root.temperaturecontroll.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;


import com.example.root.temperaturecontroll.Database.DbTemperature;
import com.example.root.temperaturecontroll.Database.Temperature;
import com.example.root.temperaturecontroll.R;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class GraphActivity extends AppCompatActivity {


    SimpleDateFormat timeFormat;
    SimpleDateFormat dateFormat;
    Button datePick;
    GraphView graphView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graphView = (GraphView) findViewById(R.id.graph);
        datePick = (Button) findViewById(R.id.datepicker);
        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                System.out.println("the selected " + mDay);
                DatePickerDialog dialog = new DatePickerDialog(GraphActivity.this,
                        new mDateSetListener(), mYear, mMonth, mDay);
                dialog.show();
            }
        });
        dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        setupUI(graphView,currentDate);


    }
    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            setupUI(graphView,new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append("").toString());
        }
    }
    private void setupUI(GraphView graphView,String date){

        DbTemperature dbTemperature = new DbTemperature(getApplicationContext());

        ArrayList<Temperature> arrayListTemperature = dbTemperature.getTemperatureOnDay(date);
        if(arrayListTemperature.isEmpty()){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("No temperature data collected on this day").setCancelable(false).setTitle("No Data").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
            return;
        }
        graphView.removeAllSeries();
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(false);
        graphView.getViewport().setScrollableY(false);
        graphView.getViewport().setMinY(-20);
        graphView.getViewport().setMaxY(40);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(getDataSeries(arrayListTemperature));
        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(5);
        graphView.getGridLabelRenderer().setHumanRounding(true);

        graphView.getGridLabelRenderer().setTextSize(18);
        graphView.getGridLabelRenderer().setLabelsSpace(5);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setGridColor(Color.WHITE);


        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()) {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    int hour = (int) value;
                    int minutes = (int) (60* (value - hour));
                    if(minutes == 0)
                        return String.valueOf(hour + ":00");
                    else
                        return String.valueOf(hour + ":" + minutes);

                } else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }
    DataPoint[] getDataSeries(ArrayList<Temperature> temperatureArrayList){
        DataPoint[] dataPoints = new DataPoint[temperatureArrayList.size()];
        int counter  = 0;
        for(Temperature data : temperatureArrayList){
            DataPoint dataPoint = new DataPoint(Double.parseDouble(data.getTime().replace(':','.').substring(0,5))
                    ,Double.parseDouble(data.getTemp()));
            dataPoints[counter] = dataPoint;
            counter++;
        }
        return dataPoints;
    }
}
