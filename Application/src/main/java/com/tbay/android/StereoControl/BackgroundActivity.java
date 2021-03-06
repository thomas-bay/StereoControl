package com.tbay.android.StereoControl;

import android.graphics.Color;
import android.os.AsyncTask;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.tbay.android.common.logger.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;


/**
 * Created by Thomas on 05-Mar-17.
 */
public class BackgroundActivity extends AsyncTask<String, Void, Void>{

    public static final String TAG = "StereoControl BackgroundActivity";

    MainActivity mMainAct;

    public BackgroundActivity(MainActivity activity) {

        mMainAct = activity;
    }

    protected Void Result(String statusStr, Boolean res) {

        final String status = statusStr;
        int col1 = Color.argb(150, 0, 255, 0);
        if (res == false)
            col1 = Color.argb(150, 255, 0, 0);
        final int col = col1;

        mMainAct.runOnUiThread(new Runnable()
                               {
                                   @Override
                                   public void run() {
                                       mMainAct.ShowError(status, col);
                                   }
                               });

        return null;
    }

//    @Override
    protected Void doInBackground(String... ip) {

        int j;
        String PortNum, IpAdr;
        byte[] InBuf = new byte[100];

        //create a socket to make the connection with the server
        try {
            j = ip[0].indexOf(':');
            if (j > 0) {
                PortNum = ip[0].substring(j + 1);
                IpAdr = ip[0].substring(0,j);
            }
            else {
                PortNum = String.valueOf(35000);
                IpAdr = ip[0];
            }

            Log.i(TAG, "Connecting... ");

            InetAddress addr = InetAddress.getByName(IpAdr);
            SocketAddress sockaddr = new InetSocketAddress(addr, Integer.parseInt(PortNum));

            Socket socket = new Socket();
            socket.connect(sockaddr, 2000);

            Log.i(TAG, "Connection opened to " + IpAdr + ":" + PortNum);

            OutputStream Out = socket.getOutputStream();
            InputStream In = socket.getInputStream();

            Log.i(TAG, "Tx: " + ip[1]);

            Out.write(ip[1].getBytes(), 0, ip[1].length());
            In.read(InBuf);
            Log.i(TAG, "Rx: " + new String(InBuf));

            socket.close();
            Log.i(TAG, "Connection closed.");


            //mMainAct.ShowError("Command accepted", Color.GREEN);

            Result("Command accepted", true);
            /*
            mMainAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMainAct.ShowError("xx", Color.argb(10, 0, 10, 0));
                }//public void run() {
            });
            */

        }
        catch (IOException e)
        {
            String errStr = "Error! " + e.getMessage();
            Log.i(TAG, errStr);
            Result(errStr, false);
        }

        return null;
    }
    //    @Override
    protected Void onPostExecute() {
        return null;
    }
}
