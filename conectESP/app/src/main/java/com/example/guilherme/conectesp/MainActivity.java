package com.example.guilherme.conectesp;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editIp;
    Button btnOn, btnOff;
    TextView textInfo1, textInfo2;
    ListView wifiList;
    List<ScanResult> wifiScanList;
    int REQUEST_CODE = 101;
    WifiManager wifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiList = (ListView) findViewById(R.id.wifiList);
        editIp = (EditText)findViewById(R.id.ip);
        btnOn = (Button)findViewById(R.id.bon);
        btnOff = (Button)findViewById(R.id.boff);
        textInfo1 = (TextView)findViewById(R.id.info1);
        textInfo2 = (TextView)findViewById(R.id.info2);

        ArrayList<String> listItems=new ArrayList<String>();
        ArrayAdapter<String> adapter;

        //configuração de wifi
        wifi = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true); // Liga o WiFi



        //se wifi ligado
        if (wifi.isWifiEnabled()) {

            WifiScanReceiver wifiReciever = new WifiScanReceiver(wifi);
            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_CODE);

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //your code that requires permission
                wifi.startScan();
                wifiScanList = wifi.getScanResults();

                if(wifiScanList.size() > 0)
                    Toast.makeText(this, "Redes disponíveis!", Toast.LENGTH_LONG).show();


                for(int p=0; p < wifiScanList.size(); p++) {

                    adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1,
                            listItems);
                    wifiList.setAdapter(adapter);
                    adapter.add(wifiScanList.get(p).SSID);

                    adapter.notifyDataSetChanged();
                }
            }

        }




        btnOn.setOnClickListener(btnOnOffClickListener);
        btnOff.setOnClickListener(btnOnOffClickListener);



    }


    View.OnClickListener btnOnOffClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            String onoff;
            if(v==btnOff){
                onoff="off";
            }else{
                onoff="on";
            }

            //btnOn.setEnabled(false);
            //btnOff.setEnabled(false);

            String serverIP = editIp.getText().toString()+":80";

            TaskEsp taskEsp = new TaskEsp(serverIP);
            taskEsp.execute(onoff);

            conectWifi();

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



    //public void conectWifi(AdapterView network, View arg1, int arg2, long arg3) {
    public void conectWifi() {

        WifiConfiguration wifiConfiguration = new WifiConfiguration();


        //Configuro uma rede baseada nos dados encontrados.
        for(int p=0; p < wifiScanList.size(); p++) {

            System.out.println(wifiScanList.get(p).SSID);

            if(wifiScanList.get(p).SSID.toString().equals("Magicball 98BD Network")){

                wifiConfiguration.BSSID = wifiScanList.get(p).BSSID;
                wifiConfiguration.SSID = "\"" + wifiScanList.get(p).SSID + "\"";
                wifiConfiguration.preSharedKey = "\"magicball\"";
                //wifiConfiguration.SSID = wifiScanList.get(p).SSID;
                //wifiConfiguration.preSharedKey = "magicball";

            }
        }

        System.out.println("Conectando ...  " + wifiConfiguration.SSID);

        //Conecta na rede criada.
        WifiManager wifiManager = wifi;
        int netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.saveConfiguration();
        //wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

}
