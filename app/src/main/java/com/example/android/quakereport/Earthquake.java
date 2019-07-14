package com.example.android.quakereport;

public class Earthquake {
    private double mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private int mTime;

    public Earthquake(double magnitude, String location, long TimeInMilliseconds){
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = TimeInMilliseconds;
        mTime = 0;
    }

    public double getMagnitude(){
        return mMagnitude;
    }

    public String getLocation(){
        return mLocation;
    }

    public long getTimeInMilliseconds(){
        return mTimeInMilliseconds;
    }

    public int getTime(){
        return mTime;
    }
}
