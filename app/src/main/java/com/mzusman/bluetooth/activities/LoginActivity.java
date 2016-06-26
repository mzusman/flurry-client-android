package com.mzusman.bluetooth.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.fragments.LoginFragment;
import com.mzusman.bluetooth.utils.logger.Log4jRuntime;

/**
 * Created by amitmu on 04/15/2016.
 */
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log4jRuntime log4jRuntime = new Log4jRuntime();
        Thread.setDefaultUncaughtExceptionHandler(log4jRuntime);
        setContentView(R.layout.activity_login);

        Fragment DeviceList = new LoginFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_login, DeviceList);
        transaction.commit();

    }


}
