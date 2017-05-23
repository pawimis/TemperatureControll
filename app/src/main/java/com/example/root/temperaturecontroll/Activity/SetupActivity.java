package com.example.root.temperaturecontroll.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.temperaturecontroll.R;
import com.example.root.temperaturecontroll.Service.SendDataService;
import com.example.root.temperaturecontroll.Variables;

import java.io.IOException;
import java.util.HashMap;

public class SetupActivity extends Activity {
    private static String TAG = "SetupActivity";
    EditText ssidText;
    EditText passwordText;
    EditText roomText;
    EditText controllerText;
    Button sendButton;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ssidText = (EditText) findViewById(R.id.activity_setup_ssid);
        passwordText = (EditText) findViewById(R.id.activity_setup_password);
        roomText = (EditText) findViewById(R.id.activity_setup_room_name);
        controllerText = (EditText) findViewById(R.id.activity_setup_controller_name);
        sendButton = (Button) findViewById(R.id.activity_setup_button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSendButtonClick();
            }
        });

    }

    private void processSendButtonClick() {
        if (!dataEmpty()) {
            UserTask userTask = new UserTask();
            userTask.execute(ssidText.getText().toString()
                    , passwordText.getText().toString()
                    , roomText.getText().toString()
                    , controllerText.getText().toString());
        }
    }

    private boolean dataEmpty() {
        return ssidText.getText().toString().isEmpty()
                && passwordText.getText().toString().isEmpty()
                && roomText.getText().toString().isEmpty()
                && controllerText.getText().toString().isEmpty();
    }

    private void startMenuActivity() {
        Intent intent = new Intent(SetupActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    private class UserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> map = new HashMap<>();
            String res = "failure";
            map.put("ssid", strings[0]);
            map.put("password", strings[1]);
            map.put("room", strings[2]);
            map.put("controller", strings[3]);
            map.put("send", "SEND");
            try {
                res = SendDataService.sendPostRequest(Variables.SETUP_PATH, map, "GET");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPreExecute() {
            progressBar = ProgressDialog.show(SetupActivity.this, "Processing", null, true, true);
        }

        @Override
        protected void onPostExecute(String strings) {
            Log.i(TAG, "post Execute " + strings);
            progressBar.hide();
            Toast.makeText(getApplicationContext(), strings, Toast.LENGTH_LONG).show();
            Log.i(TAG, strings);
            if (strings.length() != 0) {
                startMenuActivity();
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.hide();
        }
    }
}
