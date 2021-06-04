package com.example.kodmorsa;

import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFile;
import com.jlibrosa.audio.wavFile.WavFileException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;

public class SoundToMorse extends AppCompatActivity {
    private MediaRecorder recorder;
    private MediaPlayer player;

    Button recordStart;
    Button stopRecord;

    public static File stream2file (InputStream in) throws IOException {
        final File tempFile = File.createTempFile("TempFile", ".wav");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadFile() throws IOException, WavFileException, FileFormatNotSupportedException {
        InputStream inp=getAssets().open("Test.wav");
        File file=stream2file(inp);
        int defaultSampleRate = -1;		//-1 value implies the method to use default sample rate
        int defaultAudioDuration = -1;
        JLibrosa jLibrosa = new JLibrosa();
        String test = file.toString();
        float audioFeatureValues [] = jLibrosa.loadAndRead(file.toString(), defaultSampleRate, defaultAudioDuration);
//        ArrayList<Float> audioFeatureValuesList = jLibrosa.loadAndReadAsList(file.toString(), defaultSampleRate, defaultAudioDuration);
//        WavFile wavfile=WavFile.openWavFile(file);
////        int[] buffer=new int[300];
////        int tmp=wavfile.readFrames(buffer,300);
//        Log.i("wavFileDD", String.valueOf(wavfile));

        //z pliku wavfile iterowac po nim co 4410 (tyle trwa kropka) sprawdzamy, czy wartosc jest rowna 0.0
        //jezeli jest 0 to znaczy, ze dzwiek nie gra i klasyfikujemy jako kropke
        //jeżeli kropka trwa jeden raz to jest to przerwa miedzy sygnalami w jednej literze
        //jezeli trwa 3 kropki to jest to przerwa miedzy znakami
        //jezeli nie jest rowna 0 to jest wtedy grajacym sygnalem
        //jezeli gra raz to jest krotkim syngalem
        //jezeli gra 3 razy to jest dlugim
        //mierzyc co pol kropki, wtedy ominie sie przypadki , że przypadkowo ominiemy kropke


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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    loadFile();
                } catch (IOException | WavFileException | FileFormatNotSupportedException e) {
                    e.printStackTrace();
                }
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
