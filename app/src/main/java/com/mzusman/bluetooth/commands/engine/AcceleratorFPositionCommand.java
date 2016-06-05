package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * Created by zusmanmo on 05/06/2016.
 */
public class AcceleratorFPositionCommand extends PercentageObdCommand{
    public AcceleratorFPositionCommand() {
        super("01 4B");
    }

    @Override
    public String getName() {
        return AvailableCommandNames.ACCELERATOR_F_POS.getValue();
    }
}
