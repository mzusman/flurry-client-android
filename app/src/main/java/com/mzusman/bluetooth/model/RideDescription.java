package com.mzusman.bluetooth.model;

/**
 * Created by Asaf on 11/06/2016.
 */
public class RideDescription {
    private String id;
    private boolean sent;
    private String fileName;

    public RideDescription(String id, boolean sent, String fileName) {
        this.id = id;
        this.sent = sent;
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
