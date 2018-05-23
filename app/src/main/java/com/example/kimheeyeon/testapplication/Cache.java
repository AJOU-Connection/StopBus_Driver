package com.example.kimheeyeon.testapplication;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Cache {
    Context context;
    public Cache(Context co){
        context = co;
    }

    public File getCacheDir(Context context) {
        File cacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "cachefolder");
            if(!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }
        if(!cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }

    public void Write(String obj) throws IOException {
        File cacheDir = getCacheDir(context);
        File cacheFile = new File(cacheDir, "Cache.txt");
        if(!cacheFile.exists())cacheFile.createNewFile();
        FileWriter fileWriter = new FileWriter(cacheFile);
        fileWriter.write(obj);
        fileWriter.flush();
        fileWriter.close();
    }

    public String Read() throws IOException {
        File cacheDir = getCacheDir(context);
        File cacheFile = new File(cacheDir, "Cache.txt");
        if(!cacheFile.exists())cacheFile.createNewFile();
        FileInputStream inputStream = new FileInputStream(cacheFile);
        Scanner s = new Scanner(inputStream);
        String text="";
        while(s.hasNext()){
            text+=s.nextLine();
        }
        inputStream.close();
        return text;
    }

}