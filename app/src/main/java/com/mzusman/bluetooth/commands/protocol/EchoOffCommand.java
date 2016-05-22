
package com.mzusman.bluetooth.commands.protocol;
/**
 * Turn-off echo.
 *
 * @author pires
 * @version $Id: $Id
 */
public class EchoOffCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for EchoOffCommand.</p>
     */
    public EchoOffCommand() {
        super("AT E0");
    }

    /**
     * <p>Constructor for EchoOffCommand.</p>
     *
     */
    public EchoOffCommand(EchoOffCommand other) {
        super(other);
    }

    /** {@inheritDoc} */
    @Override
    public String getFormattedResult() {
        return getResult();
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return "Echo Off";
    }

}
