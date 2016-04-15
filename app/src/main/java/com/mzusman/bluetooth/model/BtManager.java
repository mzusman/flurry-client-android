package com.mzusman.bluetooth.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ListView;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.mzusman.bluetooth.Constants;
import com.mzusman.bluetooth.DetailsActivity;
import com.mzusman.bluetooth.DetailsAdapter;
import com.mzusman.bluetooth.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public class BtManager implements Manager {
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    UUID uuid = UUID.fromString("667d60d3-981e-41c8-befc-ba931ebaa385");
    FileOutputStream fileOutputStream;
    JsonWriter jsonWriter;
    List<String> setReadings;

    boolean run = true;

    RPMCommand rpmCommand = new RPMCommand();
    SpeedCommand speedCommand = new SpeedCommand();
    ThrottlePositionCommand throttlePositionCommand = new ThrottlePositionCommand();

    @Override
    public void connect(String deviceAddress) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        bluetoothSocket.connect();

        new EchoOffCommand().run(bluetoothSocket.getInputStream(),
                bluetoothSocket.getOutputStream());
        new LineFeedOffCommand().run(bluetoothSocket.getInputStream(),
                bluetoothSocket.getOutputStream());
        new TimeoutCommand(125).run(bluetoothSocket.getInputStream(),
                bluetoothSocket.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO)
                .run(bluetoothSocket.getInputStream(),
                        bluetoothSocket.getOutputStream());

//                    while (run) {
//                        time = System.currentTimeMillis();
//                        rpmCommand.run(bluetoothSocket.getInputStream(),
//                                bluetoothSocket.getOutputStream());
//                        setReadings.set(0, Long.toString(time) + "," + rpmCommand.getFormattedResult());
//                        speedCommand.run(bluetoothSocket.getInputStream(),
//                                bluetoothSocket.getOutputStream());
//                        setReadings.set(1, Long.toString(time) + "," + speedCommand.getFormattedResult());
//

//                        setReadings.set(2, Long.toString(time) + "," + throttlePositionCommand.getFormattedResult());
//
//                    }
//                });
    }


    @Override
    public List<String> getReadings(int READINGS) {
        long time = System.currentTimeMillis();

        rpmCommand.run(bluetoothSocket.getInputStream(),
                bluetoothSocket.getOutputStream());

        setReadings.set(0, Long.toString(time) + "," + rpmCommand.getFormattedResult());
        speedCommand.run(bluetoothSocket.getInputStream(),
                bluetoothSocket.getOutputStream());

        setReadings.set(1, Long.toString(time) + "," + speedCommand.getFormattedResult());

        setReadings.set(2, Long.toString(time) + "," + throttlePositionCommand.getFormattedResult());

        return setReadings;

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
    public String getReading(int READ) {
        return null;
    }
}
