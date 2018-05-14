package com.example.kimheeyeon.testapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class BusInfo implements Serializable {
    //for the information that not changed

    private ArrayList<Path> paths = new ArrayList<>();
    private String BusNumber = "";
    private String VehicleNumber = "";
    private String CarNumber = "";

    public BusInfo(){

    }

    public BusInfo(String BusNum) {
        setBusNumber(BusNum);
    }

    public String getBusNumber() {
        return BusNumber;
    }

    public ArrayList<Path> getPath() {
        return paths;
    }

    public void setPath(ArrayList<String> path) {
        for(int i = 0; i < path.size() ; i++){
            //this.paths[i] = path.get(i);
        }
    }

    public void setPaths(JSONObject stationList){

    }

    public String getPathName_by_stationSeq(int stationSeq){
        return this.paths.get(stationSeq-1).getStationName();
    }

    public void putPath(JSONObject stationInfo){
        try {
            Path nPath = new Path(stationInfo.getString("stationName"), stationInfo.getString("stationNumber"),stationInfo.getString("stationSeq"));
            this.paths.add(this.paths.size() , nPath);

            Log.d("path", this.paths.get(this.paths.size()-1).getStationName());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setBusNumber(String busNumber) {
        this.BusNumber = busNumber;
        Log.d("tag", this.BusNumber);
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.VehicleNumber = vehicleNumber;
    }

    public String getCarNumber() {
        return CarNumber;
    }

    public void setCarNumber(String carNumber) {
        CarNumber = carNumber;
    }

}
