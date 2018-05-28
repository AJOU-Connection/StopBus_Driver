package com.example.kimheeyeon.testapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class FileControl {
    String TAG = "FILECONTROLER";


    public FileControl(){

    }

    /**
     * ExistsFile
     * 파일의 존재여부 파악
     * @param file
     * @return boolean
     */
    public boolean ExistsFile(File file) {

        return file.exists();
    }

    /**
     * 파일의 내용 읽어오기
     * @param file
     * @return FileText
     * @throws Exception
     */
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

    /**
     * 파일 삭제
     * @param file
     * @return boolean
     */
    public boolean DeleteFile(File file){
        boolean result;
        if(file!=null&&file.exists()){
            file.delete();
            result = true;
        }else{
            result = false;
        }
        return result;
    }

//    public void WriteFile(String fileName, String data) throws Exception {
//        FileOutputStream os = openFileOutput(fileName, MODE_PRIVATE);
//        os.write(data.getBytes());
//        os.close();
//    }

    public boolean WriteFile(File file , byte[] file_content){
        boolean result;
        FileOutputStream fos;
        if(file!=null&&file.exists()&&file_content!=null){
            try {
                fos = new FileOutputStream(file);
                try {
                    fos.write(file_content);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }

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