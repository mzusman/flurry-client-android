package com.mzusman.bluetooth.model;


import com.mzusman.bluetooth.commands.ObdCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public interface Manager {


    interface Factory {
        void setCommandsFactory(HashMap<String, ObdCommand> commandsFactory);
    }

    HashMap<String, ObdCommand> commandsFactory = new HashMap<>();

    boolean isConnected();

    void connect(String deviceAddress) throws IOException, InterruptedException;

    ArrayList<String> getReadings() throws IOException;

    void stop();

    String getReading(String READ);

    UUID uuid = UUID.fromString("667d60d3-981e-41c8-befc-ba931ebaa385");


}
