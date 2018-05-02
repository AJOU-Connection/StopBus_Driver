package com.example.kimheeyeon.testapplication;

import java.util.Date;

public class Bus {
    //Bus가 가지고 있어야 하는 것. 현재 위치, bus number, 차량 넘버

    private String current_place;
    private BusInfo BusInfo;


    public Bus(String BusNum) {
        //text = "HaHaHaHaHa";

        this.BusInfo = new BusInfo(BusNum);

    }

    public String getBusNumber() {
        return BusInfo.getBusNumber();
    }

    public void getBusPath(String BusNum){ }

    public void getBusInfo(){

    }


}
