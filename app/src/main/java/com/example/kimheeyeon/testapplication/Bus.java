package com.example.kimheeyeon.testapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * current_place : 지내간 버스정류장
 */
public class Bus implements Serializable {
    //Bus가 가지고 있어야 하는 것. 현재 위치, bus number, 차량 넘버

    private int frontBus_place;
    private int current_place;
    private int backBus_place;
    private BusInfo busInfo;
    private ArrayList<BusLocation> locationList = new ArrayList<BusLocation>();

    public Bus(){

    }

    public Bus(String BusNum) {
        this.busInfo = new BusInfo(BusNum);
    }

    public int getCurrent_place() {
        return current_place;
    }

    public void setCurrent_place(int current_place) {
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

    public ArrayList<BusLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(ArrayList<BusLocation> locationList) {
        this.locationList = locationList;
    }

    public int getFrontBus_place() {
        return frontBus_place;
    }

    public void setFrontBus_place(int frontBus_place) {
        this.frontBus_place = frontBus_place;
    }

    public int getBackBus_place() {
        return backBus_place;
    }

    public void setBackBus_place(int backBus_place) {
        this.backBus_place = backBus_place;
    }

    public int findCurrentBus(JSONArray jInfo){
        JSONArray BusList = null;
        //강제로 null시켜서 비우기
        locationList.clear();

        Log.i("isin?", "true");
        try {
            BusList = jInfo;
            Log.d("jinfo", String.valueOf(BusList.length()));
            for(int i = 0; i < BusList.length(); i++){
                JSONObject binfo = BusList.getJSONObject(i);
                Log.d("Compare", binfo.getString("plateNo").concat(" and ").concat(binfo.getString("stationSeq")));
                BusLocation nLocation = new BusLocation(binfo.getString("plateNo"), Integer.parseInt(binfo.getString("stationSeq")));
                this.locationList.add(this.locationList.size() , nLocation);
            }

            MiniComparator comp = new MiniComparator();
            Collections.sort(locationList, comp);

            System.out.println("--after--");
            int finalResult = -1;

            for ( int i = 0 ; i < locationList.size(); i++){
                Log.d("after", locationList.get(i).getPlateNo().concat(" and ").concat(String.valueOf(locationList.get(i).getStationSeq())));

                if(locationList.get(i).getPlateNo().compareTo("경기77바1957") == 0) {

                    int currentSeq = (locationList.get(i).getStationSeq());
                    finalResult =i;
                    this.setCurrent_place(currentSeq);
                    //this.setCurrent_place();
                    Log.d("check!", locationList.get(i).getPlateNo().concat(" and ").concat(String.valueOf(locationList.get(i).getStationSeq())));
                    Log.d("beforebus!", locationList.get(i+1).getPlateNo().concat(" and ").concat(String.valueOf(locationList.get(i+1).getStationSeq())));
                    Log.d("afterBus!", locationList.get(i-1).getPlateNo().concat(" and ").concat(String.valueOf(locationList.get(i-1).getStationSeq())));

                    setFrontBus_place(locationList.get(i+1).getStationSeq());
                    setBackBus_place(locationList.get(i-1).getStationSeq());
                }
            }

            return finalResult;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    class MiniComparator implements Comparator<BusLocation> {
        @Override
        public int compare(BusLocation first, BusLocation second){
            int firstValue = first.getStationSeq();
            int secondValue = second.getStationSeq();

            if(firstValue < secondValue) {
                return -1;
            }else if(firstValue > secondValue){
                return 1;
            }else{
                return 0;
            }
        }

    }
}
