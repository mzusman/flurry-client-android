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
import com.mzusman.bluetooth.model.Model;
import com.mzusman.bluetooth.utils.Constants;
import com.mzusman.bluetooth.utils.logger.Log4jHelper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public class BtManager implements Manager {
    private BluetoothAdapter bluetoothAdapter;
    private Logger logger = Log4jHelper.getLogger("BtManager");
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private ArrayList<String> readings = new ArrayList<>();
    private long time = System.currentTimeMillis();

    public BtManager() {

    }

    @Override
    public void addCommands(String string, ObdCommand obdCommand) {
        commandsFactory.put(string, obdCommand);
    }

    @Override
    public boolean isConnected() {
        return bluetoothSocket.isConnected();
    }

    @Override
    public void connect(String deviceAddress) throws IOException {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(Model.getInstance().getDeviceAddress(Constants.BT_TAG));
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            try {

                new EchoOffCommand()
                        .run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
                new LineFeedOffCommand()
                        .run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
                new TimeoutCommand(100)
                        .run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO)
                        .run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
            } catch (IllegalAccessException | ResponseException | InstantiationException | NonNumericResponseException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            Log.d(Constants.RUN_TAG, "connect: Interrupt");
        }
    }


    /**
     * Return
     *
     * @return Returns a set of strings  - index 0 - rpm , index 1 - speed , index 2 - throttlePos
     */
    @Override
    public ArrayList<String> getReadings() throws IOException {
        if (readings.size() > 0) readings.clear();
        for (String command : commandsFactory.keySet()) {
            ObdCommand obdCommand = commandsFactory.get(command);
            try {
                obdCommand.run(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
            } catch (InterruptedException e) {
                logger.debug("getReadings Interrupt\n" + e.getMessage());
                readings.add(command + ",-1");
                continue;
            } catch (IllegalAccessException e) {
                logger.debug("getReadings illegalAcceess\n" + e.getMessage());
                readings.add(command + ",-1");
                continue;
            } catch (InstantiationException e) {
                logger.debug("getReadings InstanitaionException\n" + e.getMessage());
                readings.add(command + ",-1");
                continue;
            } catch (ResponseException e) {
                logger.debug("getReadings Response\n" + e.getMessage());
                readings.add(command + ",-1");
                continue;
            } catch (NonNumericResponseException e) {
                logger.debug("getReadings NonNumeric\n" + e.getMessage());
                readings.add(command + ",-1");
                continue;
            }
            readings.add(command + "," +
                    obdCommand.getCalculatedResult());
        }

        return readings;

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
        time = System.currentTimeMillis();
        ObdCommand command = commandsFactory.get(READ);
        if (command == null) return null;
        return READ + "," + Long.toString(time) + "," + command.getFormattedResult();


    }

}



