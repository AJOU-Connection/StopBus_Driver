package com.example.kimheeyeon.testapplication;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BusInfo {
    //for the information that not changed

    private Path[] paths;
    private String BusNumber;
    private int VehicleNumber;

    public BusInfo(String BusNum) {
        setBusNumber(BusNum);

        //this.getBusPath(BusNumber);
    }

    public String getBusNumber() {
        return BusNumber;
    }

    public Path[] getPath() {
        return paths;
    }

    public void setPath(ArrayList<String> path) {
        for(int i = 0; i < path.size() ; i++){
            //this.paths[i] = path.get(i);
        }
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
