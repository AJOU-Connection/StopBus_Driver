package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.ProgressBar;

import android.content.Intent;
import android.os.Handler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends Activity {
    Handler handler = new Handler();
    Bus settedBus = new Bus();
    String busID = "";
    String carNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_page);

        final ProgressBar P_Bar= (ProgressBar)findViewById(R.id.progressBar);
        P_Bar.setVisibility(View.INVISIBLE); //To set visible

        Button search = (Button) findViewById(R.id.Confirm_Button);
        search.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        TextView bus_id = (TextView)findViewById( R.id.bus_ID );
                        busID =  bus_id.getText().toString();

                        TextView car_n = (TextView)findViewById( R.id.Car_Number );
                        carNumber =  car_n.getText().toString();

                        P_Bar.setVisibility(View.VISIBLE);

                        String url = "http://stop-bus.tk/driver/register";

                        JSONObject sendData = new JSONObject();
                        try {
                            sendData.put("plateNo" , carNumber);
                            sendData.put("routeID" , busID);
                            Log.d("sending", sendData.getString("plateNo"));
                            Log.d("sending", sendData.getString("routeID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ConnectThread thread = new ConnectThread(url, sendData);
                        thread.start();

                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    Intent intent = new Intent(MainActivity.this, DriverActivity.class);
                                    //intent.putExtra("BUS_ID", busID); //키 - 보낼 값(밸류)
                                    //intent.putExtra("CAR_NUMBER", carNumber);
                                    Log.d("ack data", settedBus.getBusInfo().getBusNumber());
                                    intent.putExtra("busData", settedBus);

                                    startActivity(intent);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, 2000);  // 2000은 2초를 의미합니다.
                    }
                }
        );
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
                Log.d("PARSING", jBody.getString("busNumber"));

                settedBus.setBusInfo(jBody);
                settedBus.setVehicleNumber(busID);
                settedBus.setCarNumber(carNumber);

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
    }

}