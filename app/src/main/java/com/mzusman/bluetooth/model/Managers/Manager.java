package com.mzusman.bluetooth.model.Managers;


import com.mzusman.bluetooth.commands.ObdCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public interface Manager extends Readable{


    void addCommands(String string, ObdCommand obdCommand);

    HashMap<String, ObdCommand> commandsFactory = new HashMap<>();

    boolean isConnected();

    void connect(String deviceAddress) throws IOException, InterruptedException;

    void stop();

    UUID uuid = UUID.fromString("667d60d3-981e-41c8-befc-ba931ebaa385");


}
