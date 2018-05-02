package com.example.kimheeyeon.testapplication;

public class Path {


    private String stationName;
    private int stationNumber;

    public Path(String S_Name, int S_Number){

        setStationName(S_Name);
        setStationNumber(S_Number);

    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(int stationNumber) {
        this.stationNumber = stationNumber;
    }

}
