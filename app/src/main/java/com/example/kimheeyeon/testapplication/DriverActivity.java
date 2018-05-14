package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import android.content.Intent;

import java.io.*;
import java.net.URISyntaxException;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

public class DriverActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView textView1 = (TextView)findViewById( R.id.BusNum );
        Intent intent = getIntent();
        textView1.setText( intent.getStringExtra("CAR_NUMBER") );
        Bus SettedBus = (Bus)intent.getSerializableExtra("busData");
        textView1.setText( SettedBus.getBusInfo().getBusNumber().concat("번"));

        TextView FrontBus_Text = (TextView)findViewById(R.id.FrontBus_Text);






    }
}
