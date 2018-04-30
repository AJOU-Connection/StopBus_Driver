package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;

import java.io.*;
import java.net.URISyntaxException;



//socket 통신 구현해야함
public class MainActivity extends Activity {

    private BufferedReader mIn;
    private PrintWriter mOut;


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


        String ip = "192.168.0.10";
        int port = 5555;

        //Client myClient = new Client(ip, port);

    }


}
