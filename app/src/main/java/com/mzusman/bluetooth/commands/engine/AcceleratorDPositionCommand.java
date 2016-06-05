package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * Created by zusmanmo on 05/06/2016.
 */
public class AcceleratorDPositionCommand extends PercentageObdCommand{
    public AcceleratorDPositionCommand() {
        super("01 49");
    }

    @Override
    public String getName() {
        return AvailableCommandNames.ACCELERATOR_D_POS.getValue();
    }
}
