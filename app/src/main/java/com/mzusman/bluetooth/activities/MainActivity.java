package com.mzusman.bluetooth.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.fragments.FragmentDetailsList;
import com.mzusman.bluetooth.fragments.FragmentDeviceList;
import com.mzusman.bluetooth.fragments.FragmentProfile;
import com.mzusman.bluetooth.utils.Constants;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);


        Fragment fragmentProfile = new FragmentProfile();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragmentProfile);
        transaction.commit();


    }


}

