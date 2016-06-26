package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.enums.AvailableCommandNames;
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;

import org.apache.log4j.Logger;

import java.io.File;

/*
 * Class : .
 * Created by mzusman - morzusman@gmail.com on 4/20/16.
 */
public class FragmentProfile extends Fragment {

    Logger log = Log4jHelper.getLogger("ProfileFragment");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);

        // initialize the profiles array and list view
        //

        final ImageView wifi = (ImageView) view.findViewById(R.id.wifi_ib);
        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                final AlertDialog.Builder wifiBtn = new AlertDialog.Builder(getActivity());
                wifiBtn.setMessage(R.string.wifi_msg)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle wifiBundle = new Bundle();
                                Fragment fragment = new FragmentDetailsList();
                                wifiBundle.putString(Constants.MANAGER_TAG, Constants.WIFI_TAG);
                                wifiBundle.putString(Constants.DEVICE_TAG, Constants.WIFI_ADDRESS);
                                fragment.setArguments(wifiBundle);
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, fragment,
                                                Constants.DETAILS_TAG).commit();
                            }
                        }).setNegativeButton("Cancel", null)
                        .setCancelable(false).create().show();
            }
        });
        ImageView bt = (ImageView) view.findViewById(R.id.bt_ib);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                Bundle btBundle = new Bundle();
                btBundle.putString(Constants.MANAGER_TAG,
                        Constants.BT_TAG); // making archive for the next fragment
                // to know that we clicked on the BT button
                Fragment fragment = new FragmentBlueToothDevices();
                fragment.setArguments(btBundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment, Constants.DETAILS_TAG).commit();
            }
        });
        ImageView settings = (ImageView) view.findViewById(R.id.settings_ib);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] selectedCommands = new boolean[AvailableCommandNames.values().length];
                CharSequence[] items = new CharSequence[AvailableCommandNames.values().length];
                for (int i = 0; i < AvailableCommandNames.values().length; i++) {
                    items[i] = AvailableCommandNames.values()[i].getValue();
                    if (AvailableCommandNames.values()[i].isSelected())
                        selectedCommands[i] = true;
                }
                AlertDialog.Builder optionBtn = new AlertDialog.Builder(getActivity());
                optionBtn.setTitle("Select Commands").setMultiChoiceItems(items, selectedCommands, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < AvailableCommandNames.values().length; i++) {
                            AvailableCommandNames.values()[i].setSelected(selectedCommands[i]);
                            if (selectedCommands[i])
                                log.debug("selected " + AvailableCommandNames.values()[i].getValue());
                        }
                        Toast.makeText(getActivity(), "Commands have been saved!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).setCancelable(false).create();
                optionBtn.show();
            }
        });
        ImageView data = (ImageView) view.findViewById(R.id.data_ib);
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://54.152.123.228/api/v1/flurry/drivers/" + Model.getInstance().getId() + "/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Choose an Action");
        //allows the fragment to get onTouchListener notifications
        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }
}

