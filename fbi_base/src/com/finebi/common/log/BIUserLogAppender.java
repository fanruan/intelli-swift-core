package com.finebi.common.log;
/**
 * This class created on 2017/4/13.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.List;

public class BIUserLogAppender extends AppenderSkeleton {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BIUserLogAppender.class);
    protected BIUserLogPool logPool = BIUserLogPool.getInstance();

    public void close() {
        if (this.closed)
            return;
        this.closed = true;
    }


    public boolean requiresLayout() {
        return true;
    }

    @Override
    protected void append(LoggingEvent event) {
        logPool.append(event);
    }

    public List<BIUserLog4jEvent> getLogEventPool() {
        return logPool.getLogEventPool();
    }
}
