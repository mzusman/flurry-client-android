package com.mzusman.bluetooth.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mzusman.bluetooth.R;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.adapters.DevicesAdapter;
import com.mzusman.bluetooth.utils.types.Connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created on 04/15/2016.
 */
public class FragmentBlueToothDevices extends Fragment {

    DevicesAdapter devicesAdapter;
    Connector connector;
    ArrayList<String> devicesArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_list_frag, container, false);


        String managerString = getArguments().getString(Constants.MANAGER_TAG);
        if (managerString.equals(Constants.BT_TAG)) connector = new BTConnector();

        devicesArrayList = (ArrayList<String>) connector.initateConnections();

        ListView listOfDevices = (ListView) view.findViewById(R.id.device_list);
        devicesAdapter = new DevicesAdapter(devicesArrayList, getActivity());
        listOfDevices.setAdapter(devicesAdapter);
        listOfDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String deviceAddress = devicesAdapter.getDeviceAddress(position);
                Fragment details = new FragmentDetailsList();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.MANAGER_TAG, Constants.BT_TAG);
                bundle.putString(Constants.DEVICE_TAG, deviceAddress);

                details.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, details);
                transaction.commit();
            }
        });




        return view;


    }


    class BTConnector implements Connector {
        private BluetoothAdapter bluetoothAdapter;
        List<String> devicesString = new ArrayList<>();

        @Override
        public List<String> initateConnections() {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                showDialog("Device is'nt supported");
            }
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
            }

            final Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();

            if (bluetoothDevices.size() > 0) {
                for (BluetoothDevice device : bluetoothDevices) {
                    devicesString.add(device.getName() + "," +
                            device.getAddress());//insert address to the list
                }
            }

            return devicesString;
        }
    }


    public void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        builder.setMessage(msg);
        builder.show();
    }
}
