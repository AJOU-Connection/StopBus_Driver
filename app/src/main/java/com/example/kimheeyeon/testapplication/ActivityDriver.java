package com.example.kimheeyeon.testapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//bluetooth
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ActivityDriver extends Activity {
    Handler handler = new Handler();
    Bus SettedBus = new Bus();

    String isRidingPerson = "다음 정류장에 탑승객이 있습니다";
    String isGetOffPerson = "다음 정류장에 하차객이 있습니다";
    String isBothOnOffPerson = "다음 정류장에 승하차객이 있습니다";
    String noGetInOff     = "없음";

    private static String address = "30:14:09:30:15:33";

    //for tts class
    private TextToSound ts;
    //String tts_text = null;

    private SharedPreferences getoff_share;

    private int TextProperty = -1;


    //받은데이터
    public void initData(){
        getoff_share = getSharedPreferences("getoff_share", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = getoff_share.edit();
        editor.putString("isgetoff", "");
        editor.apply();
    }
    public void saveData(String Data){
        getoff_share = getSharedPreferences("getoff_share", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = getoff_share.edit();
        editor.putString("isgetoff", Data);
        editor.apply();
    }
    private String loadScore() {
        SharedPreferences pref = getSharedPreferences("getoff_share", Activity.MODE_PRIVATE);
        return pref.getString("isgetoff", "");
    }

    //보낸 데이터
    public void initData_send(){
        getoff_share = getSharedPreferences("getoff_share", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = getoff_share.edit();
        editor.putString("getoff_s", "i");
        editor.apply();
    }
    public void saveData_send(String Data){
        getoff_share = getSharedPreferences("getoff_share", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = getoff_share.edit();
        editor.putString("getoff_s", Data);
        editor.apply();
    }
    private String loadScore_send() {
        SharedPreferences pref = getSharedPreferences("getoff_share", Activity.MODE_PRIVATE);
        return pref.getString("getoff_s", null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final Intent intent = getIntent();

        //intent 로부터 받은 값 정리
        SettedBus = (Bus)intent.getSerializableExtra("busData");

        initData();
        initData_send();

        //service
        Intent sb_intent = new Intent(
                getApplicationContext(),//현재제어권자
                BluetoothCommunication.class); // 이동할 컴포넌트
        startService(sb_intent); // 서비스 시작

        //init tts
        ts = new TextToSound(this);

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

        //색상 초기화
        TextView RidePerson = (TextView)findViewById(R.id.RidePerson);
        RidePerson.setBackgroundColor(Color.rgb(246, 235, 235));

        TextView GetOffPerson = (TextView)findViewById(R.id.GetOffPerson);
        GetOffPerson.setBackgroundColor(Color.rgb(241, 238, 223));

        //반복실행할 것.
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here

                //ts초기화
                ts.setText(noGetInOff);


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

                ActivityDriver.ConnectThread thread_location = new ActivityDriver.ConnectThread(url_location, sendData, getPurpose);
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
                    Thread.sleep(300);
                    sendStation.put("stationID", SettedBus.getBusInfo().getPath().get( SettedBus.getFrontBus_place()).getStationID());

                    Log.d("sending", sendStation.getString("routeID"));
                    Log.d("sending", sendStation.getString("stationID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                ActivityDriver.ConnectThread thread_Time = new ActivityDriver.ConnectThread(url_time, sendStation, getTime);
                thread_Time.start();

                try{
                    thread_Time.join();
                    System.out.println("time_thread_is_joined");
                } catch (InterruptedException e){
                    e.printStackTrace();
                    System.out.println("time_thread_is_not_joined");
                }



                //버스의 노선번호와 station 번호를 전달하여, 도착할 정류장으로부터 남은 시간 추출
                String getGap = "remainGap";

                JSONObject sendStationCurrent = new JSONObject();
                try {
                    sendStationCurrent.put("routeID" , SettedBus.getBusInfo().getVehicleNumber());
                    sendStationCurrent.put("stationID", SettedBus.getBusInfo().getPath().get( SettedBus.getCurrent_place() + 1).getStationID());

                    Log.d("sending roudID", sendStationCurrent.getString("routeID"));
                    Log.d("remain currentbus gap",  String.valueOf(SettedBus.getBusInfo().getPath().get( SettedBus.getCurrent_place() + 1).getStationName()));
                    Log.d("sending stationID", sendStationCurrent.getString("stationID"));

                    //ts.speak(String.valueOf(SettedBus.getBusInfo().getPath().get( SettedBus.getCurrent_place()).getStationName()), true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                ActivityDriver.ConnectThread thread_Gap = new ActivityDriver.ConnectThread(url_time, sendStationCurrent, getGap);
                thread_Gap.start();

                try{
                    System.out.println("GAP_is_joined1");
                    thread_Gap.join();

                    System.out.println("GAP_is_joined");

                } catch (InterruptedException e){
                    e.printStackTrace();
                }

                System.out.println("left time is ".concat(String.valueOf(SettedBus.getLeftTime_Current())));
                if(SettedBus.getLeftTime_Current() < 6) {
                    //승객의 여부 확인
                    String url_Passenger = "http://stop-bus.tk/driver/stop";
                    String getPassenger = "passengerInfo";

                    JSONObject sendPassenger = new JSONObject();
                    try {
                        Thread.sleep(100);
                        sendPassenger.put("routeID", SettedBus.getBusInfo().getVehicleNumber());
                        sendPassenger.put("stationID", SettedBus.getBusInfo().getPath().get(SettedBus.getCurrent_place() + 1).getStationID());

                        Log.d("sendingP", sendPassenger.getString("routeID"));
                        Log.d("sendingP", sendPassenger.getString("stationID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ActivityDriver.ConnectThread thread_Passenger = new ActivityDriver.ConnectThread(url_Passenger, sendPassenger, getPassenger);
                    thread_Passenger.start();

                    try {
                        thread_Passenger.join();

                        Thread.sleep(100);

                        if(ts.getText()!= null) {
                            Log.i("tts_text", ts.getText());

                            if(ts.getText().compareTo("가져오기 에러") != 0 && ts.getText().compareTo(noGetInOff) != 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ts.ttsGreater21();
                                } else {
                                    ts.ttsUnder20();
                                }
                            }

                        }


                        System.out.println("I think stoppppppppp!" + ts.getText());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0,    20, TimeUnit.SECONDS);
    }

    private int setTTS(boolean isGetIn, boolean isGetOff, int version){
        if(ts.getText() != null){
            ts.setText(null);
        }

        String b_getOff = loadScore();
        Log.d("FROMBL", b_getOff);

        if(version == 0 )//true
        {

            Log.d("checkGetIn", String.valueOf(isGetIn));
            if (isGetIn) {
                //탑승객이 있는 경우

                ts.setText(isRidingPerson);

                //saveData_send("o");
                Log.d("isGetIn", loadScore());

                System.out.print("tttttttttthe1");

                //tts_text = "다음 정류장에 탑승객이 있습니다";
            }

            if (isGetOff || b_getOff.compareTo("o")==0 ) {
                //하차객이 있는 경우

                saveData_send("o");
                Log.d("isGetOFF|\"o\"", loadScore());

                if (ts.getText() != null) {
                    System.out.print("tttttttttthe2");
                    ts.setText(isBothOnOffPerson);
                    return 3;
                } else {
                    ts.setText(isGetOffPerson);
                    System.out.print("tttttttttthe3");
                    return 2;
                }

            }else{
                //하차객이 아에 없는 경우
                //tts_text = "다음 정류장에는 승하차객이 없습니다";

                if (ts.getText() == null) {
                    System.out.print("tttttttttthe4");
                    ts.setText(noGetInOff);
                    Log.d("noGet when v0", loadScore());
                    return 4;
                }else{
                    return 1;
                }
                //ts.setText(noGetInOff);
            }

        }else if(version == 1){
            if (b_getOff.compareTo("o") ==0 ) {
                //하차객이 있는 경우

                Log.d("iminoooooo", "oooooooo");

                saveData_send("o");


                if (ts.getText() != null) {
                    ts.setText(isGetOffPerson);
                    System.out.print("tttttttttthe5");
                    //tts_text = "다음 정류장에 하차객이 있습니다";
                    Log.d("isGetOFF in v1", loadScore());
                    return 2;
                } else {
                    ts.setText(isGetOffPerson);
                    System.out.print("tttttttttthe6");
                    //tts_text = "다음 정류장에 탑승객과 하차객이 있습니다";

                    Log.d("isboth in v1", loadScore());
                    return 2;
                }

                //tts_text = "다음 정류장에 하차객이 있습니다";
            }
            else{
                ts.setText(noGetInOff);
                System.out.print("tttttttttthe7");

                saveData_send("x");

                Log.d("nogetinoff in v1", loadScore());

                return 4;
                //tts_text = "다음 정류장에는 승하차객이 없습니다";
            }
        }
        Log.d("so_text", ts.getText());

        return -1;
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
                final String output ;
                if(purpose == "remainTime")
                    output= request(urlStr, this.s_Data);
                else
                    output = request(urlStr, this.s_Data);
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        //Log.d("gmmm", output);

                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                switch(purpose){
                                    case "busLocation" :
                                        parseBusLocation(output);
                                        break;
                                    case "remainTime" :
                                        parseTimeInformation(output, "time");
                                        break;
                                    case "passengerInfo" :
                                        parsePassengerInformation(output);
                                        break;
                                    case "remainGap" :
                                        parseTimeInformation(output, "gap");
                                        break;
                                    default :
                                        System.out.println("this is for out!! there is no version suits");
                                }

                            }
                        });

                        Log.d(purpose, "is end!!!!!!!!!!");
                    }
                });
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
                    if(testarray==null){
                        System.out.print("nul,,?");
                        return;
                    }

                    if (jHeader.getString("result").compareTo("true") != 0)
                        System.out.println("errrr!!1");

                    int findresult = SettedBus.findCurrentBus(testarray);
                    if (findresult == -1) {
                        System.out.print("null");
                    } else {
                        System.out.print("the result is : ".concat(String.valueOf(findresult)));
                        if(SettedBus.getisChanged()){
                            saveData("x");
                            saveData_send("x");

                            Log.d("the resullTT in v1", loadScore_send());
                        }

                        setBusState();
                    }

                }else{
                    Log.d("fail to find","in Driver Activity AT BUSLOCATION");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseTimeInformation(String pRecvServerPage, String version){
            Log.i("서버에서 받은 내용(for ".concat(version).concat(" ) : "), pRecvServerPage);

            try {
                JSONObject jsonObject = new JSONObject(pRecvServerPage);

                JSONObject jHeader = jsonObject.getJSONObject("header");  // JSONObject 추출
                Log.d("PARSING", jHeader.getString("result"));

                if(jHeader.getString("result").compareTo("true") == 0) {

                    if(jsonObject.isNull("body")){
                        Log.d("FAIL TO GET INFO", "BODY IS NULL IN TIME INFORMATION");
                    }else {
                        JSONObject JBody = jsonObject.getJSONObject("body");

                        int predictTime1 = Integer.parseInt(JBody.getString("predictTime1"));
                        int predictTime2 = Integer.parseInt(JBody.getString("predictTime2"));

                        Log.d("FirstBus", JBody.getString("plateNo1").concat(" " + String.valueOf(predictTime1)));
                        Log.d("SecondBus", JBody.getString("plateNo2").concat(" " + String.valueOf(predictTime2 - predictTime1)));

                        if(version.compareTo("time") == 0) {

                            TextView FrontBus_Time = (TextView) findViewById(R.id.FrontBus_Time);
                            SettedBus.setLeftTime1(predictTime1);
                            FrontBus_Time.setText(ModifyString(3, String.valueOf(SettedBus.getLeftTime1())));

                            TextView BackBus_Time = (TextView) findViewById(R.id.BackBus_Time);
                            if(predictTime2 - predictTime1 <= 0)
                                SettedBus.setLeftTime2(0);
                            else
                                SettedBus.setLeftTime2(predictTime2 - predictTime1);
                            BackBus_Time.setText(ModifyString(3, String.valueOf(SettedBus.getLeftTime2())));

                        }else if(version.compareTo("gap") == 0){
                            String b_getOff = loadScore();
                            Log.d("FROMBL??", b_getOff);

                            SettedBus.setLeftTime_Current(predictTime1);
                            Log.d("CURRENTLEFT", String.valueOf(SettedBus.getLeftTime_Current()));
                        }else{
                            System.out.println("err!!!!!!!!!!!!!!!!");
                        }
                    }
                }else{
                    Log.d("FAIL TO GET INFO", "TIME INFORMATION");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parsePassengerInformation(String pRecvServerPage){
            Log.i("parsePassenger: ", pRecvServerPage);

            try {
                JSONObject jsonObject = new JSONObject(pRecvServerPage);

                JSONObject jHeader = jsonObject.getJSONObject("header");  // JSONObject 추출
                Log.d("PARSING", jHeader.getString("result"));

                if(jHeader.getString("result").compareTo("true") == 0) {

                    if(jsonObject.isNull("body")){
                        Log.d("FAIL TO GET INFO", "BODY IS NULL IN Passenger INFORMATION");
                    }else {
                        JSONObject JBody = jsonObject.getJSONObject("body");

                        Boolean isGetIn = Boolean.valueOf(JBody.getString("isGetIn"));
                        Boolean isGetOff = Boolean.valueOf(JBody.getString("isGetOff"));

                        Log.d("isGetIn",JBody.getString("isGetIn"));
                        Log.d("isGetOff", JBody.getString("isGetOff"));

                        TextProperty = setTTS(isGetIn, isGetOff, 0);

                        Log.d("tts_text_tts", ts.getText());

                        TextView RidePerson = (TextView)findViewById(R.id.RidePerson);
                        TextView GetOffPerson = (TextView)findViewById(R.id.GetOffPerson);

                        if(TextProperty == -1){
                            System.out.println("errrrrrrrrrrrrr!");
                        }
                        else if(TextProperty == 1){
                            System.out.println("errrrrrrrrrrrrr!1");
                            //getin
                            RidePerson.setBackgroundColor(Color.rgb(229, 78, 78));
                        }else if(TextProperty == 2){
                            System.out.println("errrrrrrrrrrrrr!2");
                            //getoff
                            GetOffPerson.setBackgroundColor(Color.rgb(239, 215, 95));
                        }else if(TextProperty == 3){
                            System.out.println("errrrrrrrrrrrrr!3");
                            //both
                            GetOffPerson.setBackgroundColor(Color.rgb(239, 215, 95));
                            RidePerson.setBackgroundColor(Color.rgb(229, 78, 78));
                        }else if(TextProperty == 4){
                            System.out.println("errrrrrrrrrrrrr!4");
                            //no
                            RidePerson.setBackgroundColor(Color.rgb(246, 235, 235));
                            GetOffPerson.setBackgroundColor(Color.rgb(241, 238, 223));
                        }


                    }
                }else{
                    Log.d("FAIL TO GET INFO", "Passenger INFORMATION");

                    //ts.setText("가져오기 에러");

                    TextProperty = setTTS(false, false, 1);

                    Log.d("tts_text_tts", ts.getText());



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

                        Log.i("보내는 내용 : ", map.toString());

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

        public void setBusState(){
            //상단에 출력할 버스 번호 (ex 720-2번)
            TextView BusNum = (TextView)findViewById( R.id.BusNum );
            BusNum.setText( SettedBus.getBusInfo().getBusNumber().concat("번"));

            //앞 버스 위치 출력
            TextView FrontBus_Text = (TextView)findViewById(R.id.FrontBus_Text);
            String FrontBus_String = "";
            try {
                FrontBus_String = SettedBus.getBusInfo().getPath().get(SettedBus.getFrontBus_place()).getStationName();
            }catch(Exception e){
                FrontBus_String = "정보없음";
            }
            FrontBus_Text.setText(ModifyString(1, FrontBus_String));

            //현재 버스 위치 출력
            TextView CurrentStation = (TextView)findViewById(R.id.CurrentStation);
            int Current_Seq = SettedBus.getCurrent_place();
            CurrentStation.setText(SettedBus.getBusInfo().getPath().get(Current_Seq).getStationName());

            //뒷 버스 위치 출력
            TextView BackBus_Text = (TextView)findViewById(R.id.BackBus_Text);
            String BackBus_String = "";
            try {
                BackBus_String = SettedBus.getBusInfo().getPath().get(SettedBus.getBackBus_place()).getStationName();
            }catch(Exception e){
                BackBus_String = "정보없음";
            }
            BackBus_Text.setText(ModifyString(2,BackBus_String));

            //현재 버스 다음 위치 출력
            TextView NextStation = (TextView)findViewById(R.id.NextStation);
            try {
                NextStation.setText(SettedBus.getBusInfo().getPath().get(Current_Seq+1).getStationName());
            }catch(Exception e){
                NextStation.setText("종점");
            }

            //현재 버스 다음 위치 출력(밑 부분)
            TextView NextStation_Buttom = (TextView)findViewById(R.id.NextStation_Buttom);
            try {
                NextStation_Buttom.setText(SettedBus.getBusInfo().getPath().get(Current_Seq+1).getStationName());
            }catch(Exception e){
                NextStation_Buttom.setText("종점");
            }

            //현재 버스 방향 출력
            TextView NextStationDirection = (TextView)findViewById(R.id.NextStationDirection);
            try {
                NextStationDirection.setText(SettedBus.getBusInfo().getPath().get(Current_Seq+2).getStationName());
            }catch(Exception e){
                NextStationDirection.setText("정보없음");
            }

            TextView RidePerson = (TextView)findViewById(R.id.RidePerson);
            TextView GetOffPerson = (TextView)findViewById(R.id.GetOffPerson);
            RidePerson.setBackgroundColor(Color.rgb(246, 235, 235));
            GetOffPerson.setBackgroundColor(Color.rgb(241, 238, 223));
        }
    }

    public String ModifyString(int version, String raw_text){
        if(version == 0){
            if(raw_text.length() > 24){
                String result = raw_text.substring(0,22).concat("...");
                return result;
            }else{
                for(int i = 0 ; i < (raw_text.length()/2) ; i++ ){
                    raw_text = " ".concat(raw_text).concat(" ");
                }
                return raw_text;
            }
        }else if(version == 1 || version == 2) {
            //for backbus
            if (raw_text.length() > 16) {
                System.out.println(raw_text);
                String result = raw_text.substring(0, 13).concat("...");
                return result;
            } else {
                System.out.println(raw_text);

                if (version == 1) {
                    //for front bus
                    StringBuilder str1 = new StringBuilder();

                    for (int j = 0; j < ((16 - raw_text.length()) / 2); j++) {
                        str1.append(" ");
                    }
                    str1.append(raw_text);

                    String result = str1.toString();

                    System.out.println("raw_text" + result + ",");
                    return result;
                } else {
                    StringBuilder str1 = new StringBuilder();
                    str1.append(raw_text);
                    for (int j = 0; j < ((14 - raw_text.length()) / 2); j++) {
                        str1.append(" ");
                    }

                    String result = str1.toString();

                    System.out.println("raw_text" + result + ",");
                    return result;
                }
            }
        }else if(version == 3){
            if(raw_text.length() <2 ) {
                raw_text = "0".concat(raw_text);
            }
            return raw_text.concat("분");
        }
        else{
            return raw_text;
        }
    }
}