package com.example.kimheeyeon.testapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileControl {


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
}


