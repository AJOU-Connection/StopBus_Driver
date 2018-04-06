package com.example.kimheeyeon.testapplication;
import java.io.*;

public class Route {
    //외부로부터 txt 같은 파일로부터 데이터 긁어서 노선도 가지고 있어야함.
    //노선번호, 노선Data 가지고 있어야함
    //각 노선 Data는 정류장 번호 및 정류장 이름이 있어야함. + 지역변호
    private int RouteNumber;

    public class RouteData{
        private int LocalNum;
        private int StationNum;
        private String StationName;
    }

    private RouteData[] RouteArray;

    public Route() throws IOException {

        //txt로부터 값을 읽어오기
        //읽어온 데이터에 대해서 routeData Array push.

        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream("RouteData.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        //Read File Line By Line
        while ((strLine = br.readLine()) != null) {
            // Print the content on the console
            System.out.println(strLine);
        }

        //Close the input stream
        br.close();
    }
}
