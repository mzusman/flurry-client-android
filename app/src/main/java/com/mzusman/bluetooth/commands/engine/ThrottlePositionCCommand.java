package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;

/**
 * Created by zusmanmo on 05/06/2016.
 */
public class ThrottlePositionCCommand extends PercentageObdCommand {
    public ThrottlePositionCCommand() {
        super("01 48");
    }

    @Override
    public String getName() {
        return null;
    }
}
