package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/*
 * Class : .
 * Created by mzusman - morzusman@gmail.com on 5/24/16.
 */
public class AcceleratorPositionCommand extends PercentageObdCommand {

    public AcceleratorPositionCommand() {
        super("01 5A");
    }

    @Override
    public String getName() {
        return AvailableCommandNames.ACCELERATOR_POS.getValue();
    }
}
