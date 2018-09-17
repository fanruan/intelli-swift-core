package com.fr.swift.source.retry;

import com.fr.swift.log.SwiftLoggers;

import java.util.concurrent.TimeUnit;

/**
 * Created by FineSoft on 2015/5/7.
 */
public class Sleeper {
    public void sleep(long timeOut, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeOut);
        } catch (InterruptedException ex) {
             SwiftLoggers.getLogger().error(ex.getMessage(), ex);
        }
    }
}