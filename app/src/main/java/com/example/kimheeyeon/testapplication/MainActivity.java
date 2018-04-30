package com.example.kimheeyeon.testapplication;

import android.app.Activity;
import android.os.Bundle;

import android.widget.TextView;

import java.io.*;
import java.net.URISyntaxException;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

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


        String url = "/";
        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();

        Bus AdvancedBus     =   new Bus("advanced");
        Bus BackUpBus       =   new Bus("backup");
        Bus MyBus           =   new Bus("my");


        String ip = "192.168.0.10";
        int port = 5555;


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
