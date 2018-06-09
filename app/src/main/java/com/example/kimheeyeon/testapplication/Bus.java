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

    //private int old_current;
    //private boolean isChanged; // check if current_place is changed

    private int leftTime1;
    private int leftTime2;

    private int leftTime_Current;

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

    public int getLeftTime1() {
        return leftTime1;
    }

    public void setLeftTime1(int leftTime1) {
        this.leftTime1 = leftTime1;
    }

    public int getLeftTime2() {
        return leftTime2;
    }

    public void setLeftTime2(int leftTime2) {
        this.leftTime2 = leftTime2;
    }

    public int getLeftTime_Current() {
        return leftTime_Current;
    }

    public void setLeftTime_Current(int leftTime_Current) {
        this.leftTime_Current = leftTime_Current;
    }

//    public boolean getisChanged() {
//        return isChanged;
//    }
//
//    public void setChanged(boolean changed) {
//        isChanged = changed;
//    }

    public int findCurrentBus(JSONArray jInfo){
        JSONArray BusList = null;
        //강제로 null시켜서 비우기
        locationList.clear();

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

            System.out.println("---after---");
            int finalResult = -1;

            for ( int i = 0 ; i < locationList.size(); i++){
                Log.d("after", locationList.get(i).getPlateNo().concat(" and ").concat(String.valueOf(locationList.get(i).getStationSeq())));

                if(locationList.get(i).getPlateNo().compareTo(this.busInfo.getCarNumber()) == 0) {

                    int currentSeq = (locationList.get(i).getStationSeq() - 1);
                    finalResult = i;
//                    if(getCurrent_place()!= currentSeq) {
//                        isChanged = true;
//                    }else{
//                        isChanged = false;
//                    }
                    this.setCurrent_place(currentSeq);

                    Log.d("check!", locationList.get(i).getPlateNo().concat(" and ").concat(String.valueOf(locationList.get(i).getStationSeq())));

                    try{
                        Log.d("Frontbus!", locationList.get(i+1).getPlateNo().concat(" and ").concat(String.valueOf(locationList.get(i+1).getStationSeq())));
                        this.setFrontBus_place(locationList.get(i+1).getStationSeq() - 1);
                    }catch(Exception e){
                        this.setFrontBus_place(-1);
                    }

                    try{
                        Log.d("BackBus!", locationList.get(i-1).getPlateNo().concat(" and ").concat(String.valueOf(locationList.get(i-1).getStationSeq())));
                        this.setBackBus_place(locationList.get(i-1).getStationSeq() - 1);
                    }catch(Exception e){
                        this.setBackBus_place(-1);
                    }
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
