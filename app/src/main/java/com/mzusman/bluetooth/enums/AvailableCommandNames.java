
package com.mzusman.bluetooth.enums;

import com.mzusman.bluetooth.commands.ObdCommand;
import com.mzusman.bluetooth.commands.SpeedCommand;
import com.mzusman.bluetooth.commands.control.DistanceMILOnCommand;
import com.mzusman.bluetooth.commands.engine.AcceleratorPositionCommand;
import com.mzusman.bluetooth.commands.engine.RPMCommand;
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
    DISTANCE_TRAVELED_MIL_ON("traveled", new DistanceMILOnCommand(), false),
    DESCRIBE_PROTOCOL_NUMBER("protocol", new DescribeProtocolNumberCommand(), false);

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
