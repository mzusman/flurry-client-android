package com.mzusman.bluetooth.commands.engine;

import com.mzusman.bluetooth.commands.PercentageObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * Created by zusmanmo on 05/06/2016.
 */
public class AbsEngineLoad extends PercentageObdCommand {
    public AbsEngineLoad() {
        super("01 43");
    }

    @Override
    public String getName() {
        return AvailableCommandNames.ABS_ENGINE_LOAD.getValue();
    }
}
