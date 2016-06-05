package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * Created by zusmanmo on 05/06/2016.
 */
public class ThrottlePositionBCommand extends PercentageObdCommand {

    public ThrottlePositionBCommand() {
        super("01 47");
    }

    @Override
    public String getName() {
        return AvailableCommandNames.THROTTLE_B_POS.getValue();
    }
}
