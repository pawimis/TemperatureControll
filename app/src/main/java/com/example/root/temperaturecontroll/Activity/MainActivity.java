package com.example.root.temperaturecontroll.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.root.temperaturecontroll.Credentials;
import com.example.root.temperaturecontroll.R;
import com.example.root.temperaturecontroll.Service.SendDataService;
import com.example.root.temperaturecontroll.Variables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    Button connectButton;
    EditText usernameEditText;
    EditText passwordEditText;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectButton = (Button) findViewById(R.id.activity_main_connect_button);
        usernameEditText = (EditText) findViewById(R.id.activity_main_editText_username);
        passwordEditText = (EditText) findViewById(R.id.activity_main_editText_password);
        startMainMenuActivity();
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //UserTask userTask = new UserTask();
                //userTask.execute(usernameEditText.getText().toString(),passwordEditText.getText().toString());


            }
        });
    }
    private void startMainMenuActivity() {
        Log.i(TAG,"starting main Activity");
        Intent intent = new Intent(MainActivity.this,MenuActivity.class);
        startActivity(intent);
    }
    private class UserTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String,String> map = new HashMap<>();
            String path,res = "failure";
            int size = strings.length;
            path = Variables.SERVER_BASE + Variables.CREATE_USER;
                map.put("user_login", strings[0]);
                map.put("password", strings[1]);
            try {
                res = SendDataService.sendPostRequest(path, map);
            }catch (IOException e){e.printStackTrace();}
            return res;
        }

        @Override
        protected void onPreExecute() {
            progressBar = ProgressDialog.show(MainActivity.this,"Processing",null,true,true);
        }
        @Override
        protected void onPostExecute(String strings) {
            Log.i(TAG,"post Execute " + strings);
            progressBar.hide();
            Toast.makeText(getApplicationContext(),strings,Toast.LENGTH_LONG).show();
            Log.i(TAG,strings);
            if(strings.length()!= 0) {
                startMainMenuActivity();
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.hide();
        }
    }


}
