package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;


import java.net.URLEncoder;
import android.content.Intent;

import android.os.Handler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;



public class MainActivity extends Activity {
    Handler handler = new Handler();

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
                        final TextView textView1 = (TextView)findViewById( R.id.bus_ID );
                        P_Bar.setVisibility(View.VISIBLE);

                        String url = "http://stop-bus.tk/driver/register";

                        //HashMap<String, String> sendData = new HashMap();
                        JSONObject sendData = new JSONObject();
                        try {
                            sendData.put("plateNo" , ((TextView) findViewById( R.id.Car_Number )).getText().toString());
                            sendData.put("routeID" , ((TextView) findViewById( R.id.bus_ID )).getText().toString());
                            Log.d("sending", sendData.getString("plateNo"));
                            Log.d("sending", sendData.getString("routeID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ConnectThread thread = new ConnectThread(url, sendData);
                        thread.start();


                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, DriverActivity.class);
                                MyClass myclass = new MyClass("TEST");
                                intent.putExtra("BUS_NAME", textView1.getText().toString()); //키 - 보낼 값(밸류)
                                intent.putExtra("Class_Tet", myclass);

                                startActivity(intent);
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

        private String[][] jsonParserList(String pRecvServerPage) {

            Log.i("서버에서 받은 전체 내용 : ", pRecvServerPage);

            try {
                JSONObject json = new JSONObject(pRecvServerPage);
                JSONArray jArr = json.getJSONArray("List");


                // 받아온 pRecvServerPage를 분석하는 부분
                String[] jsonName = {"header", "body"};
                String[][] parseredData = new String[jArr.length()][jsonName.length];

                for (int i = 0; i < jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    if(json != null) {
                        for(int j = 0; j < jsonName.length; j++) {
                            parseredData[i][j] = json.getString(jsonName[j]);
                        }
                    }
                }

                // 분해 된 데이터를 확인하기 위한 부분

                for(int i=0; i<parseredData.length; i++){
                    Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][0]);
                    Log.i("JSON을 분석한 데이터 "+i+" : ", parseredData[i][1]);
                }

                return parseredData;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;

            }
        }




        // 매개변수를 URL에 붙이는 함수
        private String getPostString(HashMap<String, String> map) {
            StringBuilder result = new StringBuilder();
            boolean first = true; // 첫 번째 매개변수 여부

            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (first) first = false;
                else { // 첫 번째 매개변수가 아닌 경우엔 앞에 &를 붙임
                    result.append("&");
                    Log.d("sending_true", result.toString());
                }

                try { // UTF-8로 주소에 키와 값을 붙임
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(entry.getValue());
                    Log.d("sending_true", result.toString());
                    Log.d("sending_true", entry.getValue());
                } catch (UnsupportedEncodingException ue) {
                    ue.printStackTrace();
                }
            }

            return result.toString();
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


//                    int resCode = conn.getResponseCode();
//                    if(resCode == HttpURLConnection.HTTP_OK){
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//                        String line = null;
//                        while(true){
//                            line = reader.readLine();
//                            if(line == null){
//                                break;
//                            }
//                            output.append(line + "\n");
//                        }
//                        reader.close();
//                        conn.disconnect();
//                    }
                }


            }catch(Exception ex){
                Log.e("SampleHttp", "Exception in processing response",ex);

            }

            return output.toString();

        }
    }

}
