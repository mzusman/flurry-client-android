package com.mzusman.bluetooth.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.mzusman.bluetooth.fragments.LoginFragment;
import com.mzusman.bluetooth.R;

/**
 * Created by amitmu on 04/15/2016.
 */
public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Fragment DeviceList=new LoginFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_login, DeviceList);
        transaction.commit();
    }
}
