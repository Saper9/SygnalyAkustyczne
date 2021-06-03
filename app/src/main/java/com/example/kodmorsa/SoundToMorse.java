package com.example.kodmorsa;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;

public class SoundToMorse extends AppCompatActivity {
    private MediaRecorder recorder;
    private MediaPlayer player;

    Button recordStart;
    Button stopRecord;


    //TODO fix function
    private void prepareRecorder(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile("Test.wav");
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("startRecording", "prepare() failed");
        }

        recorder.start();

    }
    private void loadFile() throws IOException {
        InputStream input=getAssets().open("Test.wav");
    }


    private void stopRecording(){
        recorder.stop();
        recorder.release();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_to_morse);
         recordStart=findViewById(R.id.listeningButton);
         stopRecord=findViewById(R.id.stopButton);



        recordStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prepareRecorder();
            }
        });


        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });


    }



}