package com.example.android.quakereport;

public class Earthquake {
    private String mMagnitude;
    private String mLocation;
    private long mTimeInMilliseconds;
    private int mTime;

    public Earthquake(String magnitude, String location, long TimeInMilliseconds){
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = TimeInMilliseconds;
        mTime = 0;
    }

    public String getMagnitude(){
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
