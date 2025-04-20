package com.bloodcare.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class ReachabilityTest extends AsyncTask<Void, Void, Boolean> {
    private Context mContext;
    private String mHostName;
    private int mServicePort;
    private Callback mCallback;

    public interface Callback {
        void onReachabilityTestPassed();
        void onReachabilityTestFailed();
    }

    public ReachabilityTest(Context context, String hostName, int port, Callback callback) {
        mContext = context.getApplicationContext(); // Avoid leaking the Activity!
        mHostName = hostName;
        mServicePort = port;
        mCallback = callback;
    }

    public static Boolean test(Context context, String hostName, int servicePort) {
        try {
            if(isConnected(context)) {
                InetAddress address = isResolvable(hostName);
                if(address != null) {
                    if(canConnect(address, servicePort)) {
                        return true;
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean CheckInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected Boolean doInBackground(Void... args) {
        return test(mContext, mHostName, mServicePort);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(mCallback != null) {
            if(result) {
                mCallback.onReachabilityTestPassed();
            } else {
                mCallback.onReachabilityTestFailed();
            }
        }
    }

    private static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private static InetAddress isResolvable(String hostname) {
        try {
            return InetAddress.getByName(hostname);
        } catch(UnknownHostException e) {
            return null;
        }
    }

    private static boolean canConnect(InetAddress address, int port) {
        Socket socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(address, port);
        try {
            socket.connect(socketAddress, 3000);
        } catch(IOException e) {
            return false;
        } finally {
            if(socket.isConnected()) {
                try {
                    socket.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

}
