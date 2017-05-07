package com.example.root.temperaturecontroll.Service;

import android.util.Log;

import com.example.root.temperaturecontroll.Credentials;
import com.example.root.temperaturecontroll.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by root on 04.04.17.
 */

public class SendJsonService {
    private static final String TAG = "SendJsonService";
    public static Boolean sendJsonPostRequest(String urlAddress, HashMap<String, String> data) {
        try {
            URL url = new URL(urlAddress);

            HttpURLConnection httpURLConnection = null;
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.connect();

            JSONArray jsonArray = new JSONArray();
            Iterator<Map.Entry<String, String>> iterator = data.entrySet().iterator();
            while (iterator.hasNext()) {
                JSONObject object = new JSONObject();
                Map.Entry<String, String> param = iterator.next();
                object.put("Hour",param.getKey());
                object.put("Temperature",param.getValue());
                jsonArray.put(object);

            }
            JSONObject holder = new JSONObject();
            holder.put("array", jsonArray);
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(holder.toString());
            wr.flush();
            wr.close();
            String result = Credentials.getStatus(httpURLConnection,TAG, Variables.FAILURE);
            Log.i("Result",result);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
