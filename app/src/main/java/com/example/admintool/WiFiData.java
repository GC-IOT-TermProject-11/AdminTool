package com.example.admintool;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WiFiData {
    private String ssid, bssid;
    private long timeStamp;
    private int rssi;

    public WiFiData(String ssid, String bssid, long timeStamp, int rssi)
    {
        this.ssid = ssid;
        this.bssid = bssid;
        this.timeStamp = timeStamp;
        this.rssi = rssi;
    }

    public String getSSID()
    {
        return this.ssid;
    }

    public String getBSSID()
    {
        return this.bssid;
    }

    public String getTimeStamp()
    {
        Date d = new Date(timeStamp);
        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(d);
        return time;
    }

    public int getRssi()
    {
        return this.rssi;
    }

}
