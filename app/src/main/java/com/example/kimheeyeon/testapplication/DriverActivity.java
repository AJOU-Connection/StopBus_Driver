package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverActivity extends Activity{
    Handler handler = new Handler();
    Bus SettedBus = new Bus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final Intent intent = getIntent();

        //intent 로부터 받은 값 정리
//        String busID =  intent.getStringExtra("BUS_ID");
//        String car_num =  intent.getStringExtra("CAR_NUMBER");
        SettedBus = (Bus)intent.getSerializableExtra("busData");
        Log.d("check info", SettedBus.getBusInfo().getCarNumber());
        Log.d("check info", SettedBus.getBusInfo().getVehicleNumber());


        //상단에 출력할 버스 번호 (ex 720-2번)
        TextView BusNum = (TextView)findViewById( R.id.BusNum );
        BusNum.setText( SettedBus.getBusInfo().getBusNumber().concat("번"));

        //앞 버스 위치 출력
        TextView FrontBus_Text = (TextView)findViewById(R.id.FrontBus_Text);
        FrontBus_Text.setText(SettedBus.getBusInfo().getPath().get(2).getStationName());

        //현재 버스 위치 출력
        TextView CurrentStation = (TextView)findViewById(R.id.CurrentStation);
        CurrentStation.setText(SettedBus.getBusInfo().getPath().get(1).getStationName());

        //뒷 버스 위치 출력
        TextView BackBus_Text = (TextView)findViewById(R.id.BackBus_Text);
        BackBus_Text.setText(SettedBus.getBusInfo().getPath().get(0).getStationName());

        //현재 버스 다음 위치 출력
        TextView NextStation = (TextView)findViewById(R.id.NextStation);
        NextStation.setText(SettedBus.getBusInfo().getPath().get(2).getStationName());

        //현재 버스 다음 위치 출력(밑 부분)
        TextView NextStation_Buttom = (TextView)findViewById(R.id.NextStation_Buttom);
        NextStation_Buttom.setText(SettedBus.getBusInfo().getPath().get(2).getStationName());

        //현재 버스 방향 출력
        TextView NextStationDirection = (TextView)findViewById(R.id.NextStationDirection);
        NextStationDirection.setText(SettedBus.getBusInfo().getPath().get(3).getStationName().concat(" 방향"));

        //반복실행할 것.
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                System.out.println("Hello !!");

                String url = "http://stop-bus.tk/driver/register";

                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("plateNo" , SettedBus.getBusInfo().getCarNumber());
                    sendData.put("routeID" , SettedBus.getBusInfo().getVehicleNumber());
                    Log.d("sending", sendData.getString("plateNo"));
                    Log.d("sending", sendData.getString("routeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DriverActivity.ConnectThread thread = new DriverActivity.ConnectThread(url, sendData);
                thread.start();

                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
    }

    class ConnectThread extends Thread {
        String urlStr;
        JSONObject s_Data;

        public ConnectThread(String inStr, JSONObject map){
            urlStr = inStr;
            this.s_Data = map;
        }

        public void run(){

            try{
                final String output = request(urlStr, this.s_Data);
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        Log.d("gmmm", output);
                    }
                });

                jsonParserList(output);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        private void jsonParserList(String pRecvServerPage) {

            Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

            try {
                JSONObject jsonObject = new JSONObject(pRecvServerPage);

                JSONObject jHeader = jsonObject.getJSONObject("header");  // JSONObject 추출
                Log.d("PARSING", jHeader.getString("result"));

                JSONObject jBody = jsonObject.getJSONObject("body");  // JSONObject 추출
                //Log.d("PARSING", jBody.getString("frontBus"));

                setBusInfo(jBody);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String request(String urlStr, JSONObject map){
            StringBuilder output = new StringBuilder();

            try{
                URL url = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    if (map != null) { // 웹 서버로 보낼 매개변수가 있는 경우우
                        OutputStream os = conn.getOutputStream(); // 서버로 보내기 위한 출력 스트림
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); // UTF-8로 전송
                        bw.write(map.toString()); // 매개변수 전송
                        bw.flush();
                        bw.close();
                        os.close();
                    }
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림
                        while ((line = br.readLine()) != null) // 서버의 응답을 읽어옴
                            output.append(line);
                    }
                }
            }catch(Exception ex){
                Log.e("SampleHttp", "Exception in processing response",ex);

            }
            return output.toString();
        }

        private void setBusInfo(JSONObject Jinfo){

        }
    }
}
