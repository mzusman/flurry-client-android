package com.mzusman.bluetooth.exceptions;

/*
 * Class : .
 * Created by mzusman - morzusman@gmail.com on 5/22/16.
 */
public class UnkownException extends ResponseException {
    protected UnkownException(String message) {
        super("ERROR");
    }
}
