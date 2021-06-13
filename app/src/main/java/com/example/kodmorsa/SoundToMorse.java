package com.example.kodmorsa;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.FloatFFT_1D;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
        InputStream inp = getAssets().open("sos_noise_high.wav");
        File file = stream2file(inp);
//        MorseToTextConverter morseToTextConverter = new MorseToTextConverter(file.toString());
//        morseToTextConverter.executeTranslation();
//        Log.i("File info: ", morseToTextConverter.toString());
        inp.close();


//        int defaultSampleRate = -1;        //-1 value implies the method to use default sample rate
        int defaultAudioDuration = -1;    //-1 value implies the method to process complete audio duration
        int Fs = 48000; // Fs is sampling frequency -48 Khz
        JLibrosa jLibrosa = new JLibrosa();
        float [] audioFeatureValues = jLibrosa.loadAndRead(file.toString(), Fs, defaultAudioDuration);
        double [] audioValues = convertFloatsToDoubles(audioFeatureValues);
        spectri(audioValues, Fs, 0, 2500);
//        ArrayList<Float> audioFeatureValues = jLibrosa.loadAndReadAsList(file.toString(), Fs, defaultAudioDuration);
//        List<Map<Integer, Float>> peaks = CustomUtils.peak_detection(audioFeatureValues, 0.01F);
//
//        setContentView(R.layout.chart_layout);
//        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
//        Cartesian cartesian = AnyChart.line();
//        cartesian.title("Test test");
//        cartesian.yAxis(0).title("Value");
//        cartesian.xAxis(0).title("Sample");
//        List<DataEntry> seriesData = new ArrayList<>();
//        for (int i = 0; i < audioFeatureValues.length; i++) {
//            seriesData.add(new CustomDataEntry(i, audioFeatureValues[i]));
//        }
//        Set set = Set.instantiate();
//        set.data(seriesData);
//        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
//        Line series1 = cartesian.line(series1Mapping);
//        series1.name("Test");
//        series1.hovered().markers().enabled(true);
//        series1.hovered().markers()
//                .type(MarkerType.CIRCLE)
//                .size(4d);
//        series1.tooltip()
//                .position("right")
//                .anchor(Anchor.LEFT_CENTER)
//                .offsetX(5d)
//                .offsetY(5d);
//        anyChartView.setChart(cartesian);
    }
    public static double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
        {
            return null; // Or throw an exception - your choice
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }

    private void spectri(double[] data, double Fs, int start_f, int stop_f) {
        int N = data.length;
        double[] spec = Arrays.copyOf(data, data.length);
        com.example.kodmorsa.Complex [] dataComplex= new com.example.kodmorsa.Complex[data.length];
        com.example.kodmorsa.Complex[] fftNew=new com.example.kodmorsa.Complex[data.length];
        fftNew=FFT.fft1D(dataComplex);
        //DoubleFFT_1D fft = new DoubleFFT_1D(spec.length);
        //fft.complexForward(spec);
        double df = Fs/N; // Frequency bin size
        double minf = -Fs/2;
        double maxf = Fs/2 - df;
        int i = (int) Math.ceil(N/2 + (start_f * N / 2) / (Fs / 2));
        int j = (int) Math.ceil(N/2 + (stop_f * N / 2) / (Fs / 2));
        int howMany = (int) Math.ceil((maxf - minf) / df);
        List<Double> f = new ArrayList<>(howMany); // Frequency axis
        double k = minf;
        while (k <= maxf) {
            f.add(k);
            k += df;
        }
        //double[] fftShift = fftshift(fftNew, false, true);
        com.example.kodmorsa.Complex[] fftShift=FFT.fftShift1D(fftNew);
        List<Double> y = new ArrayList<Double>(spec.length);
        for (int l = 0; l < spec.length; l ++) {

            double test = Math.abs(fftShift[l].Abs());
            y.add(20 * Math.log10(test));
        }
        setContentView(R.layout.chart_layout);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.line();
        cartesian.title("Test test");
        cartesian.yAxis(0).title("Volume [dB]");
        cartesian.xAxis(0).title("Frequency [Hz]");
        List<DataEntry> seriesData = new ArrayList<>();
        for (int p = i; p < j; p++) {
            seriesData.add(new CustomDataEntry(f.get(p), y.get(p)));
        }
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Line series1 = cartesian.line(series1Mapping);
        series1.name("Spectrum");
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

    /**
     * shift zero frequency to center, or vice verse, 1D.
     * @param data the double data to be shifted
     * @param bComplex true: complex; false: real
     * @param bSign true: fftshift; false: ifftshift
     * @return the fft shifted array
     */

    public static double []  fftshift(double [] data, boolean bComplex, boolean bSign)
    {
        double [] revan = new double [data.length];

        int step = 1;
        if (bComplex) step = 2;
        int len = data.length/step;
        int p = 0;
        if(bSign)
            p = (int) Math.ceil(len/2.0);
        else
            p = (int) Math.floor(len/2.0);

        int i=0;
        if (step==1){
            for (i=p;i<len;i++){
                revan[i-p] = data[i];
            }
            for (i=0;i<p;i++){
                revan[i+len-p] = data[i];
            }
        }
        else{
            for (i=p;i<len;i++){
                revan[(i-p)*2] = data[i*2];
                revan[(i-p)*2+1] = data[i*2+1];
            }
            for (i=0;i<p;i++){
                revan[(i+len-p)*2] = data[i*2];
                revan[(i+len-p)*2+1] = data[i*2+1];
            }
        }
        return revan;
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
