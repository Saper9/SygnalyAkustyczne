package com.example.kodmorsa;

import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SoundToMorse extends AppCompatActivity {
    private MediaRecorder recorder;
    private MediaPlayer player;

    Button recordStart;
    Button stopRecord;

    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile("TempFile", ".wav");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadFile() throws IOException, WavFileException, FileFormatNotSupportedException, com.example.kodmorsa.WavFileException {
        InputStream inp = getAssets().open("500hz_noise.wav");
        File file = stream2file(inp);
        MorseToTextConverter morseToTextConverter = new MorseToTextConverter(file.toString());
        morseToTextConverter.executeTranslation();
        Log.i("File info: ", morseToTextConverter.toString());
        inp.close();

        int defaultSampleRate = -1;		//-1 value implies the method to use default sample rate
        int defaultAudioDuration = -1;	//-1 value implies the method to process complete audio duration

        JLibrosa jLibrosa = new JLibrosa();
        float[] audioFeatureValues = jLibrosa.loadAndRead(file.toString(), defaultSampleRate, defaultAudioDuration);

        setContentView(R.layout.chart_layout);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.line();
        cartesian.title("Test test");
        cartesian.yAxis(0).title("Value");
        cartesian.xAxis(0).title("Sample");
        List<DataEntry> seriesData = new ArrayList<>();
        for (int i = 0; i < audioFeatureValues.length; i++) {
            seriesData.add(new CustomDataEntry(i, audioFeatureValues[i]));
        }
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Line series1 = cartesian.line(series1Mapping);
        series1.name("Test");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        anyChartView.setChart(cartesian);
    }


    private void stopRecording() {
        recorder.stop();
        recorder.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_to_morse);
        recordStart = findViewById(R.id.listeningButton);
        stopRecord = findViewById(R.id.stopButton);


        recordStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    loadFile();
                } catch (IOException | WavFileException | FileFormatNotSupportedException | com.example.kodmorsa.WavFileException e) {
                    e.printStackTrace();
                }
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
