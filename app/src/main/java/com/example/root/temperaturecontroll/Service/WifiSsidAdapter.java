package com.example.root.temperaturecontroll.Service;

import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.temperaturecontroll.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by root on 02.04.17.
 */

public class WifiSsidAdapter extends RecyclerView.Adapter<WifiSsidAdapter.MyViewHolder>{
    List<ScanResult> scanList;
    public WifiSsidAdapter(List<ScanResult> scanList){
        this.scanList = scanList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wifi_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WifiSsidAdapter.MyViewHolder holder, int position) {

        Log.i("Binder","Bind " + scanList.get(position).SSID);
        holder.name.setText(scanList.get(position).SSID);
    }

    @Override
    public int getItemCount() {
        Log.i("List Size",String.valueOf(scanList.size()));
        return scanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name;

        public MyViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.wifi_list_row_name);
            }


    }

}
