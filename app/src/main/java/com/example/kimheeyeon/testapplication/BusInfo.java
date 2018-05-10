package com.example.kimheeyeon.testapplication;

import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BusInfo {
    //for the information that not changed

    private Path[] paths;
    private String BusNumber = "";
    private int VehicleNumber = 0;

    public BusInfo(){

    }

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

    public void setPaths(JSONObject stationList){

    }

    public void setBusNumber(String busNumber) {
        this.BusNumber = busNumber;
        Log.d("tag", this.BusNumber);
    }

    public int getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(int vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

}
