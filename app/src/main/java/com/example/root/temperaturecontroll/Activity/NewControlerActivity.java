package com.example.root.temperaturecontroll.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.root.temperaturecontroll.R;
import com.example.root.temperaturecontroll.Service.RecyclerTouchListener;
import com.example.root.temperaturecontroll.Service.WifiSsidAdapter;

import java.util.List;

public class NewControlerActivity extends Activity {

    ProgressDialog progressDialog;
    Thread t = new Thread() {
        @Override
        public void run() {
            try {
                NewControlerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = new ProgressDialog(NewControlerActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                });

                while (!isConnected()) {
                    Thread.sleep(1000);
                }
                NewControlerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.hide();
                    }
                });
                startWebActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    WifiThread wifiThread;
    WifiManager wifiManager;
    List<ScanResult> mScanResults;
    private WifiSsidAdapter wifiSsidAdapter;
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction() == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                mScanResults = wifiManager.getScanResults();
                wifiSsidAdapter.notifyDataSetChanged();
            }
        }
    };
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_controler);
        wifiThread = new WifiThread();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mScanResults = wifiManager.getScanResults();
        wifiSsidAdapter = new WifiSsidAdapter(mScanResults);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWifi();
        }
        else{
            wifiThread.start();
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(wifiSsidAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),recyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                connectWithAP(position);
            }
            @Override
            public void onLongClick(View view, int position) {
                connectWithAP(position);
            }
        }));

    }

    private void connectWithAP(int position) {
        if (wifiThread != null) {
            wifiThread.stopThread();
        }
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = mScanResults.get(position).SSID;
        wifiConfig.preSharedKey = String.format("\"%s\"", "bajabongo");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        if (!t.isAlive())
            t.start();
    }

    @Override
    protected void onStop() {
        if (mWifiScanReceiver != null)
            unregisterReceiver(mWifiScanReceiver);
        if (wifiThread != null) {
            wifiThread.stopThread();
        }
        super.onStop();
    }

    private void getWifi() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
        } else {
            wifiThread.start();
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
        Intent i = new Intent(NewControlerActivity.this, SetupActivity.class);
        startActivity(i);
        finish();
    }

    class WifiThread extends Thread {
        boolean run = true;

        @Override
        public void run() {
            try {
                while (run) {
                    wifiManager.startScan();
                    Thread.sleep(10000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void stopThread() {
            run = false;
        }
    }

}
