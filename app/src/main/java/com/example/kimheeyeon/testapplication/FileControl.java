package com.example.kimheeyeon.testapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class FileControl {
    String TAG = "FILECONTROLER";


    public FileControl(){

    }

    public boolean ExistsFile(File file) {

        return file.exists();
    }

    public String ReadFile(File file) throws Exception{
        StringBuilder text = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }

        return text.toString();
    }

//    public void WriteFile(String fileName, String data) throws Exception {
//        FileOutputStream os = openFileOutput("makingTest.txt", MODE_PRIVATE);
//        os.write(data.getBytes());
//        os.close();
//    }

    public File CreateFile(File dir , String file_path){
        File file = null;
        boolean isSuccess = false;
        if(dir.isDirectory()){
            file = new File(file_path);
            if(!file.exists()){
                Log.i( TAG , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                }
            }else{
                Log.i( TAG , "file.exists" );
            }
        }
        return file;
    }
}


