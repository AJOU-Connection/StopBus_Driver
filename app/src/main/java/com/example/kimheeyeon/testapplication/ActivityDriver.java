package com.example.kimheeyeon.testapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

//speach
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;


public class ActivityDriver extends Activity implements OnInitListener{
    Handler handler = new Handler();
    Bus SettedBus = new Bus();
    private TextToSpeech tts;
    String tts_text = null;
    private static String address = "30:14:09:30:15:33";

    private SharedPreferences getoff_share;


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

    public void initData_send(){
        getoff_share = getSharedPreferences("getoff_share", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = getoff_share.edit();
        editor.putString("getoff_s", "");
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
        setContentView(R.layout.activity_test2);

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
        tts = new TextToSpeech(this, this);

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
                //System.out.println("Hello !!");

                //색상 초기화
                TextView RidePerson = (TextView)findViewById(R.id.RidePerson);
                RidePerson.setBackgroundColor(Color.rgb(246, 235, 235));

                TextView GetOffPerson = (TextView)findViewById(R.id.GetOffPerson);
                GetOffPerson.setBackgroundColor(Color.rgb(241, 238, 223));

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
                    Thread.sleep(200);
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

                } catch (InterruptedException e){
                    e.printStackTrace();
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ActivityDriver.ConnectThread thread_Gap = new ActivityDriver.ConnectThread(url_time, sendStationCurrent, getGap);
                thread_Gap.start();

                try{
                    thread_Gap.join();

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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    ActivityDriver.ConnectThread thread_Passenger = new ActivityDriver.ConnectThread(url_Passenger, sendPassenger, getPassenger);
                    thread_Passenger.start();

                    try {
                        thread_Passenger.join();
                        if(tts_text!= null) {
                            Log.i("tts_text", tts_text);

                            if(tts_text.compareTo("가져오기 에러") != 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    ttsGreater21(tts_text);
                                    //ttsGreater21(SettedBus.getBusInfo().getPath().get( SettedBus.getCurrent_place() + 1).getStationName());
                                } else {
                                    ttsUnder20(tts_text);
                                    //ttsUnder20(SettedBus.getBusInfo().getPath().get( SettedBus.getCurrent_place() + 1).getStationName());

                                }
                            }
                        }
                        //speakOut(tts_text);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0,    20, TimeUnit.SECONDS);
    }

    //tts function
    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR) {
            tts.setLanguage(Locale.KOREAN);
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        System.out.printf("for ttss : v21 : ".concat(text));
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        System.out.printf("for ttss : v 20 :".concat(text));
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    private void setTTS(boolean isGetIn, boolean isGetOff, int version){
        if(tts_text != null){
            tts_text = null;
        }

        String b_getOff = loadScore();
        Log.d("FROMBL", b_getOff);

        if(version == 0 )//true
        {

            if (isGetIn) {
                //탑승객이 있는 경우
                TextView RidePerson = (TextView) findViewById(R.id.RidePerson);
                RidePerson.setBackgroundColor(Color.rgb(229, 78, 78));

                tts_text = "다음 정류장에 탑승객이 있습니다";
            }
            if (isGetOff || b_getOff.compareTo("o")==0 ) {
                //하차객이 있는 경우
                TextView GetOffPerson = (TextView) findViewById(R.id.GetOffPerson);
                GetOffPerson.setBackgroundColor(Color.rgb(239, 215, 95));

                if (tts_text != null) {
                    tts_text = "다음 정류장에 하차객이 있습니다";
                } else {
                    tts_text = "다음 정류장에 탑승객과 하차객이 있습니다";
                }
            }else{
                //하차객이 아에 없는 경우
                saveData("x");
                saveData_send("x");
                tts_text = "다음 정류장에는 승하차객이 없습니다";
            }

            if (!isGetIn && !isGetOff) {
                tts_text = "다음 정류장에는 승하차객이 없습니다";
            }
        }else if(version == 1){
            if (b_getOff.compareTo("o")==0 ) {
                //하차객이 있는 경우
                TextView GetOffPerson = (TextView) findViewById(R.id.GetOffPerson);
                GetOffPerson.setBackgroundColor(Color.rgb(239, 215, 95));

                tts_text = "다음 정류장에 하차객이 있습니다";
            }
            else{
                tts_text = "다음 정류장에는 승하차객이 없습니다";
            }
        }
        Log.d("so_text", tts_text);
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

                        Log.d("FirstBus", JBody.getString("plateNo1").concat(String.valueOf(predictTime1)));
                        Log.d("SecondBus", JBody.getString("plateNo2").concat(String.valueOf(predictTime2 - predictTime1)));

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

                        setTTS(isGetIn, isGetOff, 0);
                    }
                }else{
                    Log.d("FAIL TO GET INFO", "Passenger INFORMATION");
                    tts_text = "가져오기 에러";

                    setTTS(false, false, 1);
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