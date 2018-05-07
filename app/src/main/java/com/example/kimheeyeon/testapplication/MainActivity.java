package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ProgressBar;

import android.content.Intent;

import android.os.Handler;
import java.io.*;
import java.net.URISyntaxException;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_page);

        final ProgressBar P_Bar= (ProgressBar)findViewById(R.id.progressBar);
        P_Bar.setVisibility(View.INVISIBLE); //To set visible

        MyClass myClass = new MyClass();
        //TextView textView1 = (TextView)findViewById( R.id.Bottom );
        //textView1.setText( myClass.text );

        Button search = (Button) findViewById(R.id.Confirm_Button);
        search.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        final TextView textView1 = (TextView)findViewById( R.id.bus_Number );
                        P_Bar.setVisibility(View.VISIBLE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, DriverActivity.class);
                                intent.putExtra("BUS_NAME", textView1.getText().toString()); //키 - 보낼 값(밸류)

                                startActivity(intent);
                            }
                        }, 2000);  // 2000은 2초를 의미합니다.


                    }
                }
        );

        String url = "/";
        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();

        Bus PreviousBus     =   new Bus("Previous");
        Bus NextBus       =   new Bus("Next");
        Bus MyBus           =   new Bus("my");



    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            //tv_outPut.setText(s);
        }
    }

}
