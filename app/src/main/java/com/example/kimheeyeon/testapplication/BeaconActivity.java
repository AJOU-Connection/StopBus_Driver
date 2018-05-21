package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BeaconActivity extends Activity implements BeaconConsumer{
    Handler handler = new Handler();
    private BeaconManager beaconManager;

    //감지된 비콘 임시 리스트
    private List<Beacon> beaconList = new ArrayList<>();

    //this is for beacon test
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_text);

        //final Intent intent = getIntent();
        TextView b_List = (TextView)findViewById(R.id.beaconList);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

    }

}