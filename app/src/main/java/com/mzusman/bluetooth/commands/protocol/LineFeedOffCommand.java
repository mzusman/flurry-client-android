
package com.mzusman.bluetooth.commands.protocol;
/**
 * Turns off line-feed.
 *
 * @author pires
 * @version $Id: $Id
 */
public class LineFeedOffCommand extends ObdProtocolCommand {

    /**
     * <p>Constructor for LineFeedOffCommand.</p>
     */
    public LineFeedOffCommand() {
        super("AT L0");
    }

    /**
     * <p>Constructor for LineFeedOffCommand.</p>
     *
     */
    public LineFeedOffCommand(LineFeedOffCommand other) {
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
        return "Line Feed Off";
    }

}
