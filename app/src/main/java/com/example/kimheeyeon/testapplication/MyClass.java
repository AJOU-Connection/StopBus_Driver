package com.example.kimheeyeon.testapplication;

import java.io.Serializable;

public class MyClass implements Serializable{
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String text;

    public MyClass() {
        text = "HaHaHaHaHa";
    }
    public MyClass(String var){
        setText(var);
    }


}
