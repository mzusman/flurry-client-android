package com.mzusman.bluetooth.model.Managers;

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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * Created by zusmanmo on 15/04/2016.
 */

public class WifiManager implements Manager {

    private ArrayList<String> readings = new ArrayList<>();
    private long time = System.currentTimeMillis();
    private Socket socket;
    private static int TIME_OUT_VALUE = 5000;
    private Logger logger = Log4jHelper.getLogger("WifiManager");

    public WifiManager() {

    }

    @Override
    public void connect(String address) throws IOException, InterruptedException {
        String[] addressStr = Model.getInstance().getDeviceAddress(Constants.WIFI_TAG).split(",");
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(addressStr[0], Integer.parseInt(addressStr[1]));
        socket.connect(socketAddress, TIME_OUT_VALUE);
            /* 4 commands that are necessary for the obd2 api to configure itself */
        if (socket.isConnected()) {
            try {
                new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new TimeoutCommand(100).run(socket.getInputStream(), socket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO)
                        .run(socket.getInputStream(), socket.getOutputStream());
            } catch (IllegalAccessException | InstantiationException | NonNumericResponseException | ResponseException ignored) {
                logger.debug(ignored.getMessage());
            }
        }

    }

    @Override
    public void addCommands(String string, ObdCommand obdCommand) {
        commandsFactory.put(string, obdCommand);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public ArrayList<String> getReadings() throws IOException {

        time = System.currentTimeMillis();
        if (readings.size() > 0) readings.clear();
        for (String command : commandsFactory.keySet()) {
            //moving through all of the commands inside the pre setup command and execute them
            ObdCommand obdCommand = commandsFactory.get(command);
            try {
                obdCommand.run(socket.getInputStream(), socket.getOutputStream());
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
        return READ + "," + command.getFormattedResult();
    }

}
