package com.example.kimheeyeon.testapplication;

import java.util.Date;

public class Bus {
    //Bus가 가지고 있어야 하는 것. 현재 위치, 앞뒤버스와의 차

    private Date intervalTime;
    private String current_place;
    private String path;
    private String BusNumber;

    public String getBusNumber() {
        return BusNumber;
    }


    public void Bus(String BusNum) {
        //text = "HaHaHaHaHa";
        BusNumber = BusNum;

        this.getBusPath(BusNumber);
    }

    public void getBusPath(String BusNum){};


}
