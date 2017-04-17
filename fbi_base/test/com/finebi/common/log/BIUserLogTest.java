package com.finebi.common.log;
/**
 * This class created on 2017/4/13.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import junit.framework.TestCase;
import org.apache.log4j.PropertyConfigurator;

import java.io.ByteArrayInputStream;

public class BIUserLogTest extends TestCase {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(BIUserLogTest.class);

    static {
        String str = "log4j.rootLogger=INFO, ServerDailyRollingFile\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile=com.finebi.common.log.BIUserLogAppender\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile.layout=org.apache.log4j.PatternLayout\n" +
                "\n" +
                "log4j.appender.ServerDailyRollingFile.layout.ConversionPattern=%d - %m%n\n";

        PropertyConfigurator.configure(new ByteArrayInputStream(str.getBytes()));

    }

    /**
     * Detail:
     * Author:Connery
     * Date:2017/4/13
     */
    public void testLogEventAppend() {
        try {
            BIUserLogPool logPool = BIUserLogPool.getInstance();
            LOGGER.info("testLogger");
            assertEquals(logPool.getLogEventPool().size(), 1);
            LOGGER.info("testLogger2");
            assertEquals(logPool.getLogEventPool().size(), 2);

            assertEquals(logPool.getLogEventPool().get(0).getEvent().getMessage(), "testLogger");
            assertEquals(logPool.getLogEventPool().get(1).getEvent().getMessage(), "testLogger2");

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2017/4/13
     */
    public void testCapacity() {
        try {
            BIUserLogPool logPool = BIUserLogPool.getInstance();
            logPool.clear();
            for (int i = 0; i < BIUserLogPool.getBOTTOM(); i++) {
                LOGGER.info("testLogger");
            }
            assertEquals(logPool.getLogEventPool().size(), BIUserLogPool.getBOTTOM());
            LOGGER.info("testLogger");
            LOGGER.info("testLogger");

            assertEquals(logPool.getLogEventPool().size(), BIUserLogPool.getBOTTOM() + 2);
            logPool.clear();
            for (int i = 0; i < BIUserLogPool.getCAPACITY(); i++) {
                LOGGER.info("testLogger");
            }
            assertEquals(logPool.getLogEventPool().size(), BIUserLogPool.getCAPACITY());
            LOGGER.info("testLogger");
            assertEquals(logPool.getLogEventPool().size(), BIUserLogPool.getBOTTOM() + 1);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail();
        }
    }


}
