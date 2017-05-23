package com.example.root.temperaturecontroll.Service;

import com.example.root.temperaturecontroll.Variables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetDataService {
    private static final String TAG = "GetDataService";
    private static final String FAILURE = "Failure";

    public static String sendRequest(String urlAddress) throws IOException {
        HttpURLConnection httpUrlConnection;
        URL url;
        try{
             url = new URL(urlAddress);
        } catch (MalformedURLException e) {
        throw new IllegalArgumentException("invalid url: " + urlAddress);
        }
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setReadTimeout(Variables.READ_TIMEOUT);
            httpUrlConnection.setConnectTimeout(Variables.CONNECTION_TIMEOUT);
            httpUrlConnection.setRequestMethod("POST");

            // setDoInput and setDoOutput to true as we send and recieve data
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setDoOutput(true);

            // add parameter to our above url

            OutputStream os = httpUrlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.flush();
            writer.close();
            os.close();
            httpUrlConnection.connect();

        } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return e.toString();
        }

        try {

            int response_code = httpUrlConnection.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = httpUrlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                line = result.toString();
                if(line.length() == 1 || line.equals(" ")){
                    line = null;
                }

                // Pass data to onPostExecute method
                return line;

            } else {
                return FAILURE;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            httpUrlConnection.disconnect();
        }

    }
}

