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

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
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

                //해당하는 것의 모든 location data가져옴

                String url_location = "http://stop-bus.tk/user/busLocationList";
                String getPurpose = "busLocation";

                JSONObject sendData = new JSONObject();
                try {
                    sendData.put("plateNo" , SettedBus.getBusInfo().getCarNumber());
                    sendData.put("routeID" , SettedBus.getBusInfo().getVehicleNumber());
                    Log.d("sending", sendData.getString("plateNo"));
                    Log.d("sending", sendData.getString("routeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DriverActivity.ConnectThread thread_location = new DriverActivity.ConnectThread(url_location, sendData, getPurpose);
                thread_location.start();

                try {
                    thread_location.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

               //버스의 노선번호와 station 번호를 전달하여, 해당 정류장으로부터 남은 시간 추출
                String url_time = "http://stop-bus.tk/driver/gap";
                String getTime = "remainTime";

                JSONObject sendStation = new JSONObject();
                try {
                    sendStation.put("routeID" , SettedBus.getBusInfo().getVehicleNumber());
                    sendStation.put("stationID", SettedBus.getBusInfo().getPath().get( SettedBus.getFrontBus_place()).getStationID());

                    Log.d("sending", sendStation.getString("routeID"));
                    Log.d("sending", sendStation.getString("stationID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                DriverActivity.ConnectThread thread_Time = new DriverActivity.ConnectThread(url_time, sendStation, getTime);
                thread_Time.start();

                try{
                    thread_Time.join();
                } catch (InterruptedException e){
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
        String purpose;

        public ConnectThread(String inStr, JSONObject map, String purpose){
            urlStr = inStr;
            this.s_Data = map;
            this.purpose = purpose;
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

                if(purpose.equals("busLocation"))
                    parseBusLocation(output);
                else if(purpose.equals("remainTime"))
                    parseTimeInformation(output);
                else
                    System.out.println("this is for out!! there is no version suits");
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        private void parseBusLocation(String pRecvServerPage) {

            Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

            try {
                JSONObject jsonObject = new JSONObject(pRecvServerPage);

                JSONObject jHeader = jsonObject.getJSONObject("header");  // JSONObject 추출
                Log.d("PARSING", jHeader.getString("result"));

                if(jHeader.getString("result").compareTo("true") == 0) {

                    JSONArray testarray = jsonObject.getJSONArray("body");

                    if (jHeader.getString("result").compareTo("true") != 0)
                        System.out.println("errrr!!1");

                    int findresult = SettedBus.findCurrentBus(testarray);
                    if (findresult == -1) {
                        System.out.print("null");
                    } else {
                        System.out.print("the result is : ".concat(String.valueOf(findresult)));
                        this.setBusState(findresult);
                    }
                }else{
                    Log.d("fail to find","in Driver Activity AT BUSLOCATION");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseTimeInformation(String pRecvServerPage){
            Log.i("서버에서 받은 내용(for time) : ", pRecvServerPage);

            try {
                JSONObject jsonObject = new JSONObject(pRecvServerPage);

                JSONObject jHeader = jsonObject.getJSONObject("header");  // JSONObject 추출
                Log.d("PARSING", jHeader.getString("result"));

                if(jHeader.getString("result").compareTo("true") == 0) {

                    JSONObject JBody = jsonObject.getJSONObject("body");
                    int predictTime1 = Integer.parseInt(JBody.getString("predictTime1"));
                    int predictTime2 = Integer.parseInt(JBody.getString("predictTime2"));

                    Log.d("FirstBus", JBody.getString("plateNo1").concat(String.valueOf(predictTime1)));
                    Log.d("SecondBus", JBody.getString("plateNo2").concat(String.valueOf(predictTime2 - predictTime1)));

                    TextView FrontBus_Time = (TextView) findViewById(R.id.FrontBus_Time);
                    FrontBus_Time.setText(JBody.getString("predictTime1").concat(" 분"));

                    TextView BackBus_Time = (TextView) findViewById(R.id.BackBus_Time);
                    BackBus_Time.setText(String.valueOf(predictTime2 - predictTime1).concat(" 분"));
                }else{
                    Log.d("FAIL TO GET INFO", "TIME INFORMATION");
                }

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

        public void setBusState(int currentStation){
            //상단에 출력할 버스 번호 (ex 720-2번)
            TextView BusNum = (TextView)findViewById( R.id.BusNum );
            BusNum.setText( SettedBus.getBusInfo().getBusNumber().concat("번"));

            //앞 버스 위치 출력
            TextView FrontBus_Text = (TextView)findViewById(R.id.FrontBus_Text);
            int FrontBus_Seq = SettedBus.getLocationList().get(currentStation+1).getStationSeq();
            FrontBus_Text.setText(SettedBus.getBusInfo().getPath().get(FrontBus_Seq).getStationName());

            //현재 버스 위치 출력
            TextView CurrentStation = (TextView)findViewById(R.id.CurrentStation);
            int Current_Seq = SettedBus.getLocationList().get(currentStation).getStationSeq();
            CurrentStation.setText(SettedBus.getBusInfo().getPath().get(Current_Seq).getStationName());

            //뒷 버스 위치 출력
            TextView BackBus_Text = (TextView)findViewById(R.id.BackBus_Text);
            int BackBus_Seq = SettedBus.getLocationList().get(currentStation-1).getStationSeq();
            BackBus_Text.setText(SettedBus.getBusInfo().getPath().get(BackBus_Seq).getStationName());

            //현재 버스 다음 위치 출력
            TextView NextStation = (TextView)findViewById(R.id.NextStation);
            NextStation.setText(SettedBus.getBusInfo().getPath().get(Current_Seq+1).getStationName());

            //현재 버스 다음 위치 출력(밑 부분)
            TextView NextStation_Buttom = (TextView)findViewById(R.id.NextStation_Buttom);
            NextStation_Buttom.setText(SettedBus.getBusInfo().getPath().get(Current_Seq+1).getStationName());

            //현재 버스 방향 출력
            TextView NextStationDirection = (TextView)findViewById(R.id.NextStationDirection);
            NextStationDirection.setText(SettedBus.getBusInfo().getPath().get(Current_Seq+2).getStationName());
        }
    }
}
