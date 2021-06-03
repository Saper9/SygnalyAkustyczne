package com.example.kodmorsa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button recordButton;
    private static Context context;
    public static android.content.res.Resources res;
    static int patternLength = 10;
    static boolean isThreadWorking = false;
    EditText morseTextField;
    String pattern;



    String morseSheetString="abcdefghijklmnoprstuvwxyz0123456789.,?!+-/ ";
    private void PlayGeneratedPattern() {
        Log.i("Pattern: ",pattern);
        MorseTest.SetUpEverything();
        MorseTest.GenerateSoundWave();
        MorseTest.GenerateSoundWaveLine();
        pattern=pattern.toUpperCase();
        String morsePat = MorseTest.ConvertPatternToMorsePattern(pattern);
        MorseTest.PlayPattern(morsePat);

    }
    private void stopRecording(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = getResources();
        recordButton=findViewById(R.id.recordButton);
        morseTextField=findViewById(R.id.textToMorseTextField);
        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isThreadWorking) {
                    //pattern = GeneratePattern(patternLength);
                    pattern=morseTextField.getText().toString();
                    isThreadWorking = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            PlayGeneratedPattern();
                        }
                    }).start();
                } else isThreadWorking = false;
            }
        });

        Button soundrecognition=findViewById(R.id.voiceButton);

        soundrecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activitymaker();

                //startActivity(new Intent(MainActivity.this,SoundToMorse.class));

            }
        });
    }
public void activitymaker(){
        Intent intent = new Intent(MainActivity.this,SoundToMorse.class);
        intent.putExtra("test",true);
        startActivity(intent);

}

}