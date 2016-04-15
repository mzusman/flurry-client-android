package com.mzusman.bluetooth.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mzusman.bluetooth.fragments.FragmentChooseManager;
import com.mzusman.bluetooth.R;

public class MainActivity extends AppCompatActivity {
	String deviceAdress      = null;


	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportActionBar().setTitle("Welcome");

        Fragment DeviceList=new FragmentChooseManager();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, DeviceList);
        transaction.commit();







	}
}
