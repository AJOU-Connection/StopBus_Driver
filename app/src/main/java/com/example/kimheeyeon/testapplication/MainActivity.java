package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        MyClass myClass = new MyClass();
        TextView textView1 = (TextView)findViewById( R.id.Bottom );
        textView1.setText( myClass.text );
    }


}
