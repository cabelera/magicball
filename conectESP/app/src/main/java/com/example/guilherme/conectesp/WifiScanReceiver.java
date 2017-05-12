package com.example.guilherme.conectesp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by guilherme on 30/03/17.
 */

public class WifiScanReceiver extends BroadcastReceiver {
    final private OnScanListener[] scanListeners;
    final private WifiManager wifi;
    final List<String> listOfProvider;


    public interface OnScanListener {

        public void onScanFinished(ScanResult[] networks);
    }

    public WifiScanReceiver(WifiManager wifi, OnScanListener... scanListener) {
        this.scanListeners = scanListener;
        this.wifi = wifi;
        listOfProvider = null;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent == null
                || !intent.getAction().equals(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            return;
        if (scanListeners == null)
            return;
        if (wifi == null)
            return;
        final List<ScanResult> results = wifi.getScanResults();
  /*
   * He have had reports of this returning null instead of empty
   */
        if (results == null)
            return;

        try {
            // Single scan
            context.unregisterReceiver(this);
        } catch (Exception e) {
        }

        for (int i = 0; i < results.size() - 1; ++i)
            for (int j = i + 1; j < results.size(); ++j)
                if (results.get(i).SSID.equals(results.get(j).SSID))
                    results.remove(j--);
        final ScanResult[] networks = new ScanResult[results.size()];
        final Iterator<ScanResult> it = results.iterator();
        int i = 0;
        while (it.hasNext())
            networks[i++] = it.next();
        for (OnScanListener scanListener : scanListeners)
            scanListener.onScanFinished(networks);

        //sortByFrequency();
    }

    // This method call when number of wifi connections changed
    public void sortByFrequency() {
        List<ScanResult> wifiList = wifi.getScanResults();

            /* sorting of wifi provider based on level */
        Collections.sort(wifiList, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return (lhs.level > rhs.level ? -1
                        : (lhs.level == rhs.level ? 0 : 1));
            }
        });
        listOfProvider.clear();
        String providerName;
        for (int i = 0; i < wifiList.size(); i++) {
                /* to get SSID and BSSID of wifi provider*/
            providerName = (wifiList.get(i).SSID).toString()
                    +"\n"+(wifiList.get(i).BSSID).toString();
            listOfProvider.add(providerName);
        }
            /*setting list of all wifi provider in a List*/
        //adapter = new ListAdapter(MainActivity.this, listOfProvider);
        //listViwProvider.setAdapter(adapter);

        //adapter.notifyDataSetChanged();
    }

}
