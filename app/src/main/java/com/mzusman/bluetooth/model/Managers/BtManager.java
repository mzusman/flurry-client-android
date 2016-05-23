package com.mzusman.bluetooth.model.Managers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.mzusman.bluetooth.commands.ObdCommand;
import com.mzusman.bluetooth.commands.protocol.EchoOffCommand;
import com.mzusman.bluetooth.commands.protocol.LineFeedOffCommand;
import com.mzusman.bluetooth.commands.protocol.SelectProtocolCommand;
import com.mzusman.bluetooth.commands.protocol.TimeoutCommand;
import com.mzusman.bluetooth.enums.ObdProtocols;
import com.mzusman.bluetooth.exceptions.NonNumericResponseException;
import com.mzusman.bluetooth.exceptions.ResponseException;
import com.mzusman.bluetooth.model.Manager;
import com.mzusman.bluetooth.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public class BtManager implements Manager {
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    boolean run = true;


    ArrayList<String> readings = new ArrayList<>();

    long time = System.currentTimeMillis();


    public BtManager(Factory factory) {
        factory.setCommandsFactory(commandsFactory);
    }


    @Override
    public void connect(String deviceAddress) throws IOException {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            try {

                new EchoOffCommand()
                        .run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
                new LineFeedOffCommand()
                        .run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
                new TimeoutCommand(125)
                        .run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO)
                        .run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            } catch (NonNumericResponseException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(Constants.RUN_TAG, "connect: Interrupt");
        }
    }


    /**
     * Return
     *
     * @return Returns a set of strings  - index 0 - rpm , index 1 - speed , index 2 - throttlePos
     */
    @Override
    public ArrayList<String> getReadings() {
        if (readings.size() > 0) readings.clear();
        try {
            for (String string : commandsFactory.keySet()) {
                ObdCommand obdCommand = commandsFactory.get(string);

                obdCommand.run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());

                readings.add(
                        string + "," + Long.toString(time) + "," + obdCommand.getFormattedResult());
            }

            return readings;

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(Constants.RUN_TAG, "getReadings Interrupt");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Constants.IO_TAG, "getReadings IO Error");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        } catch (NonNumericResponseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void stop() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Constants.IO_TAG, "stop: Close Connect Exception ");
        }

    }

    @Override
    public String getReading(String READ) {
//        try {
        time = System.currentTimeMillis();

        ObdCommand command = commandsFactory.get(READ);

        if (command == null) return null;

//            command.run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
        return READ + "," + Long.toString(time) + "," + command.getFormattedResult();


//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//            return null;
    }

}



