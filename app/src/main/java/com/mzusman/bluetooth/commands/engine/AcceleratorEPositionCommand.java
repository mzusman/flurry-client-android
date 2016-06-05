package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * Created by zusmanmo on 05/06/2016.
 */
public class AcceleratorEPositionCommand extends PercentageObdCommand{
    public AcceleratorEPositionCommand() {
        super("01 4A");
    }

    @Override
    public String getName() {
        return AvailableCommandNames.ACCELERATOR_E_POS.getValue();
    }
}
