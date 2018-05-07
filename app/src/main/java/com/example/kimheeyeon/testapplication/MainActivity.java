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

                        HashMap<String, String> sendData = new HashMap();
                        sendData.put("plateNo" , ((TextView) findViewById( R.id.Car_Number )).getText().toString());
                        sendData.put("routeID" , ((TextView) findViewById( R.id.bus_ID )).getText().toString());

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
        HashMap<String, String> s_Data = new HashMap();

        public ConnectThread(String inStr, HashMap<String, String> map){
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
            } catch (Exception ex){
                ex.printStackTrace();
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
                }

                try { // UTF-8로 주소에 키와 값을 붙임
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException ue) {
                    ue.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return result.toString();
        }

        private String request(String urlStr, HashMap<String, String> map){
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
                        bw.write(getPostString(map)); // 매개변수 전송
                        bw.flush();
                        bw.close();
                        os.close();
                    }
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) { // 연결에 성공한 경우
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 서버의 응답을 읽기 위한 입력 스트림
                        while ((line = br.readLine()) != null) // 서버의 응답을 읽어옴
                            output.append(line);
                        //response += line;
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
