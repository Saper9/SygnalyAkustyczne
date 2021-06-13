package com.example.kodmorsa;
import org.apache.commons.math3.complex.Complex;
import org.jtransforms.fft.DoubleFFT_1D;
public class FFT {

    public static com.example.kodmorsa.Complex[] fft1D(com.example.kodmorsa.Complex[] signal){
        int n = signal.length;
        com.example.kodmorsa.Complex[] fourier = new com.example.kodmorsa.Complex[n];

        double[] coeff = new double[2*n];
        int i = 0;
        for(com.example.kodmorsa.Complex c:signal){
            coeff[i++] = c.getReal();
            coeff[i++] = c.getImaginary();
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.complexForward(coeff);

        for(i = 0; i < 2*n; i+=2){
            com.example.kodmorsa.Complex c    = new com.example.kodmorsa.Complex(coeff[i], coeff[i+1]);
            fourier[i/2] = c;
        }
        return fourier;
    }


    public static com.example.kodmorsa.Complex[] fftShift1D(com.example.kodmorsa.Complex[] fTransform){
        int n   = fTransform.length;
        int mid = (n-1)/2;
        com.example.kodmorsa.Complex[] shift = new com.example.kodmorsa.Complex[n];
        int j = 0;
        for(int i = mid+1; i < n; i++){
            shift[j] = fTransform[i];
            j++;
        }

        for(int i = 0; i <= mid; i++){
            shift[j] = fTransform[i];
            j++;
        }
        return shift;
    }

    public static com.example.kodmorsa.Complex[] ifft1D(com.example.kodmorsa.Complex[] fourier){
        int n    = fourier.length;
        double s = 1.0 / (double) n;

        com.example.kodmorsa.Complex[] signal = new com.example.kodmorsa.Complex[n];
        double[] coeff   = new double[2*n];

        int i = 0;
        for(com.example.kodmorsa.Complex c:fourier){
            coeff[i++] = c.getReal();
            coeff[i++] = c.getImaginary();
        }

        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.complexInverse(coeff, false);

        for(i = 0; i < 2*n; i+=2){
            com.example.kodmorsa.Complex c    = new com.example.kodmorsa.Complex(s*coeff[i], s*coeff[i+1]);
            signal[i/2] = c;
        }
        return signal;
    }


    public static com.example.kodmorsa.Complex[] ifftShift1D(com.example.kodmorsa.Complex[] fourier){
        int n   = fourier.length;
        int mid = n/2;
        com.example.kodmorsa.Complex[] shift = new com.example.kodmorsa.Complex[n];
        int j = 0;
        for(int i = mid; i < n; i++){
            shift[j] = fourier[i];
            j++;
        }

        for(int i = 0; i < mid; i++){
            shift[j] = fourier[i];
            j++;
        }
        return shift;
    }
}