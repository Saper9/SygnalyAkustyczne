package com.example.kodmorsa;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TextToMorseConverter extends MainActivity {
    private static AudioTrack audioTrack;
    private static int SAMPLE_RATE_HZ;
    private static int numofSamples;
    private static short samples[];
    private static short silenceTab[];
    private static float Hz;
    private static boolean setHzflag = false;
    private static int time = 250; //ms
    private static short samplesLine[];
    public static void SetTimeOfDot(int x) {
        time = x;
    }
    public static void SetUpEverything() {
        SAMPLE_RATE_HZ = 48000;
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_HZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE_HZ, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
        audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        numofSamples = time * SAMPLE_RATE_HZ / 1000; //  ms * samplerate/1000
        samples = new short[numofSamples];
        silenceTab = new short[numofSamples];
        samplesLine = new short[3 * time * SAMPLE_RATE_HZ / 1000];
        Log.i("Test", 3 * time * SAMPLE_RATE_HZ / 1000 + "");
        if (setHzflag == false) {
            Hz = 1000.0f;//domyslna wartosc dla nieustawionego Hz
        }

    }

    public static void SetHz(float Hzz) {
        Hz = Hzz;
        setHzflag = true;
    }

    public static void GenerateSoundWave() {


        for (int i = 0; i < numofSamples; i++) {

            samples[i] = (short) (Math.sin(i * Hz/*Hz*/ / SAMPLE_RATE_HZ * Math.PI * 2) * 15000/*Amp*/);
            silenceTab[i] = 0;
        }
    }

    public static void GenerateSoundWaveLine() {
        for (int i = 0; i < (3 * time * SAMPLE_RATE_HZ / 1000); i++) {

            samplesLine[i] = (short) (Math.sin(i * Hz/*Hz*/ / SAMPLE_RATE_HZ * Math.PI * 2) * 15000/*Amp*/);

        }

    }


    public static String ConvertPatternToMorsePattern(String pattern) {
        String morsePattern = "";
        GenerateSoundWave();
        for (int i = 0; i < pattern.length(); i++) {
            Log.i("charPattern", String.valueOf(pattern.charAt(i)));
            if (pattern.charAt(i) == 'A') morsePattern += ".- ";
            if (pattern.charAt(i) == 'B') morsePattern += "-... ";
            if (pattern.charAt(i) == 'C') morsePattern += "-.-. ";
            if (pattern.charAt(i) == 'D') morsePattern += "-.. ";
            if (pattern.charAt(i) == 'E') morsePattern += ". ";
            if (pattern.charAt(i) == 'F') morsePattern += "..-. ";
            if (pattern.charAt(i) == 'G') morsePattern += "--. ";
            if (pattern.charAt(i) == 'H') morsePattern += ".... ";
            if (pattern.charAt(i) == 'I') morsePattern += ".. ";
            if (pattern.charAt(i) == 'J') morsePattern += ".--- ";
            if (pattern.charAt(i) == 'K') morsePattern += "-.- ";
            if (pattern.charAt(i) == 'L') morsePattern += ".-.. ";
            if (pattern.charAt(i) == 'M') morsePattern += "-- ";
            if (pattern.charAt(i) == 'N') morsePattern += "-. ";
            if (pattern.charAt(i) == 'O') morsePattern += "--- ";
            if (pattern.charAt(i) == 'P') morsePattern += ".--. ";
            if (pattern.charAt(i) == 'Q') morsePattern += "--.- ";
            if (pattern.charAt(i) == 'R') morsePattern += ".-. ";
            if (pattern.charAt(i) == 'S') morsePattern += "... ";
            if (pattern.charAt(i) == 'T') morsePattern += "- ";
            if (pattern.charAt(i) == 'U') morsePattern += "..- ";
            if (pattern.charAt(i) == 'V') morsePattern += "...- ";
            if (pattern.charAt(i) == 'W') morsePattern += ".-- ";
            if (pattern.charAt(i) == 'X') morsePattern += "-..- ";
            if (pattern.charAt(i) == 'Y') morsePattern += "-.-- ";
            if (pattern.charAt(i) == 'Z') morsePattern += "--.. ";

            if (pattern.charAt(i) == '.') morsePattern += ".-.-.- ";
            if (pattern.charAt(i) == ',') morsePattern += "--..-- ";
            if (pattern.charAt(i) == '/') morsePattern += "-..-. ";
            if (pattern.charAt(i) == '?') morsePattern += "..--.. ";

            if (pattern.charAt(i) == '0') morsePattern += "----- ";
            if (pattern.charAt(i) == '1') morsePattern += ".---- ";
            if (pattern.charAt(i) == '2') morsePattern += "..--- ";
            if (pattern.charAt(i) == '3') morsePattern += "...-- ";
            if (pattern.charAt(i) == '4') morsePattern += "....- ";
            if (pattern.charAt(i) == '5') morsePattern += "..... ";
            if (pattern.charAt(i) == '6') morsePattern += "-.... ";
            if (pattern.charAt(i) == '7') morsePattern += "--... ";
            if (pattern.charAt(i) == '8') morsePattern += "---.. ";
            if (pattern.charAt(i) == '9') morsePattern += "----. ";

            if (pattern.charAt(i) == ' ') morsePattern += "[";


        }

        return morsePattern;

    }

    private static void PlayDot() {

        audioTrack.write(samples, 0, numofSamples);

    }

    private static void PlayLine() {

        audioTrack.write(samplesLine, 0, numofSamples * 3);


    }

    private static void PlaySilence() {

        audioTrack.write(silenceTab, 0, numofSamples);
    }


    private static void PlaySilenceBetweenChars() {

        audioTrack.write(silenceTab, 0, numofSamples);
        audioTrack.write(silenceTab, 0, numofSamples);
        audioTrack.write(silenceTab, 0, numofSamples);


    }
    private static void PlaySilenceBetweenWords(){

        audioTrack.write(silenceTab, 0, numofSamples);
        audioTrack.write(silenceTab, 0, numofSamples);
        audioTrack.write(silenceTab, 0, numofSamples);
        audioTrack.write(silenceTab, 0, numofSamples);
        audioTrack.write(silenceTab, 0, numofSamples);
        audioTrack.write(silenceTab, 0, numofSamples);
        audioTrack.write(silenceTab, 0, numofSamples);


    }

    public static int isStillSameCharacter(String morsePattern, int i) {

        if (morsePattern.charAt(i + 1) == '.' || morsePattern.charAt(i + 1) == '-') return 1;

        else return 0;
    }


    public static void PlayPattern(String morsePattern) {
        Log.i("pattern morse",morsePattern);


        audioTrack.play();
        for (int i = 0; i < morsePattern.length(); i++) {
            if (MainActivity.isThreadWorking == false) break;


            if (morsePattern.charAt(i) == '.') {

                //play znak
                PlayDot();
                //if (isStillSameCharacter(morsePattern, i) == 1) continue;
                PlaySilence();

            }
            if (morsePattern.charAt(i) == '-') {
                PlayLine();
                //if (isStillSameCharacter(morsePattern, i) == 1) continue;
                PlaySilence();

            }
            if (morsePattern.charAt(i) == ' ') {
                PlaySilenceBetweenChars();

            }
            if (morsePattern.charAt(i) == '[') {
                PlaySilenceBetweenWords();

            }


        }
        MainActivity.isThreadWorking = false;

    }
}
