package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;

import java.io.*;
import java.net.URISyntaxException;
//import java.net.Socket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


//socket 통신 구현해야함
public class MainActivity extends Activity {

    private BufferedReader mIn;
    private PrintWriter mOut;

//    private Client mSocket = new Client("19",90);

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

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        MyClass myClass = new MyClass();
        TextView textView1 = (TextView)findViewById( R.id.Bottom );
        textView1.setText( myClass.text );


        Bus AdvancedBus     =   new Bus("advanced");
        Bus BackUpBus       =   new Bus("backup");
        Bus MyBus           =   new Bus("my");

//        try {
//            Route Route     =   new Route();
//        } catch (IOException e) {
//            e.printStackTrace();
//            //toss로 txt가 없다고 명시
//        }

//        try {
//            mSocket = IO.socket("SERVER URL");
//            mSocket.connect();
//        } catch(URISyntaxException e) {
//            e.printStackTrace();
//        }

        mSocket.connect();

        String ip = "192.168.0.10";
        int port = 5555;

        //Client myClient = new Client(ip, port);

    }


}
