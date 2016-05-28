package com.mzusman.bluetooth.commands.engine;


import com.mzusman.bluetooth.commands.ObdCommand;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * Displays the current engine revolutions per minute (RPM).
 *
 * @author pires
 * @version $Id: $Id
 */
public class RPMCommand extends ObdCommand {

    private int rpm = -1;

    /**
     * Default ctor.
     */
    public RPMCommand() {
        super("01 0C");
    }

    /**
     * Copy ctor.
     */
    public RPMCommand(RPMCommand other) {
        super(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performCalculations() {
        // ignore first two bytes [41 0C] of the response((A*256)+B)/4
        if (buffer.size() >= 4)
            rpm = (buffer.get(2) * 256 + buffer.get(3)) / 4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedResult() {
        return String.format("%d%s", rpm, getResultUnit());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCalculatedResult() {
        return String.valueOf(rpm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResultUnit() {
        return "RPM";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return AvailableCommandNames.ENGINE_RPM.getValue();
    }

    /**
     * <p>getRPM.</p>
     *
     * @return a int.
     */
    public int getRPM() {
        return rpm;
    }

}
