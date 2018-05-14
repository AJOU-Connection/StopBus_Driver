package com.example.kimheeyeon.testapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Bus implements Serializable {
    //Bus가 가지고 있어야 하는 것. 현재 위치, bus number, 차량 넘버

    private String current_place;
    private BusInfo busInfo;

    public Bus(){
    }

    public Bus(String BusNum) {
        this.busInfo = new BusInfo(BusNum);
    }

    public String getCurrent_place() {
        return current_place;
    }

    public void setCurrent_place(String current_place) {
        this.current_place = current_place;
    }

    public BusInfo getBusInfo() {
        return busInfo;
    }

    public void setBusInfo(JSONObject getInfo) {
        BusInfo testinfo = new BusInfo();
        try {
            testinfo.setBusNumber(getInfo.getString("busNumber"));
            Log.d("tag", getInfo.getString("stationList"));
            JSONArray sList = new JSONArray(getInfo.getString("stationList"));

            for(int i = 0; i < sList.length(); i++){
                JSONObject binfo = sList.getJSONObject(i);
                testinfo.putPath(binfo);
            }

            this.setBusInfo(testinfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setBusInfo(BusInfo businfo){
        this.busInfo = businfo;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.busInfo.setVehicleNumber(vehicleNumber);
    }

    public void setCarNumber(String carNumber) {
        this.busInfo.setCarNumber(carNumber);
    }

    public String findCurrentBus(JSONObject jInfo){
        JSONArray BusList = null;
        try {
            BusList = new JSONArray(jInfo);
            for(int i = 0; i < BusList.length(); i++){
                JSONObject binfo = BusList.getJSONObject(i);
                if(binfo.getString("plateNo").compareTo(this.busInfo.getCarNumber()) == 0) {
                    String currentSeq = (binfo.getString("stationSeq"));
                    System.out.print(currentSeq);
                    this.setCurrent_place(this.busInfo.getPathName_by_stationSeq(Integer.parseInt(currentSeq)));
                    //this.setCurrent_place();
                    return currentSeq;
                }
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
