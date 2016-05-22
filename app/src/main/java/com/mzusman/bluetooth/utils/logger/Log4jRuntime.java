package com.mzusman.bluetooth.utils.logger;

import org.apache.log4j.Logger;

/*
 * Class : .
 * Created by mzusman - morzusman@gmail.com on 5/21/16.
 */
public class Log4jRuntime implements Thread.UncaughtExceptionHandler {
    private static Logger log = Log4jHelper.getLogger("Runtime");


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        log.error("Runtime Error" + thread.getName(), ex);
        System.exit(1);
    }
}
