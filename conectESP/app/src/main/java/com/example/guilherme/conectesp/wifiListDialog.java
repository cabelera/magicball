package com.example.guilherme.conectesp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import java.util.List;

/**
 * Created by lsitec on 09/05/17.
 */

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
public class wifiListDialog extends DialogFragment {

    CharSequence[] listItens;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        List<ScanResult> wifiScanList;

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wi-Fi")
                .setItems(listItens, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                })
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }



}
