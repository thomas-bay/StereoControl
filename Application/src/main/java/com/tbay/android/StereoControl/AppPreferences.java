package com.tbay.android.StereoControl;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import static android.provider.Settings.System.getString;
import static com.tbay.android.StereoControl.R.string.ip_text;

/**
 * Created by Thomas on 19-Nov-16.
 */

public class AppPreferences {
    static String ControlIp;    // The text to send in the SMS/TextMessage
    static String ControlPort;

    static final String ControlIpDefault = "192.168.0.5"; // "10.0.0.5";
    static final String ControlPortDefault = "8003";
    static final String StartTimeDefault = "07:30";

    public void readPreferences(Context c)
    {

        // Get selection from shared Preferences. The preference is set when selecting a radiobutton.
        // Then set the text in the editable field to the last selected text (currently a default text)
        SharedPreferences mPref = c.getSharedPreferences("com.tbay.android.StereoControl.PREFS", Context.MODE_PRIVATE);

        ControlIp = mPref.getString("CONTROLIP_TAG", ControlIpDefault);
        ControlPort = mPref.getString("CONTROLPORT_TAG", ControlPortDefault);
    }

    public void savePreferences(Context c)
    {
        SharedPreferences mPref = c.getSharedPreferences("com.tbay.android.StereoControl.PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();

        editor.putString("CONTROLIP_TAG", ControlIp);
        editor.putString("CONTROLPORT_TAG", ControlPort);

        editor.apply();
    }

    AppPreferences (Context c)
    {
        readPreferences(c);
    }
}
