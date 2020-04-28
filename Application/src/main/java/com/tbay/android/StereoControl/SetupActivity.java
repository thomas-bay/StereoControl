package com.tbay.android.StereoControl;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

public class SetupActivity extends FragmentActivity {

    private  AppPreferences mAppPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAppPrefs = new AppPreferences(getApplicationContext());

        EditText et = (EditText) findViewById(R.id.IPAddrText);
        et.setText(AppPreferences.ControlIp);
        et = (EditText) findViewById(R.id.PortAddrText);
        et.setText(AppPreferences.ControlPort);
    }

    @Override
    public void onBackPressed()
    {
        EditText et = (EditText) findViewById(R.id.IPAddrText);
        AppPreferences.ControlIp = et.getText().toString();
        et = (EditText) findViewById(R.id.PortAddrText);
        AppPreferences.ControlPort = et.getText().toString();

        mAppPrefs.savePreferences(getApplicationContext());

        finish();
    }
}
