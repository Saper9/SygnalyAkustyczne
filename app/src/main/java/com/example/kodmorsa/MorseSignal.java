package com.example.kodmorsa;

public class MorseSignal {
    public int length, checkStateInterval;
    public long startFrame, endFrame;
    public boolean signal, silence;

    public MorseSignal(int checkStateInterval) {
        this.startFrame = 0;
        this.endFrame = 0;
        this.length = 0;
        this.checkStateInterval = checkStateInterval;
        this.signal = false;
        this.silence = false;
    }

    public int length_ms() {
        return this.length * this.checkStateInterval;
    }
}
