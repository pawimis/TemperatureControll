package com.example.root.temperaturecontroll.Activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.root.temperaturecontroll.Database.DbTemperature;
import com.example.root.temperaturecontroll.Database.Temperature;
import com.example.root.temperaturecontroll.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    private final static String TAG = "Graph ";
    TextView dateDiff;
    SimpleDateFormat dateFormat;
    GraphView graphView;
    TextView dateText;
    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        dateDiff = (TextView) findViewById(R.id.activity_graph_TextView_TempDiff);
        dateText = (TextView) findViewById(R.id.activity_graph_Date);
        dateText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDatePicker();
                return true;
            }
        });
        graphView = (GraphView) findViewById(R.id.graph);
        graphView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDatePicker();
                return true;
            }
        });

        dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        setupUI(graphView,currentDate);


    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        if (mYear == 0 && mMonth == 0 && mDay == 0) {
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }
        System.out.println("the selected " + mDay);
        DatePickerDialog dialog = new DatePickerDialog(GraphActivity.this,
                new mDateSetListener(), mYear, mMonth, mDay);
        dialog.show();
    }

    private void setupUI(GraphView graphView, String date) {
        dateText.setText(date);
        DbTemperature dbTemperature = new DbTemperature(getApplicationContext());
        ArrayList<Temperature> arrayListTemperature = dbTemperature.getTemperatureOnDay(date);
        Log.i(TAG + "temperature size", String.valueOf(arrayListTemperature.size()));
        if (arrayListTemperature.size() < 1) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("No temperature data collected on this day").setCancelable(false).setTitle("No Data").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    showDatePicker();
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

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getDataSeries(arrayListTemperature));
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
        dateDiff.setText("Temperature Difference:" + dbTemperature.getMinDTempOnDay(date) + " C -" + dbTemperature.getMaxTempOnDay(date) + " C");
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

    private class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, mDay);
            String strDate = dateFormat.format(calendar.getTime());
            setupUI(graphView, strDate);
        }
    }
}
