/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tbay.android.StereoControl;

import android.net.wifi.WifiManager;
import android.view.WindowManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Button;

import com.tbay.android.common.logger.Log;
import com.tbay.android.common.logger.LogFragment;
import com.tbay.android.common.logger.LogWrapper;
import com.tbay.android.common.logger.MessageOnlyLogFilter;


/**
 * Sample application demonstrating how to test whether a device is connected,
 * and if so, whether the connection happens to be wifi or mobile (it could be
 * something else).
 *
 * This sample uses the logging framework to display log output in the log
 * fragment (LogFragment).
 */
public class MainActivity extends FragmentActivity {

    public static final String TAG = "StereoControl";
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;

    // Reference to the fragment showing events, so we can clear it with a button
    // as necessary.
    private LogFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        // Initialize text fragment that displays intro text.
        SimpleTextFragment introFragment = (SimpleTextFragment)
                    getSupportFragmentManager().findFragmentById(R.id.intro_fragment);
        introFragment.setText(R.string.intro_message);

        // Initialize the logging framework.
        initializeLogging();

        // Initialize the source IP string

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        try {
            int ip = wm.getConnectionInfo().getIpAddress();
        }
        catch(Exception e)
        {
            String S = e.toString();  // Just for debug
        }

        // Set focus away from edit fields to avoid keyboard popping up..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clear_action)
        {
            mLogFragment.getLogView().setText("");
            return true;
         }
        return false;
    }

    /**
     * Start sending data to the peer.
     */
    private void startSending(String str) {

        EditText Ip = (EditText)findViewById(R.id.DestinationIP);
        EditText Port = (EditText)findViewById(R.id.PortNumber);

        String[] args = new String[4];
        args[0] = Ip.getText().toString() + ':' +  Port.getText().toString();
        args[1] = str;

        String IPStr = "Sending started at: ";

        IPStr += Ip.getText();

        Log.i(TAG, IPStr);

        new BackgroundActivity().execute(args);

    }

    /** Create a chain of targets that will receive log data */
    public void initializeLogging() {

        // Using Log, front-end to the logging chain, emulates
        // android.util.log method signatures.

        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        // A filter that strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        mLogFragment =
                (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        msgFilter.setNext(mLogFragment.getLogView());
    }


    public void TurnOn(View v) {

        RadioGroup rg = (RadioGroup)findViewById(R.id.Selector);
        int id = rg.getCheckedRadioButtonId();
        char Selection;

        switch (id)
        {
            case R.id.Radio:
                Selection = '0';
                break;
            case R.id.Aux:
                Selection = '1';
                break;
            case R.id.TV:
                Selection = '2';
                break;
            default:
                Selection = '3';
                break;
        }

        startSending(getString(R.string.ImmediateOn)+Selection);
    }

    public void TurnOff(View v) {
        startSending(getString(R.string.ImmediateOff));
    }

    public void SetTime(View v) {

        // Format the sting like 2#xx:xx#xx:xx

        String sendStr = getString(R.string.TimeOn);
        EditText time = (EditText)findViewById(R.id.editOnTime);
        sendStr = sendStr + time.getText().toString();
        time = (EditText)findViewById(R.id.editOffTime);
        sendStr = sendStr + "#" + time.getText().toString();

        startSending(sendStr);
    }

    public void ResetTime(View v) {
        startSending(getString(R.string.TimeOff));
    }



}

