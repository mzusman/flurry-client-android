package com.mzusman.bluetooth;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

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
