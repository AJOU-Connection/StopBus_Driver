package com.example.kimheeyeon.testapplication;

public class BusInfo {
    //for the information that not changed

    private String path;
    private String BusNumber;
    private int VehicleNumber;

    public BusInfo(String BusNum) {
        setBusNumber(BusNum);

        //this.getBusPath(BusNumber);
    }

    public String getBusNumber() {
        return BusNumber;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setBusNumber(String busNumber) {
        this.BusNumber = busNumber;
    }

    public int getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(int vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

}
