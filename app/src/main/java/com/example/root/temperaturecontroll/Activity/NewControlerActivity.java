package com.example.root.temperaturecontroll.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.root.temperaturecontroll.R;
import com.example.root.temperaturecontroll.Service.RecyclerTouchListener;
import com.example.root.temperaturecontroll.Service.WifiSsidAdapter;

import java.util.List;

public class NewControlerActivity extends Activity {
    Thread t = new Thread() {
        @Override
        public void run() {
            try {
                Log.i("Thread STARTED","Thread");
                while (!isConnected()) {
                    Thread.sleep(1000);
                    Log.i("Waiting","WIFI");
                }
                Log.i("Start","Web");
                startWebActivity();

            } catch (Exception e) {
            }
        }
    };
    WifiManager wifiManager;
    List<ScanResult> mScanResults;
    Boolean thread = true;
    private WifiSsidAdapter wifiSsidAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_controler);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mScanResults = wifiManager.getScanResults();
        wifiSsidAdapter = new WifiSsidAdapter(mScanResults);
        Log.i("StartScan","Scan");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("getWifi2", "Scanning");
            getWifi();
        }
        else{
            Log.i("Scan1","Scanning");
            wifiManager.startScan();
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(wifiSsidAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),recyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = mScanResults.get(position).SSID;
                wifiConfig.preSharedKey = String.format("\"%s\"", "ckany41AJ");
                WifiManager wifiManager=(WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();
                Log.i("Thread","Thread");
                if(!t.isAlive())
                    t.start();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                mScanResults = wifiManager.getScanResults();
                Log.i("Scan Results","Scan");
                Log.i("Amount",String.valueOf(mScanResults.size()));
                wifiSsidAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onStop() {
        unregisterReceiver(mWifiScanReceiver);
        super.onStop();
    }
    private void getWifi() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
        } else {
            Log.i("Scan2","Scanning");
            wifiManager.startScan(); // the actual wifi scanning
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0x12345) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            getWifi();
        }
    }

    public  boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void startWebActivity(){
        Log.i("start web activity","started");
        Intent i = new Intent(NewControlerActivity.this, WebActivity.class);
        startActivity(i);
        finish();
    }

}
