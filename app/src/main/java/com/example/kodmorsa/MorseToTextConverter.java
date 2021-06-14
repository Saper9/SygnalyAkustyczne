package com.example.kodmorsa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MorseToTextConverter {
    private WavFile wavFile;

    private int numChannels,
            sampleRate,
            framesRead,
            mediumSignalValue,
            checkStateInterval,
            mediumSilenceValue;
    private double minSampleValue;
    private double[] buffer;

    private String decodedMorseResult;

    public MorseToTextConverter(String filename) throws WavFileException, IOException {
        this.wavFile = WavFile.openWavFile(new File(filename));
        this.numChannels = this.wavFile.getNumChannels();
        this.checkStateInterval = 10;
        this.minSampleValue = 0.01;
        this.sampleRate = ((int) wavFile.getSampleRate() / 1000) * checkStateInterval;
        this.buffer = new double[this.sampleRate * this.numChannels];
    }

    public void close() throws IOException {
        this.wavFile.close();
    }

    public void executeTranslation() throws WavFileException, IOException {
        MorseCodeDict morseCodeDictionary = new MorseCodeDict();
        List<MorseSignal> allAvailableSignals = this.getSignals();
        List<String> morseMessageAssembled = this.assemble(allAvailableSignals);

        Iterator<String> wordIterator = morseMessageAssembled.iterator();
        String originalMorseMessage = "";
        String translatedMorseMessage = "";

        while (wordIterator.hasNext()) {

            String morseMessageWord = wordIterator.next();

            if (morseMessageWord.equals("LETTER_SPACE")) {

                originalMorseMessage += " ";
                translatedMorseMessage += "";

            } else if (morseMessageWord.equals("WORD_SPACE")) {

                originalMorseMessage += "   ";
                translatedMorseMessage += " ";

            } else {
                originalMorseMessage += morseMessageWord;
                translatedMorseMessage += morseCodeDictionary.translate(morseMessageWord);
            }

        }

        System.out.println("MORSE MESSAGE: " + originalMorseMessage);
        System.out.println("TRANSLATED MESSAGE: " + translatedMorseMessage);

        this.decodedMorseResult = translatedMorseMessage;

//      Close wav file
        this.close();
    }

    public List<MorseSignal> getSignals() throws WavFileException, IOException {
        List<MorseSignal> availableSignals = new ArrayList<>();
        boolean silenceInTheBeginning = false;

        while (this.wavFile.getFramesRemaining() > 0) {
            MorseSignal nextSignal = this.readNextSignal();
            if (!silenceInTheBeginning && nextSignal.silence && availableSignals.isEmpty()) {
                silenceInTheBeginning = true;
                nextSignal.length = 1;
                continue;
            }
            availableSignals.add(nextSignal);
        }
        return availableSignals;
    }

    public MorseSignal readNextSignal() throws WavFileException, IOException {
        MorseSignal signal = new MorseSignal(this.checkStateInterval);
        signal.startFrame = this.wavFile.getFrameAlreadyRead();

        float sample = this.readBufferAudioSample();

        signal.length++; //1 = 10ms one unit of a signal

//        this threshold let us know if it is a valid signal or silence
        if (sample > minSampleValue) {
            signal.signal = true;
        } else {
            signal.silence = true;
        }

        while (this.wavFile.getFramesRemaining() > 0) {

            float sampleReadBuffer = readBufferAudioSample(); // read next chunk of audio

            signal.length++; // increase when read next chunk of audio

//          Determine next steps by checking if is silence or signal and above or under min value presented by valid signal
            if ((signal.silence && sampleReadBuffer > minSampleValue)
                    || (signal.signal && sampleReadBuffer <= minSampleValue)) {

                signal.length--;
                signal.endFrame = this.wavFile.getFrameAlreadyRead() - this.sampleRate;

                return signal;
            }
        }

        if (this.wavFile.getFramesRemaining() <= 0) {
            signal.endFrame = this.wavFile.getFrameAlreadyRead();
        }

        return signal;
    }

    private float readBufferAudioSample() throws WavFileException, IOException {
        if (this.wavFile.getFramesRemaining() > 0) {
            List<Float> validSamples = new ArrayList<>();
            this.framesRead = wavFile.readFrames(this.buffer, this.sampleRate);
            if (this.framesRead != 0) {
                for (int s = 0; s < this.framesRead * this.numChannels; s++) {
                    if (this.buffer[s] > 0) {
                        validSamples.add((float) this.buffer[s]);
                    }
                }
                if (validSamples.isEmpty()) {
                    validSamples.add((float) 0);
                }
                return this.getAverageValueInList(validSamples);
            }
        }

        return (float) 0;
    }

    private float getAverageValueInList(List<Float> list) {
        float sum = 0;

        for (float value : list) {
            sum += value;
        }

        return ((float) (sum / list.size()));
    }

    private List<String> assemble(List<MorseSignal> signals) {
        int signalFullValue = 0;
        int silenceFullValue = 0;
        Iterator<MorseSignal> soundOrSilenceIterator = signals.iterator();
        Iterator<MorseSignal> morseIterator = signals.iterator();
        List<Integer> presentSignalsLengths = new ArrayList<>();
        List<Integer> presentSilencesLengths = new ArrayList<>();

//        Check is morse signal a silence or morse signal value
        while (soundOrSilenceIterator.hasNext()) {

            MorseSignal signal = soundOrSilenceIterator.next();

            if (!signal.silence && !this.seek(presentSignalsLengths, (signal.length * checkStateInterval))) {
                presentSignalsLengths.add(signal.length * checkStateInterval);
                signalFullValue += signal.length * checkStateInterval;
            } else if (signal.silence && !this.seek(presentSignalsLengths, (signal.length * checkStateInterval))) {
                presentSilencesLengths.add(signal.length * checkStateInterval);
                silenceFullValue += signal.length * checkStateInterval;
            }

        }
//        Calculate medium values
        this.mediumSignalValue = (int) signalFullValue / presentSignalsLengths.size();
        this.mediumSilenceValue = (int) silenceFullValue / presentSilencesLengths.size();

        List<String> codeWordString = new ArrayList<>();
        String morseWord = "";

        while (morseIterator.hasNext()) {

            MorseSignal signal = morseIterator.next();

            if (signal.signal) {
//              if value is signal and its average length is less than medium we know that is is a dot
                if (signal.length_ms() < this.mediumSignalValue) {
                    morseWord += ".";
//              if value is signal and its average length is higher than medium we know that is is a dash
                } else {
                    morseWord += "-";
                }

            } else {
                // check if is a silence between words
                if (signal.length_ms() >= (2 * this.mediumSilenceValue)) {

                    codeWordString.add(morseWord);
                    codeWordString.add("WORD_SPACE");
                    morseWord = "";
                    continue;
                }

                // check if silence is between letters
                if (signal.length_ms() >= this.mediumSilenceValue) {

                    codeWordString.add(morseWord);
                    codeWordString.add("LETTER_SPACE");
                    morseWord = "";
                    continue;
                }
            }

        }
        if (!morseWord.isEmpty()) {
            codeWordString.add(morseWord);
        }
        return codeWordString;
    }

    public boolean seek(List<Integer> values, int value) {
        Iterator<Integer> i = values.iterator();

        while (i.hasNext()) {
            if (i.next() == value) {
                return true;
            }
        }

        return false;

    }

    public WavFile getWavFile() {
        return wavFile;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getFramesRead() {
        return framesRead;
    }

    public int getMediumSignalValue() {
        return mediumSignalValue;
    }

    public int getCheckStateInterval() {
        return checkStateInterval;
    }

    public int getMediumSilenceValue() {
        return mediumSilenceValue;
    }

    public double getMinSampleValue() {
        return minSampleValue;
    }

    public double[] getBuffer() {
        return buffer;
    }

    public String getDecodedMorseResult() {
        return decodedMorseResult;
    }

    public void displayInfo() {
        System.out.print(this.wavFile.getInfo());
    }

    public String result() {
        return this.decodedMorseResult;
    }

    public String toString() {
        return this.wavFile.getInfo();
    }
}
