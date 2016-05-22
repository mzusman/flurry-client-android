
package com.mzusman.bluetooth.exceptions;
/**
 * Thrown when there is a "?" message.
 *
 * @author pires
 * @version $Id: $Id
 */
public class MisunderstoodCommandException extends ResponseException {

    /**
     * <p>Constructor for MisunderstoodCommandException.</p>
     */
    public MisunderstoodCommandException() {
        super("?");
    }

}
