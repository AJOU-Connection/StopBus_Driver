package com.example.kimheeyeon.testapplication;

public class BusLocation {
    private String PlateNo;
    private int stationSeq;

    public String getPlateNo() {
        return PlateNo;
    }

    public void setPlateNo(String plateNo) {
        PlateNo = plateNo;
    }

    public int getStationSeq() {
        return stationSeq;
    }

    public void setStationSeq(int stationSeq) {
        this.stationSeq = stationSeq;
    }

    public BusLocation(String PlateNo, int StationSeq){
        setPlateNo(PlateNo);
        setStationSeq(StationSeq);
    }
}
