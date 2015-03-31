package android.fanoyong.com.networkinfo;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
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
            setTextView();
            Log.d(TAG, "************************************************************");

        }
    };

    private Context mContext;
    private ConnectivityManager mCm;
    private NetworkRequest mNetworkRequest;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mCm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        Button button1 = (Button) findViewById(R.id.btn_1);
        Button button2 = (Button) findViewById(R.id.btn_2);
        Button button3 = (Button) findViewById(R.id.btn_3);
        Button button4 = (Button) findViewById(R.id.btn_4);
        Button button5 = (Button) findViewById(R.id.btn_5);
        Button button6 = (Button) findViewById(R.id.btn_6);
        tv1 = (TextView) findViewById(R.id.textview_1);

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
                        requestRoute(ConnectivityManager.TYPE_MOBILE_MMS);
                        break;
                    case R.id.btn_4:
                        startHipri();
                        break;
                    case R.id.btn_5:
                        stopHipri();
                        break;
                    case R.id.btn_6:
                        requestRoute(ConnectivityManager.TYPE_MOBILE_HIPRI);
                        break;
                }
            }
        };

        button1.setOnClickListener(ocl);
        button2.setOnClickListener(ocl);
        button3.setOnClickListener(ocl);
        button4.setOnClickListener(ocl);
        button5.setOnClickListener(ocl);
        button6.setOnClickListener(ocl);
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

    private void startHipri() {
        int result = mCm.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableHIPRI");
        Log.d(TAG, "startUsingNetworkFeature result=" + result);
    }

    private void stopHipri() {
        int result = mCm.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableHIPRI");
        Log.d(TAG, "stopUsingNetworkFeature result=" + result);
    }

    private void requestRoute(int type) {
        new HttpTask().execute(type);
    }

    private class HttpTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer[] params) {
            int type = params[0];
            boolean result = false;
            Log.d(TAG, "requestRouteToHost type=" + type);
            switch (type) {
                case ConnectivityManager.TYPE_MOBILE_MMS:
                    result = mCm.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_MMS, lookupHost("http://mms.vtext.com/servlets/mms"));
                    break;
                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                    result = mCm.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_HIPRI, lookupHost("http://mms.vtext.com/servlets/mms"));
                    break;
            }
            Log.d(TAG, "requestRouteToHost result=" + result);
            return null;
        }
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
        sb.append("getActiveNetworkInfo\n");
        sb.append(mCm.getActiveNetworkInfo());
        NetworkInfo[] nis = mCm.getAllNetworkInfo();
        if (nis != null) {
            for (NetworkInfo ni : nis) {
                sb.append("ni.getType\t" + ni.getType() + "\tni.subType\t" + ni.getSubtypeName() + "\tni.extraInfo\t" + ni.getExtraInfo() + "\tni.getState\t" + ni.getState() + "\tni.detailState\t" + ni.getDetailedState() + "\tni.isAvailable\t" + ni.isAvailable() + "\tni.isConnected\t" + ni.isConnected() + "\n");
            }
        }
        return sb.toString();
    }

    private void setTextView() {
        StringBuilder sb = new StringBuilder();
        NetworkInfo ni = mCm.getActiveNetworkInfo();
        if (ni != null) {
            sb.append("ni.getType " + ni.getType() + " ni.subType " + ni.getSubtypeName() + " ni.extraInfo " + ni.getExtraInfo() + " ni.getState " + ni.getState() + " ni.detailState " + ni.getDetailedState() + " ni.isAvailable " + ni.isAvailable() + " ni.isconnected " + ni.isConnected());
        }
        tv1.setText(sb.toString());
    }
}
