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

public class BIUserLog4jEvent {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BIUserLog4jEvent.class);
    private LoggingEvent event;

    public BIUserLog4jEvent(LoggingEvent event) {
        this.event = event;
    }

    public LoggingEvent getEvent() {
        return event;
    }
}
