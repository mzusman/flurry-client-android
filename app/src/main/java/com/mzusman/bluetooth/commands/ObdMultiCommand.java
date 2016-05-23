
package com.mzusman.bluetooth.commands;

import com.mzusman.bluetooth.exceptions.NonNumericResponseException;
import com.mzusman.bluetooth.exceptions.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 *
 * @author pires
 * @version $Id: $Id
 */
public class ObdMultiCommand {

    private ArrayList<ObdCommand> commands;

    /**
     * Default ctor.
     */
    public ObdMultiCommand() {
        this.commands = new ArrayList<>();
    }

    /**
     * Add ObdCommand to list of ObdCommands.
     */
    public void add(ObdCommand command) {
        this.commands.add(command);
    }

    /**
     * Removes ObdCommand from the list of ObdCommands.
     */
    public void remove(ObdCommand command) {
        this.commands.remove(command);
    }

    /**
     * Iterate all commands, send them and read response.
     *
     * @param in  a {@link java.io.InputStream} object.
     * @param out a {@link java.io.OutputStream} object.
     * @throws java.io.IOException            if any.
     * @throws java.lang.InterruptedException if any.
     */
    public void sendCommands(InputStream in, OutputStream out)
            throws IOException, InterruptedException, IllegalAccessException, InstantiationException, ResponseException, NonNumericResponseException {
        for (ObdCommand command : commands)
            command.run(in, out);
    }

    /**
     * <p>getFormattedResult.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFormattedResult() {
        StringBuilder res = new StringBuilder();
        for (ObdCommand command : commands)
            res.append(command.getFormattedResult()).append(",");

        return res.toString();
    }

}
