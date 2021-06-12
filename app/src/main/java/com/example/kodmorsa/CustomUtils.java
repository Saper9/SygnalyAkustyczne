package com.example.kodmorsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomUtils {
    private static <U> List<Map<U, Float>> peak_detection(List<Float> values, Float delta, List<U> indices)
    {
//        assert(indices != null);
//        assert(values.size() != indices.size());

        Map<U, Float> maxima = new HashMap<U, Float>();
        Map<U, Float> minima = new HashMap<U, Float>();
        List<Map<U, Float>> peaks = new ArrayList<Map<U, Float>>();
        peaks.add(maxima);
        peaks.add(minima);

        Float maximum = null;
        Float minimum = null;
        U maximumPos = null;
        U minimumPos = null;

        boolean lookForMax = true;

        int pos = 0;
        for (Float value : values) {
            if (maximum == null || value > maximum) {
                maximum = value;
                maximumPos = indices.get(pos);
            }

            if (minimum == null || value < minimum) {
                minimum = value;
                minimumPos = indices.get(pos);
            }

            if (lookForMax) {
                if (value < maximum - delta) {
                    maxima.put(maximumPos, value);
                    minimum = value;
                    minimumPos = indices.get(pos);
                    lookForMax = false;
                }
            } else {
                if (value > minimum + delta) {
                    minima.put(minimumPos, value);
                    maximum = value;
                    maximumPos = indices.get(pos);
                    lookForMax = true;
                }
            }

            pos++;
        }

        return peaks;
    }

    public static List<Map<Integer, Float>> peak_detection(List<Float> values, Float delta) {
        List<Integer> indices = new ArrayList<Integer>();
        for (int i=0; i<values.size(); i++) {
            indices.add(i);
        }

        return peak_detection(values, delta, indices);
    }
}
