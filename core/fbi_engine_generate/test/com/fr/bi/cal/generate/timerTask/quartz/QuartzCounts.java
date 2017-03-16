package com.fr.bi.cal.generate.timerTask.quartz;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kary on 2016/6/30.
 */
public class QuartzCounts {
    private static QuartzCounts ourInstance = new QuartzCounts();
    private Map<String, String> flag;
    public static QuartzCounts getInstance() {
        return ourInstance;
    }

    private QuartzCounts() {
        flag = new HashMap<String, String>();
    }

    public Map<String, String> getFlag() {
        return flag;
    }
}
