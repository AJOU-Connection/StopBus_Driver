package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;

import java.io.IOException;

//import java.net.Socket;
//import java.net.URL;
//import java.net.URLConnection;
//import org.apache.http.util.ByteArrayBuffer;

//private Socket mSocket;

//socket 통신 구현해야함
public class MainActivity extends Activity {

//    private String html = "";
//    private Handler mHandler;
//
//    private Socket socket;
//
//    private BufferedReader networkReader;
//    private BufferedWriter networkWriter;
//
//    private String ip = "xxx.xxx.xxx.xxx"; // IP
//    private int port = 9999; // PORT번호


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        MyClass myClass = new MyClass();
        TextView textView1 = (TextView)findViewById( R.id.Bottom );
        textView1.setText( myClass.text );


        Bus AdvancedBus     =   new Bus();
        Bus BackUpBus       =   new Bus();

        Bus MyBus           =   new Bus();

        try {
            Route Route     =   new Route();
        } catch (IOException e) {
            e.printStackTrace();
            //toss로 txt가 없다고 명시
        }

//        try {
//            mSocket = IO.socket("SERVER URL");
//            mSocket.connect();
//        } catch(URISyntaxException e) {
//            e.printStackTrace();
//        }

    }


}
