
package com.mzusman.bluetooth.commands.protocol;

/**
 * Warm-start the OBD connection.
 *
 * @author pires
 * @version $Id: $Id
 */
public class ObdWarmstartCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for ObdWarmstartCommand.</p>
     */
    public ObdWarmstartCommand() {
        super("AT WS");
    }

    /**
     * <p>Constructor for ObdWarmstartCommand.</p>
     */
    public ObdWarmstartCommand(ObdWarmstartCommand other) {
        super(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedResult() {
        return getResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Warmstart OBD";
    }

}
