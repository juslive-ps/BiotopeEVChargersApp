package ws.tilda.anastasia.biotopeevchargersapp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BiotopeEvChargersApp extends Application {
    private static BiotopeEvChargersApp instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static BiotopeEvChargersApp getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
