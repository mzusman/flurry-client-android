
package com.mzusman.bluetooth.enums;

import com.mzusman.bluetooth.commands.ObdCommand;
import com.mzusman.bluetooth.commands.SpeedCommand;
import com.mzusman.bluetooth.commands.control.DistanceMILOnCommand;
import com.mzusman.bluetooth.commands.engine.AbsEngineLoad;
import com.mzusman.bluetooth.commands.engine.AcceleratorDPositionCommand;
import com.mzusman.bluetooth.commands.engine.AcceleratorEPositionCommand;
import com.mzusman.bluetooth.commands.engine.AcceleratorFPositionCommand;
import com.mzusman.bluetooth.commands.engine.AcceleratorPositionCommand;
import com.mzusman.bluetooth.commands.engine.CalculatedEngineLoad;
import com.mzusman.bluetooth.commands.engine.RPMCommand;
import com.mzusman.bluetooth.commands.engine.RelativeThrottlePositionCommand;
import com.mzusman.bluetooth.commands.engine.ThrottlePositionBCommand;
import com.mzusman.bluetooth.commands.engine.ThrottlePositionCCommand;
import com.mzusman.bluetooth.commands.engine.ThrottlePositionCommand;
import com.mzusman.bluetooth.commands.protocol.DescribeProtocolNumberCommand;

/**
 * Names of all available commands.
 *
 * @author pires
 * @version $Id: $Id
 */
public enum AvailableCommandNames {

    ENGINE_RPM("rpm", new RPMCommand(), true),
    ACCELERATOR_POS("accelerator", new AcceleratorPositionCommand(), true),
    SPEED("speed", new SpeedCommand(), true),
    THROTTLE_POS("throttle", new ThrottlePositionCommand(), true),
    RELATIVE_THROTTLE_POS("rel_throttle", new RelativeThrottlePositionCommand(), false),
    DISTANCE_TRAVELED_MIL_ON("traveled", new DistanceMILOnCommand(), false),
    DESCRIBE_PROTOCOL_NUMBER("protocol", new DescribeProtocolNumberCommand(), false),
    CALCULATED_ENGINE_LOAD("load", new CalculatedEngineLoad(), true),
    ABS_ENGINE_LOAD("abs_load", new AbsEngineLoad(), false),
    THROTTLE_C_POS("throttle_c", new ThrottlePositionCCommand(), false),
    ACCELERATOR_D_POS("accelerator_d", new AcceleratorDPositionCommand(), false),
    ACCELERATOR_E_POS("accelerator_e", new AcceleratorEPositionCommand(), false),
    ACCELERATOR_F_POS("accelerator_f", new AcceleratorFPositionCommand(), false),
    THROTTLE_B_POS("throttle_b", new ThrottlePositionBCommand(), false);


    private final String value;
    private final ObdCommand command;
    private boolean selected;

    /**
     * @param value Command description
     */
    AvailableCommandNames(String value, ObdCommand obdCommand, boolean selected) {
        this.value = value;
        this.command = obdCommand;
        this.selected = selected;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public final String getValue() {
        return value;
    }

    public boolean isSelected() {
        return selected;
    }

    public final ObdCommand getCommand() {
        return command;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
