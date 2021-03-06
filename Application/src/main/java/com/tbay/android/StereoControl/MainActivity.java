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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.view.WindowManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

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

    private static Context context;

    public static Context getAppContext() {
        return MainActivity.context;
    }

    // Reference to the fragment showing events, so we can clear it with a button
    // as necessary.
    private LogFragment mLogFragment;
    private  AppPreferences mAppPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();

        setContentView(R.layout.sample_main);

        // Initialize text fragment that displays intro text.
        SimpleTextFragment introFragment = (SimpleTextFragment)
                    getSupportFragmentManager().findFragmentById(R.id.intro_fragment);
        introFragment.setText(R.string.intro_message);
        //introFragment.getTextView().setTextAppearance(context, android.R.style.TextAppearance_Holo_Small);

        // Get selection from shared Preferences. The preference is set when selecting a radiobutton.
        // Then set the text in the editable field to the last selected text (currently a default text)
        mAppPrefs = new AppPreferences(getApplicationContext());

        // Initialize the logging framework.
        initializeLogging();

        // Initialize the source IP string

        @SuppressLint("WifiManagerLeak") WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

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
            //mLogFragment.getLogView().setBackgroundColor(Color.WHITE);
            findViewById(R.id.log_fragment).setBackgroundColor(Color.WHITE);
            return true;
         }

         if (item.getItemId() == R.id.setup_action)
         {

             Intent intent = new Intent(this, SetupActivity.class);
             startActivity(intent);
         }
        return false;
    }

    /**
     * Start sending data to the peer.
     */
    private void startSending(String str) {


        // Check parameters and toast
        if (Integer.parseInt(mAppPrefs.ControlPort) > 65535)
        {
            Toast.makeText(MainActivity.this, "Port number should be < 65536",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            String[] args = new String[4];
            args[0] = mAppPrefs.ControlIp + ':' + mAppPrefs.ControlPort;
            args[1] = str;

            String IPStr = "Sending started at: ";

            IPStr += args[0];

            Log.i(TAG, IPStr);

            BackgroundActivity b = new BackgroundActivity(this);
            b.execute(args);
        }
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

    public void ShowError(String strErr, int color)
    {
        findViewById(R.id.log_fragment).setBackgroundColor(color);

        //Button bt = (Button)findViewById(R.id.ResetButton);
        //bt.setTextColor(color);
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

