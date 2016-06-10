package com.mzusman.bluetooth.model.Managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzeus on 6/10/16.
 */
public interface Readable {
    List<String> getReadings() throws IOException;

    String getReading(String READ);
}
