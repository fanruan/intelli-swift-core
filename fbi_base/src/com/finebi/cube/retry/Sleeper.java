package com.finebi.cube.retry;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sean on 2015/5/7.
 */
public class Sleeper {
    public void sleep(long timeOut, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeOut);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
