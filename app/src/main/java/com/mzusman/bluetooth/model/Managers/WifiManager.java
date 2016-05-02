package com.mzusman.bluetooth.model.Managers;

import android.util.Log;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.mzusman.bluetooth.model.Manager;
import com.mzusman.bluetooth.utils.Constants;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by zusmanmo on 15/04/2016.
 */

public class WifiManager implements Manager {

    ArrayList<String> readings = new ArrayList<>();
    long time = System.currentTimeMillis();
    Socket socket;

    public WifiManager(Factory factory) {
        factory.setCommandsFactory(commandsFactory);
    }

    @Override
    public void connect(String address) throws IOException, InterruptedException {
            String[] addressStr = address.split(",");
            socket = new Socket(addressStr[0], Integer.parseInt(addressStr[1]));

            /* 4 commands that are necessary for the obd2 api to configure itself */
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO)
                    .run(socket.getInputStream(), socket.getOutputStream());




    }

    @Override
    public ArrayList<String> getReadings() {

        time = System.currentTimeMillis();
        if (readings.size() > 0) readings.clear();
        for (String command : commandsFactory.keySet()) {
            //moving throught all of the commands inside the pre setup command and execute them
            ObdCommand obdCommand = commandsFactory.get(command);
            if (obdCommand == null)
                readings.add(command + "," + Long.toString(time) + "," + "0");
            else {
                try {
                    obdCommand.run(socket.getInputStream(), socket.getOutputStream());
                    readings.add(command + "," + Long.toString(time) + "," +
                            obdCommand.getFormattedResult());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d(Constants.RUN_TAG, "getReadings Interrupt");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(Constants.IO_TAG, "getReadings IO Error");
                }
            }
        }
        return readings;


    }


    @Override
    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getReading(String READ) {

        time = System.currentTimeMillis();
        ObdCommand command = commandsFactory.get(READ);

        if (command == null)
            return READ + "," + Long.toString(time) + "," + "0";

        try {
            command.run(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return READ + "," + Long.toString(time) + "," + command.getFormattedResult();
    }

}
