package com.example.guilherme.conectesp;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lsitec on 03/05/17.
 */


public class ESPControler extends AsyncTask<String, Void, String> {

    String server;

    ESPControler(String server){
        this.server = server;
    }

    @Override
    protected String doInBackground(String... params) {

        String val = params[0];
        HttpURLConnection urlConnection = null;
        final String p = "http://"+server+"/error?luz="+val;
        StringBuilder serverResponse = null;


        /*
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                //textInfo1.setText(p);
            }
        });
        */

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
        //textInfo2.setText(s);
        //btnOn.setEnabled(true);
        //btnOff.setEnabled(true);
    }
}