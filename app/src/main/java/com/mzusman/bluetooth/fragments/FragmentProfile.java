package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.enums.AvailableCommandNames;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.adapters.ProfileAdapter;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;
import com.mzusman.bluetooth.utils.model.Profile;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/*
 * Class : .
 * Created by mzusman - morzusman@gmail.com on 4/20/16.
 */
public class FragmentProfile extends Fragment {

    int userId;
    ListView listView;
    ProfileAdapter profileAdapter;
    Logger log = Log4jHelper.getLogger("ProfileFragment");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // initialize the profiles array and list view
        ArrayList<Profile> profiles = new ArrayList<>();
        listView = new ListView(getActivity());
        profileAdapter = new ProfileAdapter(getActivity(), profiles);
        listView.setAdapter(profileAdapter);
        //
        this.userId = getActivity().getIntent().getExtras().getInt(Constants.USER_ID_TAG);

        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Previous Profiles");
        //allows the fragment to get onTouchListener notifications
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!menu.hasVisibleItems()) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.profile_menu, menu);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == (R.id.profile_add_btn)) {
            AlertDialog.Builder addBtn = new AlertDialog.Builder(getActivity());
            addBtn.setMessage(R.string.choose_msg)
                    .setNegativeButton("Bluetooth", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bundle btBundle = new Bundle();
                            btBundle.putString(Constants.MANAGER_TAG,
                                    Constants.BT_TAG); // making archive for the next fragment
                            btBundle.putInt(Constants.USER_ID_TAG, userId);
                            // to know that we clicked on the BT button
                            Fragment fragment = new FragmentDeviceList();
                            fragment.setArguments(btBundle);
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment, Constants.DETAILS_TAG).commit();
                        }
                    }).setPositiveButton("Wifi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder wifiBtn = new AlertDialog.Builder(getActivity());
                    wifiBtn.setMessage(R.string.wifi_msg)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle wifiBundle = new Bundle();
                                    Fragment fragment = new FragmentDetailsList();
                                    wifiBundle.putString(Constants.MANAGER_TAG, Constants.WIFI_TAG);
                                    wifiBundle.putInt(Constants.USER_ID_TAG, userId);
                                    fragment.setArguments(wifiBundle);
                                    getFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, fragment,
                                                    Constants.DETAILS_TAG).commit();
                                }
                            }).setCancelable(false).create().show();
                }
            }).setCancelable(false).create().show();
        } else if (item.getItemId() == R.id.options) {
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
                    dialog.dismiss();
                }
            }).setCancelable(false).create();
            optionBtn.show();

        }
        return super.onOptionsItemSelected(item);
    }
}

