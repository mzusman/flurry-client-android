
package com.mzusman.bluetooth.commands.protocol;

import com.mzusman.bluetooth.commands.ObdCommand;

/**
 * <p>Abstract ObdProtocolCommand class.</p>
 */
public abstract class ObdProtocolCommand extends ObdCommand {
    /**
     * Default ctor to use
     *
     * @param command the command to send
     */
    public ObdProtocolCommand(String command) {
        super(command);
    }

    /**
     * Copy ctor.
     *
     * @param other the ObdCommand to copy.
     */
    public ObdProtocolCommand(ObdProtocolCommand other) {
        this(other.cmd);
    }

    /**
     * <p>performCalculations.</p>
     */
    protected void performCalculations() {
        // ignore
    }

    /**
     * <p>fillBuffer.</p>
     */
    protected void fillBuffer() {
        // settings commands don't return a value appropriate to place into the
        // buffer, so do nothing
    }

    /** {@inheritDoc} */
    @Override
    public String getCalculatedResult() {
        return String.valueOf(getResult());
    }
}
