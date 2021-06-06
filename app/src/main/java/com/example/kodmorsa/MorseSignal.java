package com.example.kodmorsa;

public class MorseSignal {
    public int length, checkStateInterval;
    public long frame_start, frame_end;
    public boolean isSignal, isSilence;

    public MorseSignal(int checkStateInterval) {
        this.frame_start = 0;
        this.frame_end = 0;
        this.length = 0;
        this.checkStateInterval = checkStateInterval;
        this.isSignal = false;
        this.isSilence = false;
    }

    public int length_ms() {
        return this.length * this.checkStateInterval;
    }
}
