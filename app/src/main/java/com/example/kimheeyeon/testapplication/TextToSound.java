package com.example.kimheeyeon.testapplication;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

public class TextToSound implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private boolean ttsOk;

    private String text;

    // The constructor will create a TextToSpeech instance.
    TextToSound(Context context) {
        tts = new TextToSpeech(context, this);
    }

    @Override
    // OnInitListener method to receive the TTS engine status
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            ttsOk = true;
        }
        else {
            ttsOk = false;
        }

        if(status != TextToSpeech.ERROR) {
            tts.setLanguage(Locale.KOREAN);
        }
    }

    // A method to speak something
    @SuppressWarnings("deprecation") // Support older API levels too.
    public void speak(Boolean override) {
        if (ttsOk) {
            if (override) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
            else {
                tts.speak(text, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void ttsUnder20() {
        System.out.printf("for ttss : v21 : ".concat(text));
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void ttsGreater21() {
        System.out.printf("for ttss : v 20 :".concat(text));
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
