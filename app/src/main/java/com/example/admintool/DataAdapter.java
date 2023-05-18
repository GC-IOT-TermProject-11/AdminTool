package com.example.admintool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DataAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<WiFiData> sample;

    public DataAdapter(Context context, ArrayList<WiFiData> data) {
        mContext = context;
        this.sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public WiFiData getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        View view = mLayoutInflater.inflate(R.layout.wifilistview, null);

        TextView SSID = (TextView)view.findViewById(R.id.SSID);
        TextView BSSID = (TextView)view.findViewById(R.id.BSSID);
        TextView RSSI = (TextView)view.findViewById(R.id.rssi);
        TextView Frequency = (TextView)view.findViewById(R.id.frequency);
        TextView Distance = (TextView)view.findViewById(R.id.distance);

        SSID.setText(sample.get(position).getSSID());
        BSSID.setText(sample.get(position).getBSSID());
        RSSI.setText(Integer.toString(sample.get(position).getRssi()));
        Frequency.setText(Double.toString(sample.get(position).getFrequency()));
        Distance.setText(sample.get(position).getDistance());

        return view;
    }

}
