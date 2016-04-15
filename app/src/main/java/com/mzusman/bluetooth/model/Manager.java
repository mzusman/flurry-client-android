package com.mzusman.bluetooth.model;

import java.util.List;

/**
 * Created by zusmanmo on 15/04/2016.
 */
public interface Manager {
    public void connect();
    public List<String> getReadings(int READINGS);
    public void stop();
    public String getReading(int READ);
//    public void connect();
}
