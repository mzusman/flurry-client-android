package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * Created by zusmanmo on 05/06/2016.
 */
public class RelativeThrottlePositionCommand extends PercentageObdCommand {

    public RelativeThrottlePositionCommand() {
        super("01 45");
    }

    @Override
    public String getName() {
        return AvailableCommandNames.RELATIVE_THROTTLE_POS.getValue();
    }
}
