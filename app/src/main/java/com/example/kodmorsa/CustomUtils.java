package com.example.kodmorsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomUtils {
    private static <U> List<Map<U, Double>> peak_detection(List<Double> values, double delta, List<U> indices)
    {
//        assert(indices != null);
//        assert(values.size() != indices.size());

        Map<U, Double> maxima = new HashMap<U, Double>();
        Map<U, Double> minima = new HashMap<U, Double>();
        List<Map<U, Double>> peaks = new ArrayList<Map<U, Double>>();
        peaks.add(maxima);
        peaks.add(minima);

        Double maximum = null;
        Double minimum = null;
        U maximumPos = null;
        U minimumPos = null;

        boolean lookForMax = true;

//        int pos = 0;
        for (int i = 0; i < values.size(); i ++) {
            Double value = values.get(i);
            if (maximum == null || value > maximum) {
                maximum = value;
                maximumPos = indices.get(i);
            }

            if (minimum == null || value < minimum) {
                minimum = value;
                minimumPos = indices.get(i);
            }

            if (lookForMax) {
                if (value < maximum - delta) {
                    maxima.put(maximumPos, value);
                    minimum = value;
                    minimumPos = indices.get(i);
                    lookForMax = false;
                }
            } else {
                if (value > minimum + delta) {
                    minima.put(minimumPos, value);
                    maximum = value;
                    maximumPos = indices.get(i);
                    lookForMax = true;
                }
            }
        }
        return peaks;
    }

    public static List<Map<Integer, Double>> peak_detection(List<Double> values, double delta) {
        List<Integer> indices = new ArrayList<Integer>();
        for (int i=0; i<values.size(); i++) {
            indices.add(i);
        }

        return peak_detection(values, delta, indices);
    }
}
