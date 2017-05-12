package com.example.guilherme.conectesp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class ProgressBarActivity extends AppCompatActivity {


    ListView wifiList;
    List<ScanResult> wifiScanList;
    int REQUEST_CODE = 101;
    WifiManager wifi;

    //Progress bar variables
    private static final int PROGRESS = 0x1;
    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    private ImageView doganimation;

    private Handler mHandler = new Handler();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingscreen);


        // Dog animation running in background
        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        doganimation = (ImageView) findViewById(R.id.doganimation);
        doganimation.setBackgroundResource(R.drawable.animationlist);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) doganimation.getBackground();
        // Start the animation (looped playback by default).
        frameAnimation.start();




        // Start lengthy operation in a background thread
        mProgress = (ProgressBar) findViewById(R.id.progressbar);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus < 100) {

                    mProgress.setProgress(mProgressStatus);
                    SystemClock.sleep(200);
                    mProgressStatus++;

                    if(mProgressStatus == 99){
                        nextScene();
                    }

                }
            }
        }).start();



        findAllWifi();


    }


    public void nextScene() {

        //chamar próxima activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }


    public void findAllWifi() {

        //wifiList = (ListView) findViewById(R.id.wifiList);

        //ImageView directionalBtn = (ImageView) findViewById(R.id.directBtn);

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
                    //wifiList.setAdapter(adapter);
                    adapter.add(wifiScanList.get(p).SSID);

                    adapter.notifyDataSetChanged();
                }
            }

        }

        mProgressStatus = 50;

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

        mProgressStatus = 50;
    }


    /*
    public void confirmarLigado(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adicione uma Tarefa");
        builder.setMessage("O que você precisa fazer?");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);


        builder.setPositiveButton("Adicionar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tarefa = inputField.getText().toString();
                        Log.d("MainActivity", tarefa);

                        helper = new titopetri.com.listadetarefas.db.TaskDBHelper(MainActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(titopetri.com.listadetarefas.db.TaskContract.Columns.TAREFA, tarefa);

                        db.insertWithOnConflict(titopetri.com.listadetarefas.db.TaskContract.TABLE, null, values,
                                SQLiteDatabase.CONFLICT_IGNORE);

                        updateUI();

                    }
                });

        builder.setNegativeButton("Cancelar",null);

        builder.create().show();
    }



    public void apagarItem(View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.textoCelula);
        String tarefa = taskTextView.getText().toString();

        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE,
                TaskContract.Columns.TAREFA,
                tarefa);


        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        updateUI();
    }

    */
}
