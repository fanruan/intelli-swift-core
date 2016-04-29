package com.fr.bi.cluster.retry;

import com.fr.bi.stable.utils.code.BILogger;

import java.util.concurrent.TimeUnit;

/**
 * Created by FineSoft on 2015/5/7.
 */
public class Sleeper {
    public void sleep(long timeOut, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeOut);
        } catch (InterruptedException ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }
}