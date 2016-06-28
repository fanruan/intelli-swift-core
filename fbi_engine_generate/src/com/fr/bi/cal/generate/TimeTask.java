package com.fr.bi.cal.generate;

/**
 * Created by Kary on 2016/6/28.
 */
public class TimeTask {
    private static TimeTask ourInstance = new TimeTask();

    public static TimeTask getInstance() {
        return ourInstance;
    }

    private TimeTask() {
    }
}
