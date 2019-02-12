package com.itcs.aihome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckConnection extends BroadcastReceiver {
    public static CheckConnectionListener checkConnectionListener;
    public CheckConnection() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if(checkConnectionListener != null) {
            checkConnectionListener.onNetworkConnectionChanged(isConnected);
        }
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) Homepage.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public interface CheckConnectionListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
