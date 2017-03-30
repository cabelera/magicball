package com.example.guilherme.conectesp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    EditText editIp;
    Button btnOn, btnOff;
    TextView textInfo1, textInfo2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        editIp = (EditText)findViewById(R.id.ip);
        btnOn = (Button)findViewById(R.id.bon);
        btnOff = (Button)findViewById(R.id.boff);
        textInfo1 = (TextView)findViewById(R.id.info1);
        textInfo2 = (TextView)findViewById(R.id.info2);

        btnOn.setOnClickListener(btnOnOffClickListener);
        btnOff.setOnClickListener(btnOnOffClickListener);



    }


    View.OnClickListener btnOnOffClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String onoff;
            if(v==btnOn){
                onoff="off";
            }else{
                onoff="on";
            }

            //btnOn.setEnabled(false);
            //btnOff.setEnabled(false);

            String serverIP = editIp.getText().toString()+":80";

            TaskEsp taskEsp = new TaskEsp(serverIP);
            taskEsp.execute(onoff);

        }
    };


    private class TaskEsp extends AsyncTask<String, Void, String> {

        String server;

        TaskEsp(String server){
            this.server = server;
        }

        @Override
        protected String doInBackground(String... params) {

            String val = params[0];
            HttpURLConnection urlConnection = null;
            final String p = "http://"+server+"/error?luz="+val;
            StringBuilder serverResponse = null;


            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    textInfo1.setText(p);
                }
            });


            try {
                URL url = new URL(p);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                serverResponse = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    serverResponse.append(line);
                }

                if(serverResponse != null)
                    System.out.println(serverResponse.toString());

                in.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return "foi";
        }

        @Override
        protected void onPostExecute(String s) {
            textInfo2.setText(s);
            //btnOn.setEnabled(true);
            //btnOff.setEnabled(true);
        }
    }




}
