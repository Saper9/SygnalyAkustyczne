package com.example.kodmorsa;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class HandleJson {
    private static final int MAX_TRY_COUNT = 10;
    private JSONObject resultJson;
    private Map<String, String> resultHashMap = new HashMap<>();
    private int attempts = 0;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private JSONObject getJSONObjectFromAssets(Activity activity, String fileName) {

        try {
            InputStream textInputStream = activity.getAssets().open(fileName);
            String json = IOUtils.toString(textInputStream, StandardCharsets.UTF_8);
            resultJson = new JSONObject(json);

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }

        return resultJson;
    }

    private Map<String, String> returnHashMap(Activity activity) {

        if (resultJson != null) {

            Iterator<String> keys = resultJson.keys();
            while (keys.hasNext()) {

                final String currentKey = keys.next();
                String currentValue = "";

                try {
                    currentValue = (String) resultJson.get(currentKey);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                resultHashMap.put(currentKey, currentValue);
            }

        } else {
            Toast.makeText(activity, R.string.error_no_json_toast, Toast.LENGTH_SHORT).show();
        }

        return resultHashMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    Map<String, String> getHashMapFromAssets(Activity activity, String fileName) {

        // to handle the possibility of some problems accessing the JSON file
        // we try x times to get the file and display a toast if it fails

        attempts = 0;

        while(attempts <= MAX_TRY_COUNT
                && resultJson == null){

            attempts++;
            getJSONObjectFromAssets(activity, fileName);
        }

        return returnHashMap(activity);
    }
}
