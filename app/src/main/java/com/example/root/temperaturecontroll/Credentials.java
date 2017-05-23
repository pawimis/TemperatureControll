package com.example.root.temperaturecontroll;

import android.util.Log;

import com.example.root.temperaturecontroll.Database.DbTemperature;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import static android.content.ContentValues.TAG;

public class Credentials {
    public static void processJSONandInsertToDB(JSONArray jsonArray, DbTemperature dbTemperature) throws JSONException {
        if(jsonArray != null) {
            Log.i(TAG, String.valueOf(jsonArray));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.i(TAG, String.valueOf(jsonObject));
                dbTemperature.insertRecord(jsonObject.getString("ID"),
                        jsonObject.getString("ROOM"),
                        jsonObject.getString("DATE"),
                        jsonObject.getString("TIME"),
                        jsonObject.getString("TEMPERATURE"),
                        jsonObject.getString("CONTROLLER"));
            }
        }
    }
    public static String getStatus(HttpURLConnection httpURLConnection,String TAG,String FAILURE) throws IOException {
        int status = httpURLConnection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("Post failed with error code " + status);

        }
        if (httpURLConnection != null) {
        InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
        StringBuilder stringBuilder=  new StringBuilder();
        while((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        String response = stringBuilder.toString();
        inputStream.close();
        Log.i(TAG, response);
        httpURLConnection.disconnect();
        return response;
    } else {
        Log.i(TAG, FAILURE);
        return FAILURE;
    }
    }
}
