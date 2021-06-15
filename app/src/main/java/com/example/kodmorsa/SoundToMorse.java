package com.example.kodmorsa;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.io.*;
import java.util.*;


public class SoundToMorse extends AppCompatActivity {
    private MediaRecorder recorder;
    private MediaPlayer player;

    Button spectralDemorse;
    Button averageDemorse;

    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile("TempFile", ".wav");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void averageMorse() throws IOException, com.example.kodmorsa.WavFileException, FileFormatNotSupportedException, WavFileException {
        InputStream inp = getAssets().open("please give me 3_no_noise.wav");
        File file = stream2file(inp);
        MorseToTextConverter morseToTextConverter = new MorseToTextConverter(file.toString());
        morseToTextConverter.executeTranslation();
        TextView textView = findViewById(R.id.decodedTextField);
        textView.setText(morseToTextConverter.result());
        Log.i("File info: ", morseToTextConverter.toString());
        inp.close();

        int defaultSampleRate = -1;        //-1 value implies the method to use default sample rate
        int defaultAudioDuration = -1;    //-1 value implies the method to process complete audio duration
        JLibrosa jLibrosa = new JLibrosa();
        float[] audioFeatureValues = jLibrosa.loadAndRead(file.toString(), defaultSampleRate, defaultAudioDuration);
        double[] audioValues = convertFloatsToDoubles(audioFeatureValues);

        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.line();
        cartesian.title("Original signal");
        cartesian.yAxis(0).title("Value");
        cartesian.xAxis(0).title("Sample");
        List<DataEntry> seriesData = new ArrayList<>();
        for (int i = 0; i < audioValues.length; i++) {
            seriesData.add(new CustomDataEntry(i, audioValues[i]));
        }
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Line series1 = cartesian.line(series1Mapping);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void originalSignal() throws IOException, WavFileException, FileFormatNotSupportedException, com.example.kodmorsa.WavFileException, InterruptedException {
        InputStream inp = getAssets().open("sos.wav");
        File file = stream2file(inp);
        MorseToTextConverter morseToTextConverter = new MorseToTextConverter(file.toString());
        inp.close();


        int defaultSampleRate = -1;        //-1 value implies the method to use default sample rate
        int defaultAudioDuration = -1;    //-1 value implies the method to process complete audio duration
        JLibrosa jLibrosa = new JLibrosa();
        float[] audioFeatureValues = jLibrosa.loadAndRead(file.toString(), defaultSampleRate, defaultAudioDuration);
        double[] audioValues = convertFloatsToDoubles(audioFeatureValues);

        setContentView(R.layout.chart_layout);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.line();
        cartesian.title("Original signal");
        cartesian.yAxis(0).title("Value");
        cartesian.xAxis(0).title("Sample");
        List<DataEntry> seriesData = new ArrayList<>();
        for (int i = 0; i < audioValues.length; i++) {
            seriesData.add(new CustomDataEntry(i, audioValues[i]));
        }
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Line series1 = cartesian.line(series1Mapping);
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
        spectri(audioValues, (int) morseToTextConverter.getWavFile().getSampleRate(), 400, 600, 50);
    }

    public static double[] convertFloatsToDoubles(float[] input) {
        if (input == null) {
            return null; // Or throw an exception - your choice
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    /**
     * @param data    - Signal data
     * @param Fs      - Sampling frequency
     * @param start_f - start frequency to plot
     * @param stop_f  - stop frequency to plot
     * @param delta   - A point is considered a maximum peak if it has the maximal value, and was preceded (to the left) by a value lower by delta
     */
    private void spectri(double[] data, double Fs, int start_f, int stop_f, double delta) {
//        get data length
        int N = data.length;
//        cast data to Complex (will be needed in FFT)
        Complex[] dataComplex = new Complex[data.length];
        for (int i = 0; i < data.length; i++) {
            dataComplex[i] = new Complex(data[i], 0);
        }
//        Calculate FFT
        Complex[] fftNew = FFT.fft1D(dataComplex);

        double df = Fs / N; // Frequency bin size
        double minf = -Fs / 2;
        double maxf = Fs / 2 - df;
        int i = (int) Math.round(N / 2 + (start_f * N / 2) / (Fs / 2)); // start index of frequency range
        int j = (int) Math.round(N / 2 + (stop_f * N / 2) / (Fs / 2)); // stop index of frequency range

        // Frequency axis
        int howMany = (int) Math.ceil((maxf - minf) / df);
        List<Double> f = new ArrayList<>(howMany);
        double k = minf;
        while (k <= maxf) {
            f.add(k);
            k += df;
        }
        //
//        Calculate FFTShift
        Complex[] fftShift = FFT.fftShift1D(fftNew);
        List<Double> y = new ArrayList<Double>(fftNew.length);
        for (int l = 0; l < fftNew.length; l++) {
            double test = Math.abs(fftShift[l].Abs());
//            dB magnitude
            y.add(20 * Math.log10(test));
        }

//        setContentView(R.layout.chart_layout);
//        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
//        Cartesian cartesian = AnyChart.line();
//        cartesian.title("Spectrum");
//        cartesian.yAxis(0).title("Volume [dB]");
//        cartesian.xAxis(0).title("Frequency [Hz]");
//        List<DataEntry> seriesData = new ArrayList<>();
//        for (int p = i; p < j; p++) {
//            seriesData.add(new CustomDataEntry(f.get(p), y.get(p)));
//        }
//        Set set = Set.instantiate();
//        set.data(seriesData);
//        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
//        Line series1 = cartesian.line(series1Mapping);
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

//        Detect max peaks
        List<Double> findPeaksArray = y.subList(i, j);
        List<Map<Integer, Double>> peaks = CustomUtils.peak_detection(findPeaksArray, delta);
        Map<Integer, Double> maxPeaks = peaks.get(0);
        List<Double> peaksFrequencies = new ArrayList<>(maxPeaks.size());
        for (Integer key : maxPeaks.keySet()) {
            peaksFrequencies.add(f.get(key + i));
        }

        List<Double> recreatedSignal = new ArrayList<>();
        recreatedSignal = mfilter(data, 20, Fs, peaksFrequencies.get(0));

        setContentView(R.layout.chart_layout);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        Cartesian cartesian = AnyChart.line();
        cartesian.title("Recreated signal");
        cartesian.yAxis(0).title("value");
        cartesian.xAxis(0).title("sample");
        List<DataEntry> seriesData = new ArrayList<>();
        for (int p = 0; p < recreatedSignal.size(); p++) {
            seriesData.add(new CustomDataEntry(p, recreatedSignal.get(p)));
        }
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Line series1 = cartesian.line(series1Mapping);
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


    /**
     * @param x                  audio signal (read from wavfile)
     * @param speed              morse code speed in WPM
     * @param Fs                 sampling frequency
     * @param morseCodeFrequency morse code audio frequency
     * @return
     */
    public List<Double> mfilter(double[] x, int speed, double Fs, double morseCodeFrequency) {
        double dot_time = 1.2 / speed;
        int x_len = x.length;
        double[] t = new double[(int) (dot_time * Fs)];
        double sum = 0;
        for (int i = 0; i < t.length; i++) {
            t[i] = sum;
            sum += 1 / Fs;
        }

        double[] burst = new double[t.length];
        for (int i = 0; i < burst.length; i++) {
            burst[i] = Math.sin(2 * Math.PI * morseCodeFrequency * t[i]);
        }
        int N = burst.length;

        List<Double> x_f1 = new ArrayList<>(x_len - N);
        for (int i = 0; i < x_len - N; i++) {
            double[] xk = new double[N];
            System.arraycopy(x, i, xk, 0, N);
            double dotProduct = 0;
            for (int l = 0; l < N; l++) {
                dotProduct += burst[l] * xk[l];
            }
            x_f1.add(dotProduct);
        }
        return x_f1;
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_to_morse);
        spectralDemorse = findViewById(R.id.listeningButton);
        averageDemorse = findViewById(R.id.stopButton);


        spectralDemorse.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    originalSignal();
                } catch (IOException | WavFileException | FileFormatNotSupportedException | com.example.kodmorsa.WavFileException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        averageDemorse.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    averageMorse();
                } catch (IOException | com.example.kodmorsa.WavFileException | FileFormatNotSupportedException | WavFileException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
