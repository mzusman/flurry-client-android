package com.mzusman.bluetooth.model;

/**
 * Created by Asaf on 11/06/2016.
 */
public class RideDescription {
    private boolean sent;
    private String fileName;
    private String driverID;

    public RideDescription(boolean sent, String fileName, String driverID) {
        this.sent = sent;
        this.fileName = fileName;
        this.driverID = driverID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "RideDescription{" +
                "sent=" + sent +
                ", fileName='" + fileName + '\'' +
                ", driverID='" + driverID + '\'' +
                '}';
    }
}
