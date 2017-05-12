package com.example.guilherme.conectesp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsitec on 03/05/17.
 */
@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends FragmentActivity//AppCompatActivity
                          implements NavigationView.OnNavigationItemSelectedListener {

    EditText editIp;
    Button btnOn, btnOff;
    TextView textProgressBar, textInfo2;

    private ProgressBar mProgress;
    private int mProgressStatus = 0;


    //wifi
    ListView wifiList;
    List<ScanResult> wifiScanList;
    int REQUEST_CODE = 101;
    WifiManager wifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton btnMenu = (ImageButton) findViewById(R.id.tooglebtn);
        btnMenu.setOnClickListener(onMenuClickListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //editIp = (EditText)findViewById(R.id.ip);
        //btnOn = (Button)findViewById(R.id.bon);
        //btnOff = (Button)findViewById(R.id.boff);
        textProgressBar = (TextView)findViewById(R.id.textProgressBar);
        //textInfo2 = (TextView)findViewById(R.id.info2);

        // Start lengthy operation in a background thread
        mProgress = (ProgressBar) findViewById(R.id.progressbar);

        //SystemClock.sleep(6000);

        //startApplication();

        //btnOn.setOnClickListener(btnOnOffClickListener);
        //btnOff.setOnClickListener(btnOnOffClickListener);


    }


    public void showWifiDialog() {

        List<ScanResult> wifiList = showWifi();
        CharSequence[] listItems;

        if(wifiList != null && wifiList.size() != 0) {
            System.out.println("Entrei wifi 1 : ");
            listItems = new CharSequence[wifiList.size()];

            for (int p = 0; p < wifiList.size(); p++) {
                if(wifiList.get(p).SSID.toString() != "")
                    listItems[p] = wifiList.get(p).SSID;
                System.out.println("Wifi : " + p + " -- " + listItems[p]);
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Configurar Wi-fi")
                    .setItems(listItems, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, final int which) {
                            {
                                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                                View v = inflater.inflate(R.layout.dialog_signin_wifi, null);

                                final EditText password = (EditText) v.findViewById(R.id.password);
                                AlertDialog.Builder builderPass = new AlertDialog.Builder(MainActivity.this);
                                builderPass.setView(v)
                                        .setPositiveButton("CONECTAR", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which2) {
                                                System.out.println("Chamar o item numero :  " + which);
                                                conectWifi(wifiScanList.get(which).SSID, password.getText().toString());
                                            }
                                        })
                                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }
                                        )
                                        .setTitle(wifiScanList.get(which).SSID);
                                AlertDialog dialogPass = builderPass.create();
                                dialogPass.show();
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }



    View.OnClickListener onMenuClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }else{
                drawer.openDrawer(GravityCompat.START);
            }
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {  // tools button

            showWifiDialog();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public List<ScanResult> showWifi(){

        ArrayList<String> listItems=new ArrayList<String>();
        ArrayAdapter<String> adapter;

        //configuração de wifi
        wifi = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true); // Liga o WiFi


        //se wifi ligado
        if (wifi.isWifiEnabled()) {

            WifiScanReceiver wifiReciever = new WifiScanReceiver(wifi);
            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //your code that requires permission
                wifi.startScan();
                wifiScanList = wifi.getScanResults();

                //if (wifiScanList.size() > 0)
                //    Toast.makeText(this, "Redes disponíveis!", Toast.LENGTH_LONG).show();

                /*
                for (int p = 0; p < wifiScanList.size(); p++) {
                    adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1,
                            listItems);
                    wifiList.setAdapter(adapter);
                    adapter.add(wifiScanList.get(p).SSID);
                    adapter.notifyDataSetChanged();
                }*/
            }
        }

        return wifiScanList;
    }



    private void permissaoWifi() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECEIVE_SMS)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 0);
            }
        } else {
        }
    }


    public void conectWifi(String wifiName, String password) {

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        //Configuro uma rede baseada nos dados encontrados.

        for(int p=0; p < wifiScanList.size(); p++) {

            System.out.println(wifiScanList.get(p).SSID);

            if(wifiScanList.get(p).SSID.toString().equals(wifiName)){
                wifiConfiguration.BSSID = wifiScanList.get(p).BSSID;
                wifiConfiguration.SSID = "\"" + wifiScanList.get(p).SSID + "\"";
                //wifiConfiguration.preSharedKey = "\"magicball\"";
                wifiConfiguration.preSharedKey = "\""+password+"\"";
                //wifiConfiguration.preSharedKey = password;
                //wifiConfiguration.SSID = wifiScanList.get(p).SSID;
                //wifiConfiguration.preSharedKey = "magicball";

            }
        }

        System.out.println("Conectando ...  " + wifiConfiguration.SSID);
        System.out.println("Senha ...  " + wifiConfiguration.preSharedKey);

        //Conecta na rede criada.
        WifiManager wifiManager = wifi;
        int netId = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.saveConfiguration();
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }



    /*
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

            ESPControler controler = new ESPControler(serverIP);
            controler.execute(onoff);
        }
    };*/


}
