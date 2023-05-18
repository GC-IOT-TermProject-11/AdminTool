package com.example.admintool;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WiFiData {
    private String ssid, bssid, distance;
    private double frequency;
    private int rssi;

    public WiFiData(String ssid, String bssid, int rssi, double frequency, String distance)
    {
        this.ssid = ssid;
        this.bssid = bssid;
        this.rssi = rssi;
        this.frequency = frequency;
        this.distance = distance;
    }

    public String getSSID()
    {
        return this.ssid;
    }

    public String getBSSID()
    {
        return this.bssid;
    }

    public int getRssi()
    {
        return this.rssi;
    }

    public double getFrequency(){return this.frequency;}

    public String getDistance(){return this.distance;}

}
