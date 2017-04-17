package com.finebi.common.log;
/**
 * This class created on 2017/4/13.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BIUserLogPool {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BIUserLogPool.class);

    private static class SingletonHolder {
        /**
         * 单例对象实例
         */
        static final BIUserLogPool INSTANCE = new BIUserLogPool();
    }

    private static int BOTTOM = 5000;
    private static int CAPACITY = 10000;
    private List<BIUserLog4jEvent> eventPool = Collections.synchronizedList(new ArrayList<BIUserLog4jEvent>());

    public static BIUserLogPool getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public void append(LoggingEvent event) {
        if (eventPool.size() >= CAPACITY) {
            while (eventPool.size() > BOTTOM) {
                eventPool.remove(0);
            }
        }
        eventPool.add(new BIUserLog4jEvent(event));
    }

    public List<BIUserLog4jEvent> getLogEventPool() {
        return new ArrayList<BIUserLog4jEvent>(eventPool);
    }

    public static int getBOTTOM() {
        return BOTTOM;
    }

    public static int getCAPACITY() {
        return CAPACITY;
    }

    public void clear() {
        eventPool.clear();
    }
}
