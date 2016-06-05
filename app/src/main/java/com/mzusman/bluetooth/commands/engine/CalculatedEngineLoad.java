package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;
import com.mzusman.bluetooth.commands.protocol.AvailablePidsCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * Created by zusmanmo on 05/06/2016.
 */
public class CalculatedEngineLoad extends PercentageObdCommand {
    public CalculatedEngineLoad() {
        super("01 04");
    }

    @Override
    public String getName() {
        return AvailableCommandNames.CALCULATED_ENGINE_LOAD.getValue();
    }
}
