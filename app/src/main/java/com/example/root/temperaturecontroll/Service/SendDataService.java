package com.example.root.temperaturecontroll.Service;

import android.util.Log;

import com.example.root.temperaturecontroll.Credentials;
import com.example.root.temperaturecontroll.Variables;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SendDataService {
    private static final String TAG = "SendDataService";

    public static String sendPostRequest(String urlAddress, HashMap<String, String> data) throws IOException {

        URL url;
        try {
            url = new URL(urlAddress);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + urlAddress);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();

        HttpURLConnection httpURLConnection;
        Log.e("URL", "> " + url);
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setFixedLengthStreamingMode(bytes.length);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        OutputStream out = httpURLConnection.getOutputStream();
        out.write(bytes);
        out.close();
        return Credentials.getStatus(httpURLConnection,TAG, Variables.FAILURE);

    }
}
