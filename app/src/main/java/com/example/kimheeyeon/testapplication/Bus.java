package com.example.kimheeyeon.testapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Bus  implements Serializable {
    //Bus가 가지고 있어야 하는 것. 현재 위치, bus number, 차량 넘버

    private String current_place;
    private BusInfo BusInfo;

    public Bus(){}

    public Bus(String BusNum) {
        //text = "HaHaHaHaHa";

        this.BusInfo = new BusInfo(BusNum);

    }

    public String getCurrent_place() {
        return current_place;
    }

    public void setCurrent_place(String current_place) {
        this.current_place = current_place;
    }

    public BusInfo getBusInfo() {
        return BusInfo;
    }

    public void setBusInfo(JSONObject getInfo) {
        BusInfo testinfo = new BusInfo();
        try {
            //Log.d("tag", getInfo.toString(6));
            //Log.d("tag", getInfo.getString("busNumber"));
            testinfo.setBusNumber(getInfo.getString("busNumber"));
            testinfo.setPaths(getInfo.getJSONObject("stationList"));
            //Log.d("businfo", BusInfo.getBusNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
