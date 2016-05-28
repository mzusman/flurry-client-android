
package com.mzusman.bluetooth.commands.control;

import com.mzusman.bluetooth.commands.ObdCommand;
import com.mzusman.bluetooth.commands.SystemOfUnits;
import com.mzusman.bluetooth.enums.AvailableCommandNames;

/**
 * <p>DistanceMILOnCommand class.</p>
 *
 * @author pires
 * @version $Id: $Id
 */
public class DistanceMILOnCommand extends ObdCommand
        implements SystemOfUnits {

    private int km = 0;

    /**
     * Default ctor.
     */
    public DistanceMILOnCommand() {
        super("01 21");
    }

    /**
     * Copy ctor.
     */
    public DistanceMILOnCommand(
            DistanceMILOnCommand other) {
        super(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performCalculations() {
        // ignore first two bytes [01 31] of the response
        if (buffer.size() >= 4)
            km = buffer.get(2) * 256 + buffer.get(3);
    }

    /**
     * <p>getFormattedResult.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFormattedResult() {
        return useImperialUnits ? String.format("%.2f%s", getImperialUnit(), getResultUnit())
                : String.format("%d%s", km, getResultUnit());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCalculatedResult() {
        return useImperialUnits ? String.valueOf(getImperialUnit()) : String.valueOf(km);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResultUnit() {
        return useImperialUnits ? "m" : "km";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getImperialUnit() {
        return km * 0.621371192F;
    }

    /**
     * <p>Getter for the field <code>km</code>.</p>
     *
     * @return a int.
     */
    public int getKm() {
        return km;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return AvailableCommandNames.DISTANCE_TRAVELED_MIL_ON
                .getValue();
    }

}
