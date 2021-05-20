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
            this.resultJson = new JSONObject(json);

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }

        return this.resultJson;
    }

    private Map<String, String> returnHashMap(Activity activity) {

        if (this.resultJson != null) {

            Iterator<String> keys = this.resultJson.keys();
            while (keys.hasNext()) {

                final String currentKey = keys.next();
                String currentValue = "";

                try {
                    currentValue = (String) this.resultJson.get(currentKey);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                this.resultHashMap.put(currentKey, currentValue);
            }

        } else {
            Toast.makeText(activity, R.string.error_no_json_toast, Toast.LENGTH_SHORT).show();
        }

        return this.resultHashMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    Map<String, String> getHashMapFromAssets(Activity activity, String fileName) {
        this.attempts = 0;

        while(this.attempts <= MAX_TRY_COUNT
                && resultJson == null){

            this.attempts++;
            getJSONObjectFromAssets(activity, fileName);
        }

        return returnHashMap(activity);
    }
}
