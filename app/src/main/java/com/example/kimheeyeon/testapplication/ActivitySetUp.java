package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.ProgressBar;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ActivitySetUp extends Activity {
    Handler handler = new Handler();
    Bus settedBus = new Bus();
    String busID = ""; // 20번
    String carNumber = ""; //경기...
    String routeId = ""; //234000024
    //private ArrayList<String> locationList = new ArrayList<String>();
    String locationList[];

    String busAreaList[];
    String busRouteIdList[];

    boolean isParsedWell = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_page_bk);

        final ProgressBar P_Bar = (ProgressBar) findViewById(R.id.progressBar);
        P_Bar.setVisibility(View.INVISIBLE); //To set visible

        final Button Confirm_Button = (Button) findViewById(R.id.Confirm_Button);
        Button Area_Search = (Button) findViewById(R.id.Area_Search);
        final Button bus_search = (Button) findViewById(R.id.bus_search);


        Confirm_Button.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
//                    TextView bus_id = (TextView) findViewById(R.id.bus_ID);
//                    busID = bus_id.getText().toString();

                    P_Bar.setVisibility(View.VISIBLE);

                    //check if there is any json file that we get previous
                    //파일이 있으면 가지고와서 정리(bus 데이터 만든다는 뜻)
                    //없으면 서버로부터 받아오기 + txt file로 저장
                    File testfile = ActivitySetUp.this.getFilesDir();
                    File file = new File(testfile, routeId.concat(".txt"));

                    FileControl fc = new FileControl();

                    String ReadData = "";

                    if (fc.ExistsFile(file)) {
                        Log.i("version", "get from file");

                        try {
                            ReadData = fc.ReadFile(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.i("read Data", ReadData);
                        jsonParserList(ReadData);

                    } else {
                        Log.i("version", "get from server");
                        String url = "http://stop-bus.tk/driver/register";
                        String version = "stationList";

                        JSONObject sendData = new JSONObject();
                        try {
                            sendData.put("plateNo", "경기00가1234");
                            sendData.put("routeID", routeId);
                            Log.d("sending", sendData.getString("plateNo"));
                            Log.d("sending", sendData.getString("routeID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ConnectThread thread = new ConnectThread(url, sendData, version);
                        thread.start();

                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    if(isParsedWell) {
                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    Intent intent = new Intent(ActivitySetUp.this, ActivityDriver.class);

                                    //Log.d("ack data", settedBus.getBusInfo().getBusNumber());
                                    intent.putExtra("busData", settedBus);

                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 500);  // 2000은 2초를 의미합니다.
                    }else{
                        Toast.makeText(getApplicationContext(), "해당하는 버스는 운행중이지 않습니다", Toast.LENGTH_LONG).show();

                    }
                }
            }
        );

        bus_search.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {

                    P_Bar.setVisibility(View.VISIBLE);

                    String url = "http://stop-bus.tk/user/busLocationList";
                    String version = "busList";

                    JSONObject sendData = new JSONObject();
                    try {
                        sendData.put("routeID", routeId);
                        Log.d("sending", sendData.getString("routeID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ConnectThread Location_thread = new ConnectThread(url, sendData, version);
                    Location_thread.start();

                    try {
                        Location_thread.join();
                        P_Bar.setVisibility(View.INVISIBLE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(isParsedWell) {
                        Spinner spinner = (Spinner) findViewById(R.id.Car_Number);

                        spinner.setVisibility(View.VISIBLE);

                        if (locationList[0].compareTo("NO BUS") != 0) {
                            Confirm_Button.setVisibility(View.VISIBLE);
                        }

                        ArrayAdapter<String> spinnerArray = new ArrayAdapter<String>(ActivitySetUp.this, android.R.layout.simple_spinner_item, locationList);
                        spinner.setAdapter(spinnerArray);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String item = (String) parent.getSelectedItem();
                                carNumber = item;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), "해당하는 버스가 운행중이지 않습니다", Toast.LENGTH_LONG).show();

                    }
                }
            }
        );

        Area_Search.setOnClickListener(
            new Button.OnClickListener(){
                public void onClick(View v){
                    TextView bus_id = (TextView) findViewById(R.id.bus_ID);
                    busID = bus_id.getText().toString();

                    String url = "http://stop-bus.tk/user/search?type=route";
                    String version = "areaList";

                    JSONObject sendData = new JSONObject();
                    try {
                        sendData.put("keyword", busID);
                        Log.d("sending", sendData.getString("keyword"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ConnectThread Area_thread = new ConnectThread(url, sendData, version);
                    Area_thread.start();

                    try {
                        Area_thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if(isParsedWell) {
                        Spinner Car_Area = (Spinner) findViewById(R.id.Car_Area);

                        Car_Area.setVisibility(View.VISIBLE);
                        bus_search.setVisibility(View.VISIBLE);

                        ArrayAdapter<String> spinnerArray = new ArrayAdapter<String>(ActivitySetUp.this, android.R.layout.simple_spinner_item, busAreaList);
                        Car_Area.setAdapter(spinnerArray);

                        Car_Area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String item = (String) parent.getSelectedItem();
                                for (int i = 0; i < busAreaList.length; i++) {
                                    if (busAreaList[i].compareTo(item) == 0)
                                        routeId = busRouteIdList[i];
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), "해당하는 버스가 없습니다", Toast.LENGTH_LONG).show();
                    }

                }
            }
        );
    }

    class ConnectThread extends Thread {
        String urlStr;
        JSONObject s_Data;
        String version;

        public ConnectThread(String inStr, JSONObject map, String version) {
            urlStr = inStr;
            this.s_Data = map;
            this.version = version;
        }

        public void run() {

            try {
                final String output = request(urlStr, this.s_Data);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("gmmm", output);
                    }
                });

                switch(this.version) {
                    case "stationList" :
                        jsonParserList(output);
                        break;
                    case "busList" :
                        parseBusLocation(output);
                        break;
                    case "areaList" :
                        parseAreaList(output);
                    default:
                        System.out.println("setup is fail");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private String request(String urlStr, JSONObject map) {
            StringBuilder output = new StringBuilder();

            try {
                URL url = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn != null) {
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
            } catch (Exception ex) {
                Log.e("SampleHttp", "Exception in processing response", ex);

            }
            return output.toString();
        }
    }

    public void jsonParserList(String pRecvServerPage) {
        try {
            FileOutputStream os = openFileOutput(busID.concat(".txt"), MODE_PRIVATE);
            os.write(pRecvServerPage.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

        try {
            JSONObject jsonObject = new JSONObject(pRecvServerPage);

            JSONObject jHeader = jsonObject.getJSONObject("header");  // JSONObject 추출
            Log.d("PARSING", jHeader.getString("result"));

            if (jHeader.getString("result").compareTo("true") == 0) {

                JSONObject jBody = jsonObject.getJSONObject("body");  // JSONObject 추출

                if (jBody == null) {
                    System.out.print("nul,,?");
                    //Toast.makeText(getApplicationContext(), "해당하는 버스가 없습니다", Toast.LENGTH_LONG).show();
                    isParsedWell = false;
                    return;
                }

                isParsedWell = true;
                settedBus.setBusInfo(jBody);
                settedBus.setVehicleNumber(routeId);
                settedBus.setCarNumber(carNumber);
            } else {
                isParsedWell = false;
                Log.d("GET DATA ERR", "FAIL TO GET BUSLIST");
                //Toast.makeText(getApplicationContext(), "해당하는 버스가 없습니다", Toast.LENGTH_LONG).show();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseBusLocation(String pRecvServerPage) {

        Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

        try {
            JSONObject jsonObject = new JSONObject(pRecvServerPage);

            JSONObject jHeader = jsonObject.getJSONObject("header");  // JSONObject 추출
            Log.d("PARSING", jHeader.getString("result"));

            if(jHeader.getString("result").compareTo("true") == 0) {

                try {
                    JSONArray testarray = jsonObject.getJSONArray("body");

                    if (testarray == null) {
                        System.out.print("json is null when get buslocation");

                        //Toast.makeText(getApplicationContext(), "해당하는 버스가 없습니다", Toast.LENGTH_LONG).show();


                        isParsedWell = false;

                        return;
                    }

                    isParsedWell = true;

                    if (jHeader.getString("result").compareTo("true") != 0)
                        System.out.println("errrr!!1");
                    else {
                        JSONArray BusList = jsonObject.getJSONArray("body");
                        //강제로 null시켜서 비우기
                        locationList = null;
                        this.locationList = new String[BusList.length()];
                        for (int i = 0; i < BusList.length(); i++) {
                            JSONObject binfo = BusList.getJSONObject(i);
                            Log.d("Compare", binfo.getString("plateNo").concat(" and ").concat(binfo.getString("stationSeq")));
                            this.locationList[i] = binfo.getString("plateNo");
                        }

                        if (locationList == null) {
                            this.locationList = new String[1];
                            this.locationList[0] = "NO BUS";
                        }

                    }
                }catch (JSONException e){
                    isParsedWell = false;
                    return;
                }
            }else{
                isParsedWell = false;
                Log.d("fail to find","in Driver Activity AT BUSLOCATION");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void parseAreaList(String pRecvServerPage) {

        Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

        try {
            JSONObject jsonObject = new JSONObject(pRecvServerPage);

            JSONObject jHeader = jsonObject.getJSONObject("header");  // JSONObject 추출
            Log.d("PARSING", jHeader.getString("result"));

            if(jHeader.getString("result").compareTo("true") == 0) {

                try {
                    JSONArray testarray = jsonObject.getJSONArray("body");
                    if (testarray == null) {
                        System.out.print("json is null when get areaList");

                        isParsedWell = false;
                        return;
                    }

                    isParsedWell = true;

                    if (jHeader.getString("result").compareTo("true") != 0)
                        System.out.println("errrr!!1");
                    else {
                        JSONArray AreaList = jsonObject.getJSONArray("body");
                        //강제로 null시켜서 비우기

                        int count = 0;
                        for (int i = 0; i < AreaList.length(); i++) {
                            JSONObject binfo = AreaList.getJSONObject(i);
                            if (binfo.getString("routeNumber").compareTo(busID) == 0)
                                count++;
                        }

                        this.busAreaList = new String[count];
                        this.busRouteIdList = new String[count];

                        int number = 0;

                        for (int i = 0; i < AreaList.length(); i++) {
                            JSONObject binfo = AreaList.getJSONObject(i);
                            if (binfo.getString("routeNumber").compareTo(busID) == 0) {
                                this.busRouteIdList[number] = binfo.getString("routeID");
                                this.busAreaList[number++] = binfo.getString("regionName");
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    isParsedWell = false;
                }
            }else{
                isParsedWell = false;
                Log.d("fail to find","in Driver Activity AT AREAINFO");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}