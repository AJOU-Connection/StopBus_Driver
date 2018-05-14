package com.example.kimheeyeon.testapplication;

import java.io.Serializable;

public class Path implements Serializable {


    private String stationName;
    private String stationNumber;
    private int stationSequence;

    public Path(String S_Name, String S_Number, String S_Seq){

        setStationName(S_Name);
        setStationNumber(S_Number);
        setStationSequence(Integer.parseInt(S_Seq));

    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(String stationNumber) {
        this.stationNumber = stationNumber;
    }

    public int getStationSequence() {
        return stationSequence;
    }

    public void setStationSequence(int stationSequence) {
        this.stationSequence = stationSequence;
    }

}
