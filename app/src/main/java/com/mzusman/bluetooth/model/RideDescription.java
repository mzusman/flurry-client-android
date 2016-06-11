package com.mzusman.bluetooth.model;

/**
 * Created by Asaf on 11/06/2016.
 */
public class RideDescription {
    private boolean sent;
    private String fileName;

    public RideDescription(boolean sent, String fileName) {
        this.sent = sent;
        this.fileName = fileName;
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
}
