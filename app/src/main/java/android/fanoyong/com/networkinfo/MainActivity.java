package android.fanoyong.com.networkinfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    private static final String TAG = "NetworkInfo|";
    private BroadcastReceiver mConnChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "************************************************************");
            Log.d(TAG, "ConnectivityChange occured.");
            Log.d(TAG, "ConnectivityInfo :");
            String dump = getConnectivityInfo();
            Log.d(TAG, dump);
            Log.d(TAG, "************************************************************");
        }
    };


    private Context mContext;
    private ConnectivityManager mCm;
    private NetworkRequest mNetworkRequest;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mCm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        Button button1 = (Button) findViewById(R.id.btn_1);
        Button button2 = (Button) findViewById(R.id.btn_2);
        Button button3 = (Button) findViewById(R.id.btn_3);

        IntentFilter intentfilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mConnChangeReceiver, intentfilter);
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_1:
                        startMobileMms();
                        break;
                    case R.id.btn_2:
                        stopMobileMms();
                        break;
                    case R.id.btn_3:
                        startMobileMmsNew();
                        break;
                    default:
                }
            }
        };

        button1.setOnClickListener(ocl);
        button2.setOnClickListener(ocl);
        button3.setOnClickListener(ocl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mConnChangeReceiver);
    }


    private void startMobileMms() {
        int result = mCm.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableMMS");
        Log.d(TAG, "startUsingNetworkFeature result=" + result);
    }

    private void stopMobileMms() {
        int result = mCm.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableMMS");
        Log.d(TAG, "stopUsingNetworkFeature result=" + result);
    }

    private void startMobileMmsNew() {
        boolean result = mCm.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_MMS, lookupHost("http://mms.vtext.com/servlets/mms"));
        Log.d(TAG, "startUsingNetworkFeature result=" + result);
    }

    private int lookupHost(String hostname) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            return -1;
        }
        byte[] addrBytes;
        int addr;
        addrBytes = inetAddress.getAddress();
        addr = ((addrBytes[3] & 0xff) << 24)
                | ((addrBytes[2] & 0xff) << 16)
                | ((addrBytes[1] & 0xff) << 8)
                | (addrBytes[0] & 0xff);
        return addr;
    }

    private String getConnectivityInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("getAllNetworkInfo\n");
        sb.append(mCm.getAllNetworkInfo());
        sb.append("************************************************************\n");
        sb.append("getActiveNetworkInfo\n");
        sb.append(mCm.getActiveNetworkInfo());
        sb.append("************************************************************\n");
        NetworkInfo[] nis = mCm.getAllNetworkInfo();
        if (nis != null) {
            for (NetworkInfo ni : nis) {
                sb.append("ni.getType" + ni.getType() + "ni.getState" + ni.getState() + "ni.detailState" + ni.getDetailedState() + "ni.isAvailable" + ni.isAvailable() + "ni.isconnected" + ni.isConnected());
            }
        }
        sb.append("************************************************************\n");
        return sb.toString();
    }
}
