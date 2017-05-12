package com.example.guilherme.conectesp;

/**
 * Created by lsitec on 11/05/17.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class LoginDialogFragment extends DialogFragment {
    OnLoginListener mListener;
    String mNote;
    EditText tiLayoutName;
    EditText tiLayoutPassword;
    ProgressBar progress;
    TextView tvHint;
    Activity myActivity = null;


    public LoginDialogFragment() {
    }

    public static LoginDialogFragment newInstance(OnLoginListener mOnLoginListener, String note) {
        LoginDialogFragment fragment = new LoginDialogFragment();
        fragment.mListener = mOnLoginListener;
        fragment.mNote = note;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_signin_wifi, null);

        tiLayoutPassword = (EditText) v.findViewById(R.id.password);
        builder.setView(v)
                .setPositiveButton("CONECTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    List<ScanResult> wifiScanList = null;
                                    WifiManager wifi = null;

                                    ArrayList<String> listItems=new ArrayList<String>();
                                    ArrayAdapter<String> adapter;

                                    //configuração de wifi
                                    /*wifi = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
                                    wifi.setWifiEnabled(true); // Liga o WiFi


                                    //se wifi ligado
                                    if (wifi.isWifiEnabled()) {

                                        WifiScanReceiver wifiReciever = new WifiScanReceiver(wifi);
                                        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

                                        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                            //your code that requires permission
                                            wifi.startScan();
                                            wifiScanList = wifi.getScanResults();
                                        }
                                    }*/

                                    WifiConfiguration wifiConfiguration = new WifiConfiguration();

                                    if(wifiScanList != null) {
                                        //Configuro uma rede baseada nos dados encontrados.
                                        for (int p = 0; p < wifiScanList.size(); p++) {

                                            System.out.println(wifiScanList.get(p).SSID);

                                            if (wifiScanList.get(p).SSID.toString().equals("Magicball 98BD Network")) {

                                                wifiConfiguration.BSSID = wifiScanList.get(p).BSSID;
                                                wifiConfiguration.SSID = "\"" + wifiScanList.get(p).SSID + "\"";
                                                wifiConfiguration.preSharedKey = "\"magicball\"";
                                                //wifiConfiguration.SSID = wifiScanList.get(p).SSID;
                                                //wifiConfiguration.preSharedKey = "magicball";

                                            }
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
                })
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                )
                .setTitle(getArguments().getString("titulo"));

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String username = tiLayoutName.getText().toString();
                    String password = tiLayoutPassword.getText().toString();
                    /*if (Utils.isOnline(LoginDialogFragment.this.getActivity())) {
                        if (!username.isEmpty() && password.length() >= 8) {
                            mListener.onLogin(username, password);
                            tiLayoutName.setVisibility(View.GONE);
                            tiLayoutPassword.setVisibility(View.GONE);
                            progress.setVisibility(View.VISIBLE);
                            tvHint.setText(R.string.login_hint_logging_in);
                        } else if (username.isEmpty()) {
                            tvHint.setText(R.string.login_hint_error_empty_username);
                        } else if (password.length() < 6) {
                            // according to a user report, the password may less than 8 characters
                            tvHint.setText(R.string.login_hint_error_short_password);
                        }
                    } else {
                        tvHint.setText(R.string.login_hint_error_offline);
                    }*/
                }
            });
        }
    }

    public void resetLogin() {
        tiLayoutName.setVisibility(View.VISIBLE);
        tiLayoutPassword.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        tvHint.setText("Passoword inválido");
    }

    public void setListener(OnLoginListener onLoginListener) {
        mListener = onLoginListener;
    }

    public interface OnLoginListener {
        //void onLogin(String username, String password);
    }
}